# 7. 병렬 데이터 처리와 성능


### 지난 장 요약
- stream 인터페이스를 통해 데이터 collection을 선언형으로 제어하는 방법들
- 또, 외부 반복을 내부 반복으로 바꾸면 Native Java Library가 stream 요소의 처리를 제어할 수 있음<br>
  > 👉🏻 Java 개발자는 collection 데이터 처리 속도를 높이려고 따로 고민할 필요가 없음!
- 컴퓨터의 멀티코어를 활용해서 파이프라인 연산을 실행할 수 있음⭐⭐ (가장 중요!)

<br>

### Java 7 이전
: 데이터 컬렉션을 병렬로 처리하기 어려웠음
  - step 1. 데이터를 서브파트로 분할함
  - step 2. 분할된 서브파트를 각각의 스레드로 할당함
  - step 3. 의도치 않은 race condition이 발생하지 않도록 적절한 동기화를 추가함
  - step 4. 부분 결과를 합침

<br>

### Java 7 이후
- fork/join framework 제공
  - 더 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있는 기능

<br>

### Stream을 쓰면 얼마나 병렬 실행을 쉽게 할 수 있는지?
- stream → 순차 스트림을 병렬 스트림으로 자연스럽게 바꿀 수 있음!
- 병렬 스트림의 내부적 처리 과정을 알아야만 → 스트림을 잘못 사용하는 상황을 피할 수 있음
  - 내부적 원리 : 병렬 스트림이 요소를 여러 청크로 분할하는 것부터 시작
  - Custom Spliterator를 직접 구현 → 분할 과정을 직접 제어 가능

<br><br>

# 7.1 병렬 스트림
- Stream 인터페이스 : 간단히 요소를 병렬로 처리할 수 있음
- Collection에 parallelStream을 호출 → 병렬 스트림(parallel stream) 생성됨

### parallel stream
: 병렬 스트림
- 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림

### 병렬 스트림 활용하기
- 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있음

**예시 : 1 ~n 까지의 모든 숫자의 합계를 반환하는 메서드** 
1. 스트림으로 투박하게 구현하는 방법
    - step 1) 무한 스트림을 만듦
    - step 2) 주어진 크기로 스트림을 제한함
    - step 3) 두 숫자를 더하는 BinaryOperator로 리듀싱 작업 수행
  ```java
  public long sequentialSum(long n) { 
	  return Stream.iterate(1L, i -> i + 1)
	  	       .limit(n) 
	  	       .reduce(0Lz Long::sum);
  ```

2. 전통적인 자바에서 반복문으로 구현하는 방법
```java
public long iterativeSum(long n) { 
	long result = 0; 
	for (long i = 1L; i <= n; i++) { 
		result += i; 
	} 
	return result; 
}
```

이럴 경우, n이 커진다면 이 연산을 병렬로 처리하는 것이 더욱 좋다.
- 결과 변수는 어떻게 동기화할 것인가?
- 몇 개의 스레드를 사용해야 할 것인가?
- 숫자는 어떻게 생성할 것인가?
- 생성된 숫자는 누가 더할 것인가?

>👉🏻 **병렬 스트림을 사용하면 위의 문제를 모두 쉽게 해결할 수 있다.**

<br><br>

## 7.1.1 순차 스트림 → 병렬 스트림

### `parallel`
- 순차 스트림 내에 parallel 호출하면, 기존의 함수형 **리듀싱 연산(숫자 합계 계산)이 병렬로 처리됨**

```java
public long parallelSum(long n) {
	return Stream.iterate(1L, i -> i + 1)
		     .limit(n)
		     .parallel()   // 스트림을 병렬 스트림으로 변환
		     .reduce(0L, Long::sum); 
}
```

- 스트림이 여러 청크로 분할되어 처리됨
    → 여러 청크에 병렬로 수행
    → 리듀싱 연산으로 생성된 부분 결과를 다시 리듀싱 연산으로 합침
    → 전체 스트림의 리듀싱 결과를 도출
    
    ![Pasted image 20230524201915](https://github.com/deingvelop/modern-java-in-action/assets/100582309/29144206-7bb6-4114-ba73-a335f69f4906)

- parallel을 호출해도 스트림 자체에는 아무런 변화도 일어나지 않음
- 원리
  - 내부적으로, 이후 연산이 병렬로 수행해야 함을 의미하는 boolean 플래그가 설정됨

<br>

> 💡 **병렬 스트림에서 사용하는 스레드 풀 설정**<br>
> **Q.** 스트림의 parallel 메서드로 병렬 작업 수행하는 스레드는 어디서, 어떻게, 몇 개나 생성되는가?<br>
> **A.** <br>
> - 병렬 스트림은 내부적으로 `ForkJoinPool<br>`을 사용한다(포크/조인 프레임워크는 7.2절에서 자세히 설명한다). 
> - 기본적으로 `ForkJoinPool`은 프로세서 수, 즉 `Runtime.getRuntime().availableProcessors()`가 반환하는 값에 상응하는 스레드를 가진다.
> - 만약 `ForkJoinPool`의 `property`값을 바꿔 설정하고 싶다면?
>   ```java
>   System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
>   ```
>   - 위의 예시는 전역 설정 코드이므로 이후의 모든 병렬 스트림 연산에 영향을 준다. 즉, 현재는 하나의 병렬 스트림에 사용할 수 있는 특정한 값을 지정할 수 없다. 일반적으로 기기의 프로세서 수와 같으므로 특별한 이유가 없다면 `ForkJoinPool`의 기본값을 그대로 사용할 것을 권장한다.

<br>

### `sequential`
- sequential을 사용하면 병렬스트림 → 순차스트림으로 전환 가능

> 👉🏻 두 메서드를 이용하면, 어떤 연산을 병렬로 실행하고 어떤 연산을 순차로 실행할지 제어할 수 있음!

### parallel과 sequential 사용하기
- 동시에 쓰면?
  - 두 메서드 중 최종적으로 호출된 메서드가 전체 파이프라인에 영향을 미친다.
    ```java
	stream.para1lel()
	      .filter(...)
	      .sequential()
	      .map()
	      .parallel()
	      .reduce();
	```
  - 이 예제에서 파이프라인의 마지막 호출은 parallel이므로 파이프라인은 전체적으로 병렬로 실행된다.

<br><br>

## 7.1.2 스트림 성능 측정

### Java Microbenchmark Harness
**(JMH, 자바 마이크로벤치마크 하니스)**
- Java Library
- 간단하고, 어노테이션 기반 방식을 지원함
- 안정적으로 자바 프로그램이나 JVM을 대상으로 하는 다른 언어용 벤치마크를 구현할 수 있음

#### 용어 설명
> **Benchmark**
> - 전자기기의 연산성능을 시험하여 수치화하는 것
> - 특히 전산용어로써 벤치마크는 여러 가지 전자기기의 성능을 비교 평가하는 의미

> **HotSpot**
> - 데스크톱과 서버 컴퓨터를 위한 JVM
> - 

### JMH 활용하기

**환경 설정**

- Maven : pom.xml 파일(빌드 과정 정의)에 몇 가지 dependency를 추가하여 사용 가능
	```xml
	<!-- 핵심 JMH 구현 -->
	<dependency>
		<groupId>org.openjdk.jmh</groupld>
		<artifactId>jmh-core</artifactId>
		<version>1.17.4</version>
	</dependency>
	<!-- JAR 파일을 만드는 데 도움을 주는 어노테이션 프로세서 -->
	<dependency>
		<groupId>org.openjdk.jmh</groupld>
		<artifactId>jmh-generator-annprocess</artifactId>
		<version>1.17.4</version>
	</dependency>
	```
- 다음 플러그인도 추가하면 자바 아카이브 파일을 이용해서 벤치마크를 편리하게 실행할 수 있다.
  ```xml
  <build>
	  <plugins>
		  <plugins>
			  <groupId>org.apache.maven.plugins</groupId>
			  <artifactId>maven-shade-plugin</artifactId>
			  <executions>
				  <execution>
					  <phase>package</phase>
					  <goals><goal>shade</goal></goals>
					  <configuration>
						  <finalName>benchmarks</finalName>
						  <transformers>
							  <transformer implementation= "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
								<mainClass>org.openjdk.jmh.Main</mainClass>
							  </transformer>
						   </transformers>
					   </configuration>
				   </execution>
			   </executions>
		   <plugin>
	   </plugins>
   <build>
  ```

<br>

### sequentialSum 메서드 간단히 벤치마크하기

```java
@BenchmarkMode(Mode.AverageTime)    // 벤치마크 대상 메서드를 실행하는 데 걸린 평균 시간 측정 
@OutputTimeUnit(TimeUnit.MILLISECONDS)     // 벤치마크 결과를 밀리초 단위로 출력 
@Fork(2, jvmArgs={"-Xms4G", "-Xmx4G"})     // 4Gb의 힙공간을 제공한 환경에서 두 번 벤치마크를 수행해 신뢰성 확보
public class ParallelStreamBenchmark { 
	private static final long N = 10_000_000L;
	
	@Benchmark    // 벤치마크 대상 메서드 
	public long sequentialSum() {
		return Stream.iterate(1L, i -> i + 1).limit(N)
					  .reduce(0L, Long::sum);
	} 
	
	@TearDown(Level.Invocation)    // 매 번 벤치마크를 실행한 다음에는 가비지컬렉터 동작 시도
	public void tearDown() { 
		System.gc(); 
	}
}
```
- 벤치마크가 가능한 한 가비지 컬렉터의 영향을 받지 않도록 힙의 크기를 충분하게 설정
- 벤치마크가 끝날 때마다 가비지 컬렉터가 실행되도록 강제
  > 💡 이렇게 주의를 들여 설정했지만, 여전히 결과는 정확하지 않을 수 있다. <br>기계가 지원하는 코어의 갯수 등이 실행 시간에 영향을 미칠 수 있기 때문!

- 위의 클래스를 컴파일하면, maven plugin이 `benchmarks.jar`라는 두 번째 파일을 만듦
  - jar 파일 실행 방법
  ```java
  java -jar ./target/benchmarks.jar ParallelStreamBenchmark
  ```
  - JMH 명령이 계산하는 과정
    - 핫스팟이 코드를 최적화 할 수 있도록 20 번을 실행 → 벤치 마크를 준비한 → 20번을 더 실행 → 최종 결과를 계산
- 실행 결과
  ![Pasted image 20230525135036](https://github.com/deingvelop/modern-java-in-action/assets/100582309/4efa9c9f-da51-48b2-a5dc-4055b9a55d15)

<br>

### for 루프 vs stream 병렬 처리
- for 루프
	```java
	@Benchmark 
	public long iterativeSum() { 
		long  result = 0; 
		for (long i = 1L; i <= N; i++) {
			result += i; 
		}
		return result; 
	}
	```
  - 결과
  ![Pasted image 20230525135445](https://github.com/deingvelop/modern-java-in-action/assets/100582309/fc4a808a-ec6a-45b6-a337-3b1465564c9b)


   
- 병렬 stream
  ![Pasted image 20230525135612](https://github.com/deingvelop/modern-java-in-action/assets/100582309/7cbdaae4-acb5-49f7-bf0e-4591cc4b7c93)



> 💡 **병렬 처리가 무조건 빠르진 않다!**
> - 병렬 버전이 쿼드 코어 CPU를 활용하지 못하고 순차 버전에 비해 다섯 배나 느린 실망스러운 결과가 나왔다. 두 가지 문제를 발견할 수 있다.
> - 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야 한다.
> - 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.

<br>

###  반복 작업을 병렬 수행 단위로 나누기
- 이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에, 청크로 분할하기 어렵기 때문!
  ![Pasted image 20230525140025](https://github.com/deingvelop/modern-java-in-action/assets/100582309/30f6108f-fbdb-4709-b3f8-d7a2bf09e78c)

- 리듀싱 연산이 수행되지 않음 - 리듀싱 과정을 시작하는 시점에 전체 숫자 리스트가 준비되지 않았기 때문에 → 청크로 분할할 수 없음
- 오히려 순차처리 방식과 크게 다른 점이 없는데 **스레드를 할당하는 오버헤드만 증가하게 됨**

> - 병렬 프로그래밍을 오용(예를 들어 병렬과 거리가 먼 반복 작업)하면 오히려 전체 프로그램의 성능 이 더 나빠질 수도 있다. 
> - 따라서 `parallel` 메서드를 호출했을 때 내부적으로 어떤 일이 일어나는지 꼭 이해해야 함!

<br>

### 병렬 실행에 특화된 메서드 사용

#### `LongStream.rangeClosed`
- 기본형 long을 직접 사용 → 박싱과 언박싱 오버헤드가 사라짐
- 쉽게 청크로 분할할 수 있는 숫자 범위를 생산 (ex: 1-20을 각각 1-5, 6-10,11-15,16-20 범위의 숫자로 분할)

- 순차 스트림 측정
  ```java
  @Benchmark public long rangedSum() { 
	  return LongStream.rangeClosed(1, N)
					   .reduce(0L, Long::sum);
  }
  ```

- 출력 결과
  ![Pasted image 20230525140813](https://github.com/deingvelop/modern-java-in-action/assets/100582309/68cd3190-ab6f-4d90-8b64-6c43a87338be)


- 이렇게 특화된 메서드를 활용한 처리 속도가 더 빠름!
  - 특화되지 않은 스트림 : 오토박싱, 언박싱 등의 오버헤드를 수반하기 때문
   > 상황에 따라서는 어떤 알고리즘을 병렬화하는 것보다 적절한 자료구조를 선택하는 것이 더 중요하다는 사실을 단적으로 보여줌
   

- 병렬 스트림을 적용하면? → 순차 실행보다 빠른 성능!
```java
@Benchmark 
public long parallelRangedSum() { 
	return LongStream.rangeClosed(1, N) 
					 .parallel()
					 .reduce(0L, Long::sum); 
}
```
  - 결과
  ![Pasted image 20230525141112](https://github.com/deingvelop/modern-java-in-action/assets/100582309/783100f7-4bdc-4f51-8921-bd349797d36e)


  > 올바른 자료구조를 선택해야 병렬 실행도 최적의 성능을 발휘할 수 있다 는 사실!

<br>

### 병렬화 이용시 유의사항
> 병렬화가 완전 공짜는 아니다. 

병렬화를 이용하려면?
- 스트림을 재귀적으로 분할해야 함
- 각 서브스트림을 서로 다른 스레드의 리듀싱 연산으로 할당하고, 이들 결과를 하나의 값으로 합쳐야 함
- 멀티코어 간의 데이터 이동은 우리 생각보다 비싸다. <br>따라서 코어 간에 데이터 전송 시간보다 훨씬 오래 걸리는 작업만 병렬로 다른 코어에서 수행하는 것이 바람직!
- 또한 상황에 따라 쉽게 병렬화를 이용할 수 있거나 아니면 아예 병렬화를 이용할 수 없는 때도 있음
- 그리고 스트림을 병렬화해서 코드 실행 속도를 빠르게 하고 싶으면 항상 병렬화를 올바르게 사용하고 있는지 확인해야 함!!

<br>

## 7.1.3 병렬 스트림의 올바른 사용법

### 자주 일어나는 실수
- 병렬 스트림을 사용하며, 많은 실수는 공유된 상태를 바꾸는 알고리즘을 사용하기 때문에 일어난다. 
- ex) n까지의 자연수를 더하면서 공유된 누적자를 바꾸는 프로그램
  ```java
  public long sideEffeetSum(long n) { 
	  Accumulator accumulator = new Accumulator()
	  LongStream.rangeClosed(1, n)
	            .forEach(accumulator::add);
	  return accumulator.total; 
  } 
  
  public class Accumulator { 
	  public long total = 0; 
	  public void add(long value) { 
		  total += value; 
	  } 
  }
  ```
  - 이 코드는 순차 실행하도록 구현되어있음
  - 따라서, 병렬로 실행하면 참사가 일어남
    - total을 접근할 때마다 (다수의 스레드에서 동시에 데이터에 접근하는) 데이터 레이스 문제가 일어남
    - ⇒ 동기화로 문제를 해결 → 결국 병렬화라는 특성이 없어져 버림<br><br>
  - 위의 스트림을 병렬로 만들어보면??
  ```java
  public long sideEffectParallelSum(long n) {
	  Accumulator accumulator = new Accumulator();
	  LongStream.rangeClosed(1, n)
			  .parallel()
			  .forEach(accumulator::add); 
	  return accumulator.total; 
  }
  ```
  - 위에서 소개한 하니스로 실행한 결과를 출력하기
  ```java
  System.out.println("SideEffeet parallel sum done in: " + measurePerf(Parallelstreams::sideEffectParallelSum, 10_000_000L) + " msecs" );
  ```
  - 결과
    ![Pasted image 20230525182825](https://github.com/deingvelop/modern-java-in-action/assets/100582309/3b9c2c17-e497-46fd-8efa-bb70ba50c9a4)

  - 일단, 성능보다도 올바른 결과값이 나오지 않음
    - 여러 스레드에서 동시에 누적자, 즉 `total += value`를 실행하면서 이런 문제가 발생!
    - `total += value`는 아토믹 연산이 아님!
    - 즉, 여러 스레드에서 공유하는 객체의 상태를 바꾸는 `forEach` 블록 내부에서 `add` 메서드를 호출하면서 이 같은 문제가 발생

> 💡 **Tip!**<br>
> 상태 변화를 피하는 방법은 18, 19장에서 설명한다. <br>
> 우선은 병렬 스트림이 올바로 동작하려면 공유된 가변 상태를 피해야 한다는 사실만 기억하자.

<br><br>

## 7.1.4 병렬 스트림 사용하여 성능 개선하기

- 양을 기준으로 병렬 스트림 사용을 결정하는 것은 바람직하지 못함 (ex: 1,000개 이상의 요소일 경우 등)

#### 규칙
- 확신이 서지 않으면 **직접 성능을 측정**하라!
  - 순차 스트림을 병렬스트림으로 쉽게 바꿀 수는 있지만, 병렬 스트림으로 바꾸는 것만이 능사는 아님
  - 언제나 병렬 스트림이 순차 스트림보다 빠른 것은 아님
  - 또, 병렬 스트림의 수행 과정은 투명하지 않을 때가 많음<br><br>
- **박싱을 주의**하라
  - 자동 박싱과 언박싱은 성능을 크게 저하시킬 수 있는 요소
  - Java 8 : 박싱 동작을 피할 수 있도록 기본형 특화 스트림 (IntStream，LongStream, DoubleStream)을 제공함
  - 따라서 되도록이면 **기본형 특화 스트림을 사용하는 것이 좋다**.<br><br>
  - **순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다**. 
    - 특히 `limit`나 `findFirst`처럼 요소의 순서에 의존하는 연산을 병렬 스트림에서 수행하려면 비싼 비용을 치러야 함
      - ex) `findAny`는 요소의 순서와 상관없이 연산하므로 `findFirst`보다 성능이 좋음
    - 정렬된 스트림에 `unordered`를 호출하면 비정렬된 스트림을 얻을 수 있음
    - 스트림에 N개 요소가 있을 때 요소의 순서가 상관없다면(ex: List) 비정렬된 스트림에 `limit`를 호출하는 것이 더 효율적이다<br><br>
    - 스트림에서 수행하는 **전체 파이프라인 연산 비용을 고려**하라. 
      - 처리해야 할 요소 수가 `N`이고 하나의 요소를 처리하는 데 드는 비용을 `Q`라 하면 → 전체 스트림 파이프라인 처리 비용을 `N*Q`로 예상할 수 있다. 
      - `Q`가 높아진다는 것은 병렬 스트림으로 성능을 개선할 수 있는 가능성이 있음을 의미한다.<br><br>
    - **소량의 데이터에서는 병렬 스트림이 도움 되지 않는다.** 
      - 소량의 데이터를 처리하는 상황에서는 병렬화 과정에서 생기는 부가 비용을 상쇄할 수 있을 만큼의 이득을 얻지 못하기 때문이다.<br><br>
  - **스트림을 구성하는 자료구조**가 적절한지 확인하라.
    - ex) `ArrayList`를 `LinkedList`보다 효율적으로 분할할 수 있다. 
      - `LinkedList`를 분할하려면 모든 요소를 탐색해야 하지만 `ArrayList`는 요소를 탐색하지 않고도 리스트를 분할할 수 있기 때문이다. 
      - 또한 `range` 팩토리 메서드로 만든 기본형 스트림도 쉽게 분해할 수 있다. 
      - 마지막으로 7.3절에서 설명 하는 것처럼 커스텀 `Spliterator`를 구현해서 분해 과정을 완벽하게 제어할 수 있다.<br><br>
  - **스트림의 특성**과 **파이프라인의 중간 연산**이 스트림의 특성을 어떻게 바꾸는지에 따라 **분해 과정의 성능이 달라질 수 있다.**
    - 예를 들어 SIZED 스트림은 정확히 같은 크기의 두 스트림으로 분할할 수 있으므로 효과적으로 스트림을 병렬 처리할 수 있다. 
    - 반면 필터 연산이 있으면 스트림의 길이를 예측할 수 없으므로 효과적으로 스트림을 병렬 처리할 수 있을지 알 수 없게 된다.<br><br>
    - **최종 연산의 병합 과정 비용**을 살펴보라. 
      - ex) `Collector`의 `combiner` 메서드
      - 병합 과정의 비용이 비싸다면 병렬 스트림으로 얻은 성능의 이익이 서브스트림의 부분 결과를 합치는 과정에서 상쇄될 수 있다
      - ![Pasted image 20230525185610](https://github.com/deingvelop/modern-java-in-action/assets/100582309/dd3d2eec-e8ec-4186-895e-ea574cd9e9bf)
<br><br>
    - 병렬 스트림이 수행되는 **내부 인프라구조**도 살펴봐야 한다. 
      - Java 7에서 추가된 fork/join Framework로 병렬 스트림이 처리된다. 
      - 병렬 스트림을 제대로 사용하려면 병렬 스트림의 내부 구조를 잘 알아야 한다.
  
