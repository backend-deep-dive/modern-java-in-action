# 6.2 리듀싱과 요약

### 6.2.1 스트림값에서 최댓값과 최솟값 검색

```java
Comparator<Dish> dishCaloriesComparator =
Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
```

maxBy, minBy 는 comparator를 인수로 받는 collector 함수.

### 6.2.2 요약 연산

summing, averaging, summarizing 등의 시리즈가 있다. 

### summing...
- Collectors.summingInt

```java
int totalColories = menu.stream().collect(summingInt(Dish::getCalories));
```

- summingLong
- summingDouble

### averaging...

- averagingInt
- averagingLong
- averagingDouble

### summarizing...
- summarizingInt
- summarizingLong
- summarizingDouble

``` java
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
IntSummaryStatistics{count=9, sum=4300, min=120, average=477, max= 800}
```

### 6.2.3 문자열 연결

``` java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
```

내부적으로 StringBuilder를 이용해서 효율적으로 문자열을 하나로 만든다.

클래스가 toString 메서드를 포함하고 있다면 .map(Dish::getName) 과 같은 부분을 생략할 수도 있다.

``` java
String shortMenu = menu.stream().collect(joining());
```

joining 함수에 separator를 던져 줄 수 있다. 

``` java
String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
```

### 6.2.4 범용 리듀싱 요약 연산

지금까지 살펴본 모든 컬렉터는 reducing 팩토리 매서드로도 모두 정의해서 사용할 수 있다. 단지 프로그래밍적 편의성과 가독성 때문에 미리 만들어 둔 Collectors 시리즈를 사용하는 것이다.

```java
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
```

위 코드는 아래에 있는 summingInt 함수를 완벽히 대체했다.

``` java
int totalColories = menu.stream().collect(summingInt(Dish::getCalories));

```

reducing 함수는 3개의 인자를 받는다.

https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html#collect

-   `identity`: Like the `Stream.reduce` operation, the identity element is both the initial value of the reduction and the default result if there are no elements in the stream. In this example, the identity element is `0`; this is the initial value of the sum of ages and the default value if no members exist.
-   `mapper`: The `reducing` operation applies this mapper function to all stream elements. In this example, the mapper retrieves the age of each member.
-   `operation`: The operation function is used to reduce the mapped values. In this example, the operation function adds `Integer` values.

이 3가지 정보만 있으면 웬만한 collector를 금방 뚝딱 내부적으로 자유롭 만들어낼 수 있다!

#### reducing 함수는 한 개의 인자만을 받을 수도 있다.

이때 identity는 스트림의 첫 번째 인자이고, mapper는 자기 자신을 받는 항등 함수이다. (x -> x)

이때의 문제점은 스트림이 텅 비었을 때 발생한다. 이 점을 해결하기 위해 스트림이 비었을 시 Optional.empty를 반환한다. 

따라서, 해당 reducing 콜렉터를 통해 반환되는 값은 `Optional<Dish>` 이다.

### collect 과 reduce 의 차이점은 무엇인가?

reduce가 하는 일을 collect가 할 수도 있고, collect가 하는 일을 reduce가 할 수도 있다. (아마도...) 

사실, 우리가 하고자 하는 거의 대부분의 스트림 연산을 reduce만으로 다 해결 가능하다. 예를 들어, map, filter 등도 reduce로 간단하게 해결 가능하다.

``` java

Stream<Ingeter> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();

List<Integer> numbers = stream.reduce(
// identity
new ArrayList<Integer>(),

// accumulator
(l, e) -> {l.add(e); return l; },

// combiner
(l1, l2) -> l1.addAll(l2); return l1;}
);

```

그럼에도 불구하고 이들은 코드의 가독성과 명확성, 그리고 실용성을 위해 분리되었다.

- collect: 도출하려는 결과를 누적하는 컨테이너를 바꾼다.
- reduce: 두 값을 하나로 도출하는 불변형 연산이다.

#### 가독성, 명확성

reduce를 보면, 
아, 무언가 변환시키고 누적시켜서 새로운 값을 형성하려는 의도이구나!

collect를 보면
아, 무언가를 모아서 컨테이너에 담으려고 하는 의도이구나!

#### 실용성

reduce와 collect를 구분하는 것이 병렬에서 유리하다.

단순 collecting을 위해 multi-thread로 stream().reduce()를 사용한다면, new ArrayList<>()를 통해 여러 배열리스트를 계속 생성하고 combine해야 한다. 성능 저하를 야기할 수 있다.

collect는 가변 컨테이너 관련 작업을 하면서 병렬성을 확보하는 것에 특화되어 있다. 자세한 내용은 7장에서...

### 질문:

![](attachments/Pasted%20image%2020230511163314.png)

3개의 차이는 무엇일까요?

```java
reducing(Integer::sum);

reducing(0, (x, y) -> x + y.getCalories());

reducing(0, Dish::getCalories, Integer::sum));

```


### 연습문제

Collectors.reducing 을 사용해서

직접 custom-made
- summing
- counting
을 만들어보시오.

정답:

```java
reducing(0, Dish::getCalories, Integer::sum));
reducing(0L, e -> 1L, Long::sum);

```

### 연습문제 2

stream.reduce()를 사용해서
직접 custom-made
- summing
- counting
을 만들어보시오.

정답:

```java
int totalCalories = menu
.stream()
.map(Dish::getCalories)
.reduce(Integer::sum)
.get()
```

> 10장에서도 다시 설명했지만 get 말고 orElse(), orElseGet()을 사용하자.

```java
// 확인 x
int totalCount = menu
.stream()
.reduce(0, (x, y) -> x + 1);
```

> 아마 동작한다...

> intStream의 sum 함수를 사용할 수도 있다.

```java
int totalCalories = menu
.stream()
.maptoInt(Dish::getCalories)
.sum();
```

## 퀴즈 6-1 
리듀싱으로 문자열 연결하기

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
```

위 코드를 reducing으로 올바르게 바꾼 코드들을 모두 고르시오.

1.
```java
String shortMenu = menu
.stream().map(Dish::getName)
.collect(reducing(
(s1, s2) -> s1 + s2
)).get();

```

2.
```java
String shortMenu = menu.stream()
.collect(reducing(
(d1, d2) -> d1.getName() + d2.getName()
)).get();
```

3.
```java
String shortMenu = menu.stream()
.collect(reducing(
// identity
"",

// mapper
Dish::getName,

// operation
(s1, s2) -> s1 + s2
));
```

정답은 1, 3번.

2번을 올바르게 바꾸려면 어디를 고쳐야 할까?
