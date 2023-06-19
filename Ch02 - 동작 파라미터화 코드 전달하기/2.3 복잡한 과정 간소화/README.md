# 복잡한 과정 간소화

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

![](parameter.png)

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