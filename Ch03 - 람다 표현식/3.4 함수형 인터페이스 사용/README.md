#  3.4 함수형 인터페이스 사용
- 함수형 인터페이스의 추상 메서드 시그니처를 함수 디스크립터 (function descriptor)라고 한다.
- 다양한 람다 표현식을 사용하기 위해서는 공통의 함수 디스크립터를 기술하는 함수형 인터페이스 집합이 필요하다.
- 자바 8에서는 `java.util.function` 패키지로 여러 가지 새로운 함수형 인터페이스를 제공한다.
## 3.4.1 Predicate
- Predicate<T> 인터페이스는 test라는 추상 메서드를 정의하며 test는 제네릭 형식 T의 객체를 인수로 받아 boolean을 반환한다.
- 우리가 만들었던 인터페이스와 같은 형태인데 따로 정의할 필요 없이 바로 사용할 수 있다는 점이 특징이다.
- 즉 제네릭 T 형식의 객체를 사용하는 boolean 표현식이 필요한 상황에서 Predicate 인터페이스를 사용할 수 있다.
  
아래는 String 객체를 인수로 받는 람다를 정의할 수 있다.
```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

public <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for (T t : list) {
        if (p.test(t)) {
            results.add(t);
        }
    }
    return results;
}

Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```
## 3.4.2 Consumer
- Consumer<T> 인터페이스는 제네릭 형식 T 객체를 받아서 void를 반환하는 accept라는 추상 메서드를 정의한다.
- 제네릭 T 형식의 객체를 인수로 받아서 어떤 동작을 수행하고 싶을 때 Consumer 인터페이스를 사용할 수 있다.
- 예를들어 Integer 리스트를 인수로 받아서 각 항목에 어떤 동작을 수행하는 forEach 메서드를 정의할 때 활용할 수 있다.

아래는 forEach와 람다를 이용해서 리스트의 모든 항목을 출력하는 예제이다.
```java

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

public <T> void forEach(List<T> list, Consumer<T> c) {
    for (T t : list) {
        c.accept(t);
    }
}
forEach(Arrays.asList(1,2,3,4,5), (Integer i) -> System.out.println(interface));
```
## 3.4.3 Function
- Function<T,R> 인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 apply를 정의한다.
- 입력을 출력으로 매핑하는 람다를 정의할 때 Function 인터페이스를 활용할 수 있다.
  - ex) 사과의 무게 정보를 추출하거나 문자열을 길이와 매핑

아래는 String 리스트를 인수로 받아 각 String의 길이를 포함하는 Integer 리스트로 변환하는 map 메서드를 정의하는 코드이다.
```java

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

public <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> results = new ArrayList<>();
    for (T t : list) {
      results.add(f.apply(t));
    }
    return results;
}

//[7,2,6]
List<Integer> l = map(
        Arrays.asList("lambdas","in","action"),
        (String s) -> s.length() // Function의 apply 메서드를 구현하는 람다
);
```
### 기본형 특화
- 자바의 모든 형식은 참조형(Byte,Integer,Object,List) 아니면 기본형(int,double,char,byte..)에 해당한다.
- 하지만 제네릭 내부 구현 때문에 제네릭 파라미터에는 **참조형**만 사용할 수 있다.
- 자바에서는 `기본형 -> 참조형`으로 변환하는 기능을 제공하는데, 이 기능을 박싱 이라고 한다.
  - 반대로 `참조형 -> 기본형` 으로 변환하는 동작을 언방식 이라고 한다.
  - 박싱과 언박싱을 자동으로 처리하는 오토박싱 기능도 지원한다.
  - 하지만 이러한 변환 과정에는 비용이 들기 때문에 자바 8에서는 오토바 박싱을 피할 수 있도록 특별한 버전의 함수형 인터페이스도 제공한다.

아래 코드는 `IntPredicate`는 1000이라는 값을 박싱하지 않지만, `Predicate<Integer>`는 1000이라는 값을 Integer 객체로 반환하는 코드이다.
```java
public interface IntPredicate {
    boolean test(T t);
}
IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000) // 참 (박싱 없음)

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
oddNumbers.test(1000); // 거짓 (박싱)
```