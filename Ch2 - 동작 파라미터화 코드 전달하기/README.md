## 담당자

- 2.1 변화하는 요구사항에 대응하기 : 송민진
- 2.2 동적 파라미터화 : 김대현
- 2.3 복잡한 과정 간소화 : 홍승아
- 2.4 실전 예제
  - 1~2 : 이홍섭
  - 3~4 : 어정윤 

## 진행 날짜
- 2023년 4월 6일 (목요일)

## 통합 정리
> 👀 **들어가기 전에**

## Behavior Parameterization (동작 파라미터화)란?

: 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록

- 이 코드 블록은 나중에 프로그램에서 호출하며, 실행이 나중으로 미뤄짐<br>
  (나중에 실행될 메서드의 인수로 코드 블록을 전달할 수 있음)
- 결국 코드 블록에 따라 메서드의 동작이 파라미터화됨

#### 동작 파라미터화의 실생활 예시
- 룸메이트에게 이메일로 '우체국에 가서, 이 고객 번호를 사용하고, 관리자에게 이야기한 다음, 소포를 가져와라'라는 동작을 담은 내용을 전달한다.
- 그러면 룸메이트는 이메일을 확인한 후, 해당 내용을 인수로 받아 수행한다.

#### Q. 동작 파라미터화가 필요한 이유?
- 사용자 요구사항은 항상 바뀌기 때문!
- 변화하는 요구사항은 SW 엔지니어링에서 피할 수 없는 문제!

#### Q. 동작 파라미터화를 이용하면 장점은?
- **변화되는 요구사항**에 **엔지니어링적 비용을 가장 최소화하며 대응**할 수 있다.
- **새로 추가한 기능도 쉽게 구현**할 수 있다.
- 장기적인 관점에서 **유지 보수가 쉽다**.

<br>

# 2.1 변화하는 요구사항에 대응하기

## 변화하는 요구사항에 유연하게 대응할 수 있는 코드란?

다음은 사과 색을 정의하는 enum이다.
```java
enum Color { RED, GREEN }
```

<br>

### **요구사항 1**
: **녹색 사과만 찾아주세요.**

```java
public static List filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>(); // 사과 누적 리스트
	for (Apple apple : inventory) {
		if (GREEN.equals(apple.getColor()) { //  녹색 사과만 선택 
			result.add(apple);
		}
	} 
	return result; 
}
```

<br>

### **요구사항 2**
: **녹색 사과 + 어두운 녹색 사과, 빨간 사과, 어두운 빨간색 사과를 모두 찾아주세요.**

- 단순한 방법 : 메서드 코드를 복사해, if문의 조건만 수정한다.
  - 코드의 반복 발생 (DRY, Don't Repeat Yourself 원칙 위배)<br>
  - 더욱 다양한 변화되는 요구사항에는 적절히 대응 불가

- 이럴 때 적용하면 좋은 규칙!
  > ***`💡 Tip`***<br>
  > ***거의 비슷한 코드가 반복 존재한다면, 그 코드를 추상화한다.***

<br>

- 개선된 방법 : 색을 파라미터화할 수 있도록 메서드에 파라미터를 추가한다.<br>
	```java
	public static List filterApplesByColor(List<Apple> inventory, Color color) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple: inventory) { 
			if (apple.getColor().equals(color)) {
				result.add(apple);
			}
		}
		return result; 
	}
	```
  사용 방법
	```java
	List<Apple> greenApples = filterApplesByColor(inventory, GREEN); 
	List<Apple> redApples = filterApplesByColor(inventory, RED);
	```

<br>


### **요구사항 3**
: **초록색 사과도 따로 골라두고, 무게가 150g 이상인 사과도 따로 모두 찾아주세요.**

- 단순한 방법 : 무게 정보 파라미터로 필터링하는 메서드 한 개 추가하기
  ```java
  public static List filterApplesByWeight(List inventory, int weight) { 
    List<Apple> result = new ArrayList<>();
    for (Apple apple: inventory) {
      if (apple.getWeight() > weight) {
        result.add(apple);
      }
    }
    return result;
  }
  ```
  - 코드의 반복 발생
  - 성능 저하
  - 반복을 줄이기 위해서는, 한 줄이 아니라 메서드 전체 구현을 고쳐야 함! 즉, 엔지니어링적으로 비싼 대가를 치러야 함

<br>

- **개선 방법 1** (비추천) : 모든 속성을 메서드 파라미터로 추가하기
  - 단, 어떤 기준으로 사과를 필터링할지 구분하는 또 다른 방법 필요
  - 색이나 무게 중 어떤 것을 기준으로 필터링할지 가리키는 플래그 또 추가 필요

<br>

- **개선 방법 1-1** (비추천) : 여러 속성을 filter라는 메서드로 합치기
  ```java
  public static List filterApples(List<Apple> inventory, Color color, 
                int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple： inventory) {
      if ((flag && apple.getColor().equals(color)) ||     // 색이나 무게 선택 방법
        (!flag && apple.getWeight() > weight)) {        // 바람직 X
        result.add(apple);
      }
    }
    return result;
  }
  ```
  **사용 방법**
  ```java
  List<Apple> greenApples = filterApples(inventory, GREEN, 0, true); 
  List<Apple> heavyApples = filterApples(inventory, null, 150, false);
  ```
  - 형편 없는 코드
    - `true`/`false`의 불명확한 사용
  - 추가적인 요구사항에 유연하게 대응 불가
    - 여러 중복된 `filterApplesby...()`메서드를 만들어야 함
    - 혹은 모든 것을 처리하는 거대한 하나의 필터 메서드를 구현 해야 함

<br>

> 💡 ***To Be Continued...***<br>
> *위에는 조건들이 나름 잘 정의되어 있었지만, 문제가 잘 정의되어 있지 않은 상황이라면?<br>
> => 곤란해짐!*<br>
> *👉🏻 `filterApples()`에다가 분류 기준을 동작파라미터화를 이용해서 넣어보면 어떨까?`*
# 2.2 동작 파라미터화

참 또는 거짓을 반환하는 함수를 프레디케이트 (predicate) 라고 한다.

<https://www.cs.rochester.edu/u/nelson/courses/csc_173/predlogic/predicates.html#:~:text=A%20predicate%20is%20a%20boolean,a%20predicate%20with%20no%20arguments.>

사과를 인풋으로 받고 참, 거짓을 반환하는 ApplePredicate 인터페이스를 선언한다.

![](images/20230404202722.png)

이후 다양한 버전의 ApplePredicate을 구현하면서 코어 로직과 필터링 로직을 분리할 수 있다.

![](images/20230404202854.png)

이를 전략 디자인 패턴이라고 한다.

## 전략 디자인 패턴

Strategy pattern

<https://en.wikipedia.org/wiki/Strategy_pattern>

OCP를 지키는 패턴이다.

lambda를 사용할 수 없던 시절에 고대의 개발자들이 그와 비슷한 작업을 할 수 있도록 노력한 흔적이다. 현대에는 단순히 인자로 lambda를 넘겨주면 된다.

## 2.2.1 네 번째 시도: 추상적 조건으로 필터링

![](images/20230404205607.png)

필터에서 프레디케이트를 사용하는 방법이다.

### 코드/동작 전달하기

동작 파라미터화의 장점: 컬렉션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다.

![](images/20230404205745.png)

결국 노란색으로 색칠된 로직이 중요한 부분이다. 그 주변에 public class, test메서드 등은 오직 고대 자바의 문법적 한계를 극복하기 위해 필요한 boilerplate 코드일 뿐이다.


## 퀴즈 2-1: 유연한 prettyPrintApple 메서드 구현하기

format printer도 매우 비슷한 방식으로 구현할 수 있다. test 대신 print 혹은 format string 하는 단일 함수를 가진 SAM (Single Abstract Method) 인터페이스를 만들면 된다.

![](images/20230404210518.png)

## 실습

### 윈도우

 2-2 폴더에 들어가서 powershell을 연 뒤 ./run.ps1 을 실행하면 된다.

### 유닉스

> 주의: 테스트 해보지 않음.

터미널에서 ./run.sh를 실행하면 된다.

### 결과

![](images/20230406151222.png)

## 추가로 생각해볼 점

다음은 위에서 언급된 위키피디아의 strategy pattern 항목 중 자바 예시 코드이다.

``` java
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

enum BillingStrategy {

    // Normal billing strategy (unchanged price)
    NORMAL (a -> a),
    // Strategy for Happy hour (50% discount)
    HAPPY_HOUR (a -> a/2),
    ;

    private final IntUnaryOperator strategy;

    BillingStrategy(IntUnaryOperator strategy) {
        this.strategy = strategy;
    }
    
    // Use a price in cents to avoid floating point round-off error
    int getActPrice(int rawPrice) {
        return this.strategy.applyAsInt(rawPrice);
    }

}

class CustomerBill {
    private final List<Integer> drinks = new ArrayList<>();
    private BillingStrategy strategy;

    public CustomerBill(BillingStrategy strategy) {
        this.strategy = strategy;
    }

    public void add(int price, int quantity) {
        this.drinks.add(this.strategy.getActPrice(price*quantity));
    }

    // Payment of bill
    public void print() {
        int sum = this.drinks.stream().mapToInt(v -> v).sum();
        System.out.println("Total due: " + sum);
        this.drinks.clear();
    }

    // Set Strategy
    public void setStrategy(BillingStrategy strategy) {
        this.strategy = strategy;
    }
}

public class StrategyPattern {
    public static void main(String[] arguments) {
        // Prepare strategies
        BillingStrategy normalStrategy    = BillingStrategy.NORMAL;
        BillingStrategy happyHourStrategy = BillingStrategy.HAPPY_HOUR;

        CustomerBill firstCustomer = new CustomerBill(normalStrategy);

        // Normal billing
        firstCustomer.add(100, 1);

        // Start Happy Hour
        firstCustomer.setStrategy(happyHourStrategy);
        firstCustomer.add(100, 2);

        // New Customer
        CustomerBill secondCustomer = new CustomerBill(happyHourStrategy);
        secondCustomer.add(80, 1);
        // The Customer pays
        firstCustomer.print();

        // End Happy Hour
        secondCustomer.setStrategy(normalStrategy);
        secondCustomer.add(130, 2);
        secondCustomer.add(250, 1);
        secondCustomer.print();
    }
}
```

굉장히 신기한 코드다. 링크에 있는 c#이나 python의 예시 코드는 책과 비슷한 패턴을 보여 친숙한데, 자바 코드는 그렇지 않다.

의도가 명확하지 않지만, 람다를 enum에 저장한 걸로 보아 자바8 이후의 코드로 추정된다.

람다를 따로 변수에 저장하기 번거로운 자바의 특성을 우회하기 위해 IntUnaryOperator에 저장한 다음에 재활용하려고 한 흔적이 돋보인다. 이런 방법도 있구나 싶다. 나중에 만나더라도 당황하지 말자.
# 2.3 복잡한 과정 간소화

위에서 보여준 예시처럼 메서드로 새로운 동작을 전달하려면 Predicate 인터페이스를 선언하고, 이를 구현하는 여러 클래스를 정의한 다음에 인스턴스화해야 한다. 이는 상당히 번거로운 작업이며, 시간 낭비이다.

이를 개선하기 위해서 자바는 `클래스의 선언과 인스턴스화`를 동시에 수행할 수 있도록 `익명 클래스(anonymous class)`라는 기법을 제공한다. 익명 클래스를 이용하면 코드의 양을 줄일 수 있다. (더 가독성 있는 코드 구현하려면 → 람다 표현식)


## 2.3.1. 익명 클래스

`익명 클래스`는 자바의 지역 클래스와 비슷한 개념이다. 말 그대로 이름이 없는 클래스다. 익명 클래스를 이용하면 `클래스 선언과 인스턴스화`를 동시에 할 수 있다. 즉, 즉석에서 필요한 구현을 만들어서 사용할 수 있다.

## 2.3.2. 다섯 번째 시도 : 익명 클래스 사용

익명 클래스를 이용하여 ApplePredicate를 구현하는 객체를 만드는 방법으로 필터링 예제를 다시 구현하는 코드이다.

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() { # filterApples 메서드의 동작을 직접 파라미터화했다!
  public boolean test(Apple apple) {
    return RED.equals(apple.getColor());
  }
});
```

익명 클래스의 단점은 여전히 많은 공간을 차지한다는 점과, 많은 개발자들이 익명 클래스 사용에 익숙하지 않는다는 점이 있다.

### Quiz 2-2. 익명 클래스 문제

다음 코드를 실행한 결과는 4, 5, 6, 42 중 어느 것일까?

```java
public class MeaningOfThis {
	public final int value = 4;
	public void doIt() {
		int value = 6;
		Runnable r = new Runnable () {
			public final int value = 5;
			public void run() {
				int value = 10;
				System.out.println(this.value);
			}
		};
		r.run();
	}

	public static void main(String...args) {
		MeaningOfThis m = new MeaningOfThis();
		m.doIt(); # 이 행의 출력 결과는?
	}
}
```

코드에서 this는 MeaningOfThis가 아니라 Runnable을 참조하므로 5가 정답이다.

이유는 먼저 **`doIt`** 메서드 내에서 **`Runnable`** 객체를 생성하고 **`run`** 메서드를 호출한다. 
**`run`** 메서드 내에서 **`this.value`** 를 출력한다. 
이때 **`this`** 는 익명 클래스의 인스턴스를 가리키며, 그 인스턴스는 **`value`** 필드를 가지고 있다. 
이 필드는 **`final`** 로 선언되어 있으며 값을 변경할 수 없으므로, **`this.value`** 는 항상 5이다.

*코드의 장황함(verbosity)은 나쁜 특성이다. 장황한 코드는 구현하고 유지보수하는 데 시간이 오래 걸리고 읽기 좋지 않다. 한 눈에 이해할 수 있어야 좋은 코드다.*

익명 클래스로 인터페이스를 구현하는 여러 클래스를 선언하는 과정을 줄일 수 있지만 코드 조각(선택 기준 가리키는 불리언 표현식)을 전달하는 과정에서 결국은 객체를 만들고 명시적으로 새로운 동작을 정의하는 메서드(Predicate의 test 메서드)를 구현해야 한다는 점은 변하지 않는다.

## 2.3.3. 여섯 번째 시도 : 람다 표현식 사용

람다 표현식을 이용해 코드를 간결하게 재구현하여 복잡성 문제를 해결할 수 있다.

```java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

<img width="581" alt="image" src="https://user-images.githubusercontent.com/97447334/230613469-aff9c002-6d81-411e-b241-0c43e79dc30a.png">


## 2.3.4. 일곱 번째 시도 : 리스트 형식으로 추상화

```java
public interface Predicate<T> {
  boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) { # 형식 파라미터 T 등장
  List<T> result = new ArrayList<>();
  for(T e : list) {
    if(p.test(e)) {
      result.add(e);
    }
  }
  return result;
}
```

이제 사과가 아니더라도 바나나, 오렌지, 정수, 문자열 등의 리스트에 필터 메서드를 사용할 수 있다.

```java
List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
```

이렇게 해서 유연성과 간결함을 얻을 수 있게 되었다.
# 2.4 실전 예제
## 2.4.1 Comparator로 정렬하기
자바 8의 List에는 sort 메서드가 포함되어 있다. 다음과 같은 인터페이스를 갖는 `java.util.Comparator` 객체를 이용해서 sort의 동작을 파라미터화 할 수 있다.
```java
//java.util.Comparator
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```
Comparator를 구현해서 sort 메서드의 동작을 다양화 할 수 있다.<br>
예를들어 무게가 적은 순서로 목록에서 사과를 정렬하는 것도 가능하다.
```java
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) { 
        return a1.getWeight().compareTo(a2.getWeight());
    }
});
```
요구사항이 바뀌면 새로운 요구사항에 맞는 Comparator를 만들어 sort메서드에 전달할 수 있다.<br>
위 코드를 람다 표현식으로 이용하면 아래와 같은 코드가 된다.
```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight)));
```
## 2.4.2 Runnable로 코드 블록 실행하기
자바 8까지는 Thread 생성자에 객체만을 전달할 수 있었으므로 보통 결과를 반환하지 않는 void run 메소드를 포함하는 익명 클래스가 Runnable 인터페이스를 구현하도록 하는 것이 일반적인 방법이었다.<br>
자바에서는 Runnable 인터페이스를 이용해서 실행할 코드 블록을 지정할 수 있다.
```java
// java.lang.Runnable
public interface Runnable {
    void run();
}
```
Runnable을 이용해서 다양한 동작을 스레드로 실행할 수 있다.
```java
Thread t = new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello");
    }
});
```
위 코드를 자바 8부터 지원하는 람다 표현식을 이용하면 아래와 같다.
```java
Thread t = new Thread(() -> System.out.println("Hello"));
```
## 2.3. Callable을 결과로 반환하기

자바 5부터 지원하는 ExecutorService 인터페이스는 태스크 제출과 실행 과정의 연관성을 끊어준다.

ExecutorService를 이용하면 태스크를 스레드 풀로 보내고 결과를 Future로 저장할 수 있다. <-> 스레드 + Runnable 이용 방식

> Callable 인터페이스를 이용해 결과를 반환하는 태스크를 만든다.

| java.util.concurrent.Callable

```java
public interface Callable<V> {
    V call();
}
```

다음과 같이 실행 서비스에 태스크를 제출해서 위 코드를 활용할 수 있다. 아래 코드는 태스크를 실행하는 스레드의 이름을 반환한다.

```java
ExecutorService executorService = Executors.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>() {
    @Override
    public String call() throw Exception {
            return Thread.currentThread().getName();
    }
});
```

| 람다 이용

```java
Future<String> threadName = executorService.submit(() -> Thread.currentThread().getName());
```

## 2.4. GUI 이벤트 처리하기

GUI 프로그래밍은 마우스 클릭이나 문자열 위로 이동하는 등의 이벤트 대응 동작을 수행하는 식으로 동작한다.

모든 동작에 반응할 수 있어야 하기 때문에 GUI 프로그래밍에서도 변화에 대응할 수 있는 유연한 코드가 필요하다.

자바 FX에서는 setOnAction 메소드에 EventHandler를 전달함으로써 이벤트에 어떻게 반응할지 설정할 수 있다.

```java
Button button = new Button("Send");
button.setOnAction(new EventHandler<ActionEvent>() {
    public void handle(ActionEvent event) {
        label.setText("Sent!");
    }
});
```

즉, EventHandler는 setOnAction 메소드의 동작을 파라미터화한다.

| 람다 이용

```java
button.setOnAction(event -> label.setText("Sent!"));
```