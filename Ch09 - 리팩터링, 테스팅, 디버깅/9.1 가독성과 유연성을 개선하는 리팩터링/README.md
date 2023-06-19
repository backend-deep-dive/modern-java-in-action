# 9.1. 가독성과 유연성을 개선하는 리팩터링

### 더 간결한 코드를 만드는 방법
- 람다 표현식은 익명 클래스보다 코드를 간결하게 만듦
- 파라미터로 넣을 메서드가 이미 구현되어 있을 경우 -> 메서드 참조가 더 간결하게 만듦
- 람다 표현식은 동작 파라미터화 형식을 지원함 -> 더 큰 유연성을 갖출 수 있음 (다양한 요구사항 변화에 대응할 수 있도록 동작을 파라미터화함)

<br>

## 9.1.1. 코드 가독성 개선

> 코드 가독성이 좋다 = 코드를 다른 사람도 쉽게 이해할 수 있다.

- 코드의 문서화를 잘 해야 한다.
- 표준 코딩 규칙을 준수해야 한다.

<br>

## 9.1.2. 익명 클래스를 람다 표현식으로 리팩터링하기

- 하나의 추상 메서드를 구현하는 익명 클래스는 람다 표현식으로 리팩터링할 수 있다.
- 익명 클래스 : 코드를 장황하게 만들며, 쉽게 에러를 일으킴<br>
  → 람다 표현식을 이용하여 간결하고 가독성 좋은 코드를 구현할 수 있음

```java
// 익명 클래스를 이용한 코드
Runnable r1 = new Runnable() {
	public void run() {
		System.out.println("Hello");
	} 
};

// 람다 표현식을 이용한 코드
Runnable r2 = () -> System.out.println("Hello");
```


### 유의 사항
모든 익명 클래스를 람다 표현식으로 변환할 수 있는 것은 아니다.
1. 익명 클래스에서 사용한 this와 super는 람다 표현식에서 다른 의미를 갖는다. 
    (익명 클래스의 this = 자신, 람다 this = 람다를 감싸는 클래스)
2. 익명 클래스 : 감싸고 있는 클래스의 변수를 가릴 수 있지만(shadow), 람다 표현식에서는 불가능하다.
  ```java
  int a = 10;
  Runnable r1 = () -> {
	  int a = 2;    // 컴파일에러 
	  System.out.println(a);
  };
  
  Runnable r2 = new Runnable(){ 
	  public void run(){ 
		  int a = 2;     // 잘 작동한다. 
		  System.out.println(a); 
	  } 
  };
  ``` 
  
3. 익명 클래스를 람다 표현식으로 바꾸면, Context Overloading에 따른 모호함이 초래될 수 있다.
    - 익명 클래스 : 인스턴스화할 때 명시적으로 형식이 정해짐
    - 람다 : context에 따라 달라진다.
  ```java
  // Task라는 Runnable과 같은 시그니처를 갖는 함수형 인터페이스
  interface Task {
	  public void execute();
  }
  
  public static void doSomething(Runnable r) { r.run(); }
  public static void doSomething(Task a) { r.run(); }
  ```
  ```java
  
  // Task를 구현하는 익명 클래스
  doSomething(new Task() { 
	  public void execute() { 
		  System.out.println("Danger danger!!"); 
	  }
  });
  ```
  이럴 경우, 익명 클래스를 람다 표현식으로 바꾸면 메서드를 호출할 때 Runnable과 Task 모두 대상 형식이 될 수 있으므로 문제가 생긴다. 즉, 아래와 같다.
  ```java
  doSomething(() -> System.out.println("Danger danger!!"));    // doSomething(Runnable)과 doSomething(Task) 중 어느 것을 가리키는지 알 수 없는 모호함이 발생
  ```
  명시적 형변환(Task)를 이용해서 모호함을 제거할 수 있다.
  ```java
  doSomething((Task)() -> System.out.printin("Danger danger!!"));
  ```

<br>

## 9.1.3. 람다 표현식을 메서드 참조로 리팩터링하기

람다 표현식 대신 메서드 참조를 이용하면 가독성을 높일 수 있다. 메서드명으로 코드의 의도를 명확하게 알릴 수 있기 때문이다.

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
	menu.stream()
		.collect( 
			groupingBy(dish -> {
				if (dish.getCalories() <= 400) return CaloricLevel.DIET; 
				else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL; 
				else return CaloricLevel.FAT; 
			})
		);
```

Dish 클래스에 새로운 메서드를 추가하면 메서드 참조를 활용하여 위의 코드를 더 간단히 할 수 있다.

```java
// Dish 클래스
public class Dish{ 
	...
	
	public CaloricLevel getCaloricLevel() { 
		if (this.getCalories() <= 400) return CaloricLevel.DIET; 
		else if (this.getCalories() <= 700) return CaloricLevel.NORMAL; 
		else return CaloricLevel.FAT; 
	}
}
```

```java
// 람다 표현식을 메서드로 추출한 코드
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(groupingBy(Dish::getCaloricLevel));
```

### 정적 헬퍼 메서드

#### `comparing`

```java
// 리팩터링 전 : 비교 구현에 신경 써야 함
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

// 리팩터링 후 : 코드가 문제 자체를 설명한다.
inventory. sort(comparing (Apple::getWeight));0.0
```

- sum, maximum 등 자주 사용하는 리듀싱 연산 : 내장 헬퍼 메서드 제공
  - ex) 람다 표현식 + 저수준 리듀싱 연산보다 ⇒ Collectors API 가 더 코드의 의도가 명확함
  ```java
  // 저수준 리듀싱 연산을 조합한 코드
  int totalCalories = menu.stream().map(Dish::getCalories)
								   .reduce(0, (c1, c2) -> c1 + c2);
								   
  // 내장 컬렉터 이용 : 코드 자체로 문제를 더 명확히 설명할 수 있음 (ex: summingInt)
  int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
  ```

<br>

## 9.1.4. 명령형 데이터 처리를 스트림으로 리팩터링하기

- 이론적으로는, 반복자를 이용한 기존의 모든 컬렉션 처리 코드를 스트림 API로 바꿔야 한다.<br>
  → 데이터 처리 파이프라인의 의도를 더 명확히 보여주기 때문!

- stream의 장점
  - short-circuit
  - laziness
  - multicore architecture를 활용하기에 좋음


### 예시 1. 

**개선 전**
두 가지 패턴 (필터링, 추출)로 엉켜있는 코드. 가독성이 떨어지며, 병렬 실행이 매우 어렵다.
```java
List<String> dishNames = new ArrayList<>(); 
for(Dish dish: menu) { 
	if(dish.getCalories() > 300) { 
		dishNames.add(dish.getName()); 
	} 
}
```

**개선 후**
스트림 API를 이용하면 문제를 더 직접적으로 기술할 수 있을 뿐 아니라 쉽게 병렬화할 수 있다.

```java
menu.parallelStream() 
	.filter(d -> d.getCalories() > 300) 
	.map(Dish::getName) 
	.collect(toList());
```

> **💡 유의!**
> - 명령형 코드의 break, continue, return 등의 제어 흐름문을 모두 분석해서 같은 기능을 수행하느는 스트림 연산으로 유추해야 함<br>
> ⇒ 명령형 코드를 스트림 API로 바꾸는 것은 쉬운 일이 아님!<br>
> ⇒ 명령형 코드를 스트림 API로 바꾸도록 도와주는 몇 가지 도구가 있음 (ex: https://goo.gl/Mal5w(http://refactoring.info/tools/LambdaFicator)) (링크 작동 안 됨)

<br>


## 9.1.5. 코드 유연성 개선

- 람다 표현식을 이용하면, 동작 파라미터화(behaviour parameterization)를 쉽게 구현할 수 있음<br>
  ⇒ 다양한 람다를 전달해서 다양한 동작을 표현할 수 있음<br>
  ⇒ 변화하는 요구사항에 대응할 수 있는 코드를 구현할 수 있음 (ex: Predicate로 필터링 기능 구현, 비교자로 다양한 비교 기능 구현 등)

### 함수형 인터페이스 적용

- 람다 표현식을 이용하려면 함수형 인터페이스가 필요함 → 함수형 인터페이스를 코드에 추가해야 함
- 람다 표현식 리팩터링에 자주 사용하는 2가지 패턴 : conditional deferred execution(조건부 연기 실행), execute around(실행 어라운드)


<br>

### Conditional Deferred Execution (조건부 연기 실행)

- 실제 작업을 처리하는 코드 내부에 제어 흐름문이 복잡하게 얽힌 경우 활용 (흔히 보안 검사, 로깅 관련 코드)


#### 예시 : 내장 Java Logger 클래스 사용 예제

**개선 전**
```java
if (logger.isLoggable(Long.FINER)) {
  logger.finer("Problem: " + generateDiagnostic());
}
```
- 문제점들
  - logger의 상태가 isLoggable이라는 메서드에 의해 클라이언트 코드로 노출됨
  - 메시지를 로깅할 때마다 logger 객체의 상태를 매번 확인할 필요는 없음 → 코드를 어지럽힘

**1차 개선** : 내부 메서드로 숨기기
logger 객체를 내부적으로 확인하는 log() 메서드 생성
  ```java
  logger.log(Level.FINER, "Problem: " + generateDiagnostic());
  ```
- 개선된 점 및 문제점
  - 불필요한 if 문 제거
  - logger의 상태를 노출하지 않음
  - 단, 아직 인수로 전달된 메시지 수준에서 logger가 활성화되어 있지 않더라도 항상 로깅 메시지를 평가하게 된다는 문제 존재


**2차 개선 : 람다 활용**

- 특정 조건(Level.FINER)에서만 메시지가 생성될 수 있도록 메시지 생성 과정을 연기하도록 설정하기
```java
// 2차 개선 : 위에서 만든 log() 메서드를 람다를 활용하여 개선
// log() 메서드
public void log(Level level, Supplier msgSupplier) { 
	if(logger.isLoggable(level)){ 
		log (level, msgSupplier.get());
	}
}
```
```java
// log() 메서드 호출
logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic());
```

- log() 메서드는 logger의 수준이 적절하게 설정되어 있을 때만 인수로 넘겨진 람다를 내부적으로 실행함
- 만약 클라이언트 코드에서 객체 상태를 자주 확인하거나, 객체의 일부 메서드를 호출하는 상황이라면? → 내부적으로 객체의 상태를 확인한 다음에 호출하도록 새로운 메서드를 구현하는 것이 좋다. (람다, 메서드를 파라미터로 사용!)
- 효과 : 코드 가독성 ↑, 캡슐화 ↑ (객체ㅜ 상태가 클라이언트 코드로 노출되지 않음)

<br>

### Execute Around (실행 어라운드)

- 매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드 → 람다로 준비, 종료하는 과정을 처리하는 로직 재사용 → 코드 중복 줄임

```java
String oneLine = processFile((BufferedReader b) -> b.readLine());    // 람다 전달
String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());    // 다른 람다 전달

// IOException을 던질수 있는 람다의 함수형 인터페이스
public static String processFile(BufferedReaderProcessor p) throws IOException {
	try(BufferedReader br = new BufferedReader(new FileReader("ModernJavaInAction/chap9/data.txt"))) { 
		return p.process(br)    // 인수로 전달된 BufferedReaderProcessor 실행
	}
}  I  public interface BufferedReaderProcessor { String process(BufferedReader b) throws IOException; }
```

- 함수형 BufferReaderProcessor => 람다로 BufferedReader 객체의 동작을 결정할 수 있도록 해줌