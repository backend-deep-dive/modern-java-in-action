
## 5.8.1 - 값으로 스트림 만들기

임의의 수를 인수로 받는 정적 메서드 Stream.of 를 이용해서 스트림을 만들 수 있다.

```java
Stream<String> stream = Stream.of("Modern ", "Java ", "In ", "Action ");
// 임의의 문자를 받아 Stream<String> 을 만들었다.
stream.map(String::toUpperCase).forEach(System.out::println);

MODERN 
JAVA 
IN 
ACTION
```

empty 메서드를 이용해서 스트림을 비울 수 있다.

```java
Stream<String> emptySteam = Stream.empty();
```

---

## 5.8.2 - null이 될 수 있는 객체로 스트림 만들기

자바 9에서는 null이 될 수 있는 개체를 스트림을 만들 수 있는 새로운 메소드가 추가되었다.
예를 들어 System.getproperty는 제공된 키에 대응하는 속성이 없으면
이전에는 null을 명시적으로 확인해야 했다.

```java
String homeValue = System.getProperty("home");  
Stream<String> homeValueStream  
= homeValue == null ? Stream.empty() : Stream.of(value);
```

Stream.ofNullable을 이용해 다음처럼 코드를 구현할 수 있다.

```java
Stream<String> homeValueStream = 
	Stream.ofNullable(System.getProperty("home"));
```

null이 될 수 있는 객체를 포함하는 스트림값을 flatMap 과 함께 사용하는 상황에서는 이 패턴을 
더 유용하게 사용할 수 있다.

```java
Stream<String> values =  
	Stream.of("config", "home", "user")  
		.flatMap(key -> Stream.ofNullable(System.getProperty(key)));
```

---

## 5.8.3 - 배열로 스트림 만들기

배열을 인수로 받는 정적 메서드 Arrays.stream 을 이용해서 스트림을 만들 수 있다.

```java
int[] numbers = {2,3,5,7,11,13};
int sum = Arrays.stream(numbers).sum(); 
// Arrays.stream() 은 int, long, double 을 지원한다.
// IntSteam 인터페이스에서 제공하는 기본메서드 sum

Answer : 41
```

---

## 5.8.4 - 파일로 스트림 만들기

파일을 처리하는 등의 I/O 연산에 사용하는 자바의 NIO API도 스트림 API를 활용할 수 있도록 업데이트 되었다.
java.nio.file.Files 의 많은 정적 메서드가 스트림을 반환한다.

```txt
Hello Spring!!
Nice to meet you!! Hello
For MAC
```

```java
long uniqueWords = 0;  
try(Stream<String> lines =  
// Files.lines 로 파일의 각 행 요소를 반환하는 스트림을 얻음, 
// (Java 가상머신의 Default Charset 을 가져옴)  
Files.lines(Paths.get("/Users/rio/Desktop/data.txt"), Charset.defaultCharset())) {  
uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))  
					.distinct()  
					.count();  
System.out.println(uniqueWords);  
} catch (IOException e) {  
	e.printStackTrace();  
}
```

스트림은 자원을 자동으로 해제할 수 있는 AutoCloseable 이므로 try-finally가 필요없다.
Stream 인터페이스는 AutoCloseable 인터페이스를 구현한다. 따라서 try/catch 블록 내의 자원은
자동으로 관리된다.

- IO 와 NIO의 선택
	- IO 선택 (BufferdInputStream, BufferedOutputStream)
		- 연결 클라이언트의 수가 적고,
		- 전송되는 데이터가 대용량이면서, (NIO 의 Buffer의 크기를 무한정 늘릴 수 없음)
		- 순차적으로 처리될 필요성이 있을 경우
		- 블로킹만 지원하기에 입출력 스트림의 메소드에 입출력이 일어나기 전까지 스레드가 대기상태가 됨.
		- 스레드가 블로킹되면 다른 일을 할 수가 없고 interrup 해서 블로킹을 빠져나올 수 없다.
	- NIO 선택
		- 연결 클라이언트의 수가 많고
		- 전송되는 데이터 용량이 적으면서,
		- 입출력 작업 처리가 빨리 끝나는 경우
		- 스레드를 interrup 함으로써 빠져나올 수 있다.
		- NIO는 넌블로킹을 지원하기에 입출력 작업시 스레드가 블로킹되지 않는다.

---

## 5.8.5 - 함수로 무한 스트림 만들기

스트림 API는 함수에서 스트림을 만들 수 있는 두 정적 메서드 Stream.iterate 와 Stream.generate 를 제공
두 연산을 이용해서 **무한 스트림**, 즉 크기가 고정되지 않은 스트림을 만들 수 있다.

#### iterate 메서드

```java
Stream<Integer> stream = Stream.iterate(0, n -> n + 2)  
								.limit(10);  
  
stream.forEach(System.out::println);
```

iterate  메서드는 초깃값과 람다를 인수로 받아서 새로운 값을 끊임없이 생산할 수 있다.
limit() 해주지 않는다면 무한으로 새로운 값을 만들어 낸다.
즉, **무한 스트림**이다. 

이러한 스트림을 **바운드 스트림**이라고 표현한다.

### Quiz 5-4 피보나치수열 집합 (iterate)

iterate로 피보나치수열 집합
(0,1), (1,1), (1,2), (2,3), (3,5), (5,8) ... 처럼 연속적인 숫자로 이루어진다.

```java
Stream.iterate(new int[]{0, 1},  
			t -> new int[]{t[1], t[0]+t[1]})  
			.limit(20)  
			.forEach(t -> System.out.println("(" + t[0] + "," + t[1] +")"));
```

iterate 의 초기값으로 {0, 1}을 지정해주고
iterate는 두번째 자리에 주어지는 람다를 연속으로 수행하기에
다음에 주어질 값을 지정해주면 된다.

자바 9의 iterate 메소드는 프레디케이트를 지원한다.
예를 들어 0에서 시작해서 100보다 크면 숫자 생성을 중단하는 코드를 다음처럼 구현할 수 있다.

```java
IntStream.iterate(0, n -> n < 100, n -> n + 4)  
		.forEach(System.out::println);
```

iterate 메소드는 두 번째 인수로 프레디케이트를 받아 언제까지 작업을 수행할 것인지의 기준으로 사용한다.

filter 동작으로는 언제 이 작업을 중단해야 하는지 알 수 없기 때문에 무한동작하게 된다. (Non-short-circuit)

<img width="483" alt="스크린샷 2023-05-08 오후 4 34 41" src="https://user-images.githubusercontent.com/96435200/236763747-28351d67-5603-44a5-a063-a506b8e72c36.png">

Short-circuit 을 지원하는 takeWhile 을 이용하는 것이 해법이다.

```java
IntStream.iterate(0, n -> n + 4)  
			.takeWhile(n -> n < 100)  
			.forEach(System.out::println);
```


#### generate 메서드

iterate 와 비슷하게 generate도 요구할 때 값을 계산하는 무한 스트림을 만들 수 있다.
하지만 iterate 와 달리 generate 는 생산된 각 값을 연속적으로 계산하지 않는다.

```java
Stream.generate(Math::random)  
			.limit(5)  
			.forEach(System.out::println);
```

여기서 limit 가 없다면 스트림은 언바운드 상태가 된다.

여기서 사용한 발행자 (Math.random) 은 상태가 없는 메서드, 즉 나중에 계산에 사용할 어떤 값도 저장해두지
않는다.

발행자에 상태가 있어도 되지만 병렬 코드에서는 발행자에 상태가 있으면 안전하지 않다.

IntStream 의 generate 메서드에서는 Suppier< T > 대신에 IntSupplier 를 인수로 받는다.

```java
IntStream twos = IntStream.generate(new IntSupplier() {  
	@Override  
	public int getAsInt() {  
	return 2;  
	}  
});
```

익명 클래스는 getAsInt 메서드의 연산을 커스터마이즈할 수 있는 상태 필드를 정의할 수 있다는 점이 다르다.
하지만 이렇게 상태를 바꾸게 되면 부작용이 생길 수 있다.

```java
IntSupplier fib = new IntSupplier() {  
  
private int previous = 0;  
private int current = 1;  
  
@Override  
public int getAsInt() {  
int oldPrevious = this.previous;  
int nextValue = this.previous + this.current;  
this.previous = this.current;  
this.current = nextValue;  
return oldPrevious;  
}  
};  
  
IntStream.generate(fib).limit(10).forEach(System.out::println);
```

getAsInt를 호출하면 인스턴스 변수에 어떤 피보나치 요소가 들어있는지 추적하므로 **가변 상태**의 객체이다
매번 객체 상태가 바뀌며 새로운 값을 생산한다.

iterate를 사용했을 때는 각 과정에서 새로운 값이 생성되면서도 기존 상태를 바꾸지 않는 **불변 상태**를
유지했다.

스트림을 병렬로 처리하면서 올바른 결과를 얻으려면 **불변 상태 기법**을 고수해야 한다.
또한 무한한 크기를 가진 스트림 처리를 하고 있기에 limit를 이용해 명시적으로 스트림의 크기를 제한해야 한다.
