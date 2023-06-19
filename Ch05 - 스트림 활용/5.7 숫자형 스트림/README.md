# 5.7. 숫자형 스트림

## 5.7.1. 기본형 특화 스트림

스트림 API는 박싱 비용을 피할 수 있도록 세 가지 기본형 특화 스트림을 제공한다.

- IntStream
- DoubleStream
- LongStream

세 가지 숫자 스트림을 사용하면 합계 계산을 위한 `sum`, 최댓값 요소 검색을 위한 `max` 등  자주 사용하는 숫자 관련 리듀싱 연산 수행 메소드를 제공한다.

특화 스트림은 오직 박싱 과정에서 일어나는 효율성만 관련 있을 뿐 스트림에 추가 기능을 제공하지 않는다.

### 숫자 스트림으로 매핑

`mapToInt`, `mapToDouble`, `mapToLong` 세 가지 메소드를 가장 많이 사용한다.

이 메소드들은 map 과 같은 기능을 수행하지만, Stream<T> 대신 특화된 스트림을 반환한다.

```java
int calories = menu.stream()            // Stream<Dish> 반환
        .mapToInt(Dish::getCalories)    // IntStream 반환
        .sum();
```

### 객체 스트림으로 복원하기

IntStream의 map 연산은 IntUnaryOperator(int -> int)를 인수로 받는다.

숫자 스트림에서 스트림으로 복원하기 위해서는 `boxed()`를 사용할 수 있다.

```java
List<Integer> calories = menu.stream()  // Stream<Dish>
        .mapToInt(Dish::getCalories)    // IntStream
        .boxed()                        // Stream<Integer>
        .collect(Collectors.toList());
```

### 기본값 : OptionalInt

IntStream에서 최댓값을 찾을 때 0이라는 기본값 때문에 잘못된 결과가 도출될 수 있다.

스트림에서 요소가 없는 상황과 실제 최댓값이 0인 상황을 구별하기 위해서 값의 존재 여부를 가리킬 수 있는 컨테이너 클래스 Optional을 Integer, String 등의 참조 형식으로 파라미터화할 수 있다.

(OptionalInt, OptionalDouble, OptionalLong 세 가지 기본형 특화 스트림 버전도 제공)

```java
int maxCalory = menu.stream()
        .mapToInt(Dish::getCalories)
        .max()          // OptionalInt
        .orElse(1);     // 값이 없을 때 최댓값 명시적으로 설정
```

## 5.7.2 숫자 범위

프로그램에서 특정 범위의 숫자를 이용해야 할 때 IntStream과 LongStream에서는 `range`와 `rangeClosed` 두 정적 메소드를 사용할 수 있다.

- range: 종료값이 결과에 포함되지 않음
- rangeClosed: 종료값이 결과에 포함됨

```java
System.out.println("== range() 범위 ==");
IntStream.range(1, 5).forEach(System.out::println);
System.out.println("== rangeClosed() 범위 ==");
IntStream.rangeClosed(1, 5).forEach(System.out::println);
```

실행 결과

```
== range() 범위 ==
1
2
3
4
== rangeClosed() 범위 ==
1
2
3
4
5
```