# 6.5. Collector 인터페이스
Collector 인터페이스는 리듀싱 연산(==컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.

다음 코드는 Collector 인터페이스의 시그니처와 다섯 개의 메서드 정의를 보여준다.

```java
public interface Collector<T, A, R> {
  Supplier<A> supplier();
  BiConsumer<A, T> accumulator();
  Function<A, R> finisher();
  BinaryOperator<A> combiner();
  Set<Characteristics> characteristics();
}
```

- T는 수집될 스트림 항목의 제네릭 형식이다.
- A는 누적자. 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
- R은 수집 연산 결과 객체의 형식(항상 그런것은 아니지만 대게 컬렉션 형식)이다.

예를 들어 Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T>라는 클래스를 구현할 수 있다.

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

### **6.5.1 Collector 인터페이스의 메서드 살펴보기**

**supplier 메서드 : 새로운 결과 컨테이너 만들기**

supplier 메서드는 빈 결과로 이루어진 Supplier를 반환해야 한다. 즉, 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수다.

ToListCollector처럼 누적자를 반환하는 컬렉터에서는 빈 누적자가 비어있는 스트림의 수집 과정의 결과가 될 수 있다.

```java
public Supplier<List<T>> supplier() {
  return() -> new ArrayList<T>();
}

//생성자 참조 방식으로 전달도 가능public Supplier<List<T>> supplier() {
  return ArrayList::new;
}
```

**accumlator 메서드 : 결과 컨테이너에 요소 추가하기**

accumlator 메서드는 리듀싱 연산을 수행하는 함수를 반환한다. 스트림에서 n번째 요소를 탐색할 때 두 인수, 즉 누적자(n-1번째 항목까지 수집한 상태)와 n번째 요소를 함수에 적용한다.

함수의 반환값은 void, 즉 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부 상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다.

```java
public BiConsumer<List<T>, T> acuumulator() {
  return (list, item) -> list.add(item);
}

//다음처럼 메서드 참조를 이용하면 코드가 더 간결해진다.public BiConsumer<List<T>, T> accumulator() {
  return List::add;
}
```

**finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기**

finisher 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 객체로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.

```java
public Function<List<T> List<T>> finisher() {
  return Function.identity();
}
```

**combiner 메서드 : 두 결과 컨테이너 병합**

combiner는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.

toList의 combiner는 비교적 쉽게 구현할 수 있으며, 스트림의 두 번째 서브 파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다.

```java
public BinaryOperator<List<T>> combiner() {
  return (list1, list2) -> {
    liat.addAll(list2);
    return list1;
  }
}
```

이 메서드를 이용하면 스트림의 리듀싱을 병렬로 처리할 수 있다.

**병렬 리듀싱 수행 과정**

- 스트림을 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다.
- 모든 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 병렬로 처리한다.
- 컬렉터의 combiner 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합친다.

**Characteristics 메서드**

characteristics 메서드는 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다.

Characteristics는 스트림을 병렬로 리듀스 할지와 병렬로 리듀스한다면 어떤 최적하를 선택해야할지 힌트를 제공한다.

**Characteristics의 항목**

- UNORDERED : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
- CONCURRENT : 다중 스레드에서 accumulator 함수를 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어있지 않은 상황에서만 병렬 리듀싱을 수행할 수 있다.
- IDENTITY_FINISH : finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있으며, 누적자 A를 결과 R로 안전하게 형변환할 수 있다.

### **6.5.2 응용하기**

지금까지 살펴본 다섯 가지 메서드를 이용해서 자신만의 커스텀 toListCollector를 구현할 수 있다.

```java
public class ToListCollector<T> implements Collect<T, List<T>, List<T>> {
  @Override
  public Supplier<List<T>> supplier() {
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<T>, T> accumulator() {
    return List::add;
  }

  @Override
  public Function<List<T> List<T>> finisher() {
    return Function.identity();
  }

  @Override
  public BinaryOperator<List<T>> combiner() {
    return (list1, list2) -> {
      liat.addAll(list2);
      return list1;
    }
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
  }
}
```

**컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기**

IDENTITY_FINISH 수집 연산에서는 Collector 인터페이스를 완전히 새로 구현하지 않고도 같은 결과를 얻을 수 있다.

Stream은 세 함수(발행, 누적, 합침)를 인수로 받는 collect  메서드를 오버로드하여 각각의 메서드는 Collector 인터페이스의 메서드가 반환하는 함수와 같은 기능을 수행한다.

```java
List<Dish> dishes = menuStream.collect(
  ArrayList::new,//발행
  List::add,//누적
  List:addAll);//합침
```
