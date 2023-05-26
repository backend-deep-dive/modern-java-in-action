## 담당자

- 6.1 컬렉터란 무엇인가 : 홍승아
- 6.2 리듀싱과 요약 : 김대현
- 6.3 그룹화 : 송민진
- 6.4 분할 : 이홍섭
- 6.5 Collector 인터페이스 : 홍승아
- 6.6 커스텀 컬렉터를 구현해서 성능 개선하기 : 이홍섭
## 진행 날짜
- 2023년 5월 11일 (목요일)
- 2023년 5월 18일 (목요일) -> 5월 25일(목요일)
## 통합 정리
# 6.1. 컬렉터란 무엇인가?
4장과 5장에서는 스트림에서 최종 연산 `collect`를 사용해, `toList`로 스트림 요소를 항상 리스트로만 변환했다. 

이 장에서는 `reduce`가 그랬던 것처럼 `collect` 역시 다양한 요소 누적 방식을 인수로 받아서 스트림을 최종 결과로 도출하는 리듀싱 연산을 수행할 수 있음을 설명한다.

다양한 요소 누적 방식은 `Collector` 인터페이스에 정의되어 있다.

> 컬렉션(Collection), 컬렉터(Collector), collect는 서로 다르다는 점에 주의하자!
> - `collect()`: Collector를 매개변수로 하는 스트림의 최종 연산
> - `Collector`: 수집(collect)에 필요한 메서드를 정의해 놓은 인터페이스
> - `Collection`: 자바 기본 자료구조 중 하나로, 데이터 집합을 다루는 인터페이스 (ex. List, Set, Map)


- 통화별로 트랜잭션을 그룹화한 코드 예제

```java
// 명령형 프로그래밍 버전
Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
    for (Transaction transaction : transactions) {
      Currency currency = transaction.getCurrency();
      List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
      if (transactionsForCurrency == null) {
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionsForCurrency);
      }
      transactionsForCurrency.add(transaction);
    }
---
{JPY=[JPY 7800.0, JPY 5700.0], EUR=[EUR 1500.0, EUR 1100.0, EUR 5600.0, EUR 6800.0], USD=[USD 2300.0, USD 4500.0, USD 4600.0], GBP=[GBP 9900.0, GBP 3200.0], CHF=[CHF 6700.0, CHF 3400.0]}
```
함수형 프로그래밍에서는 ‘무엇’을 원하는지 직접 명시할 수 있어서 어떤 방법으로 이를 얻을지는 신경 쓸 필요가 없다.

```java
// 함수형 프로그래밍 버전
Map<Currency, List<Transaction>> transactionsByCurrencies =
    transactions.stream().collect(groupingBy(Transaction::getCurrency));
```
- collect 메서드로 Collector 인터페이스의 구현을 전달했다.
- Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할 지 지정한다.
- 리스트를 만들기 위해 toList를 사용하는 대신 더 범용적인 컬렉터 파라미터를 collect 메서드에 전달함으로써 원하는 연산을 간결하게 구현할 수 있다.
    - toList를 Collector 인터페이스의 구현으로 사용하여, 스트림의 요소를 리스트로 만들 수 있다.
    - `groupingBy`를 이용해서 Currency를 키로 갖고, 이에 대응하는 Transaction 리스트를 값으로 갖는 Map을 만들었다.

<br>

## 6.1.1. 고급 리듀싱 기능을 수행하는 컬렉터

- 함수형 API의 또 다른 장점으로 높은 수준의 조합성과 재사용성을 꼽을 수 있다.
- Collector의 최대 강점은 collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이다.
- 스트림에서 collect를 호출하면 collect에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하면서 컬렉터가 작업을 수행한다.
- 함수를 요소로 변환할 때는 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다.
- Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림에 어떤 리듀스 연산을 수행할지 결정된다.
- Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다. (toList, toSet 등)

<br>

## 6.1.2. 미리 정의된 컬렉터

Collectors 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 메서드를 제공한다.

Collectors에서 제공하는 메서드의 기능은 크게 세 가지로 구분된다.

- 스트림 요소를 하나의 값으로 리듀스하고 요약
- 요소 그룹화
- 요소 분할
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

# 그룹화

: 데이터 집합을 하나 이상의 특성으로 분류해서 그룹화하는 연산

- **~Java 7** : 명령형 프로그래밍 - 그룹화를 구현하려면, 까다롭고, 할 일이 많으며, 에러도 많이 발생함
- **Java 8** ~ : 함수형 프로그래밍 - 가독성 있는 한 줄의 코드로 그룹화 구현 가능

<br>

## Classification Function (분류 함수)

: 함수를 기준으로 스트림을 그룹화하는 함수

- 그룹화 연산의 결과물 : 그룹화 함수가 반환하는 key와, 각 key에 대응하는 스트림의 모든 항목 리스트를 value로 갖는 Map

<br>

### 1. 그룹화의 활용

#### case 1. 메서드 참조를 분류 함수로 사용

```java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
```

```java
{
	FISH=[prawns, salmon], 
	OTHER=[french fries, rice, season fruit, pizza],
	MEAT=[pork, beef, chicken]
}
```

#### case 2. 람다 표현식으로 분류 함수를 구현

예시) 400칼로리 이하를 `diet`로，400 ~ 700칼로리를 `normal`로, 700 칼로리 초과를 `fat` 요리로 분류하는 코드

```java
public enum CaloricLevel { DIET, NORMAL, FAT }
```

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
	groupingBy(dish -> {
		if (dish.getCalories() <= 400) return CaloricLevel.DIET;
		else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
		else return CaloricLevel.FAT;
	}));
```

#### case 3. 다수준 그룹화 (여러 기준으로 동시에 그룹화)

- `Collectors.groupingBy`
  - 항목을 다수준으로 그룹화 가능
  - 일반적인 분류 함수와 컬렉터를 인수로 받음
  - 즉, 바깥쪽 groupingBy 메서드에 스트림의 항목을 분류할 두 번째 기준을 정의하는 내부 groupingBy를 전달해서, 두 수준으로 스트림의 항목을 그룹화할 수 있음


```java
Map<Dish.Type, Map<CaloricLevel, List<Dish>> dishesByTypeCaloricLevel = menu.stream().collect(
	groupingBy(Dish::getType,    // 첫 번째 수준의 분류 함수
		groupingBy(dish -> {     // 두 번째 수준의 분류 함수
			if (dish.getCalories() <= 400)
				return CaloricLevel.DIET;
			else if (dish.getCalories() <= 700)
				return CaloricLevel.NORMAL;
			else return CaloricLevel.FAT;
		})
	)
);
```

```java
{
	MEAT={
		DIET=[chicken],
		NORMAL=[beef],
		FAT=[pork]
	},
	FISH={
		DIET=[prawns],
		NORMAL=[salmon]
	},
	OTHER={
		DIET=[rice, seasonal fruit],
		NORMAL=[french fries, pizza]
	}
}
```

- 외부 맵 : 첫 번째 수준의 분류 함수에서 분류한 키값 ‘fish, meat, other’을 가짐
- 내부 맵 (=외부 맵의 값) : 두 번째 수준의 분류 함수의 기준 ‘normal, diet, fat’을 키값으로 가짐
- 다수준 그룹화 연산은 다양한 수준으로 확장할 수 있음
<br>⇒ 즉, n수준 그룹화의 결과는 n수준 트리 구조로 표현되는 n수준 맵이 된다.

![image](https://github.com/deingvelop/modern-java-in-action/assets/100582309/dc31f721-436e-4849-a6cd-2424fe10442c)

> - `groupingBy`의 연산을 **버킷(bucket, 물건을 담을 수 있는 양동이)** 개념으로 생각하면 쉽다. 
>   - 첫 번째 `groupingBy` : 각 키의 버킷을 만든다.
>   - 그 후 : 준비된 각각의 버킷을 서브스트림 Collector로 채워가기를 반복하면서 n수준 그룹화를 달성한다.

#### case 4. 서브그룹으로 데이터 수집

예시 1) groupingBy 컬렉터에 두 번째 인수로 counting 컬렉터를 전달해서 메뉴에서 요리의 수를 종류별로 계산하기
```java
Map<Dish.Type, Long> typesCount = menu.stream().collect(
							groupingBy(Dish::getType, counting()));
```

```java
// 결과
{ MEAT=3, FISH=2, OTHER=4 }
```

> 💡 **Tip**
> - 분류 함수 한 개의 인수를 갖는 groupingBy(f) : 사실 groupingBy(f, toList())의 축약형

예시 2) 요리의 종류를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리를 찾는 프로그램

```java
Map<Dish.Type, Optional<Dish>> mostCaloricByType =
	menu.stream()
	     .collect(groupingBy(Dish::getType,
	     .maxBy(comparingInt(Dish::getCalories))));
```

```java
// 그룹화의 결과로 요리의 종류를 key로, Optional<Dish>를 value로 갖는 맵이 반환됨
// Optional<Dish> : 해당 종류의 음식 중 가장 높은 칼로리의 음식을 래핑함
{
	FISH=Optional[salmon],
	OTHER=Optional[pizza],
	MEAT=Optional[pork]
}
```

> 💡 **Note**
> - 팩토리 메서드 `maxBy`가 생성하는 컬렉터의 결과 형식에 따라 맵의 값이 `Optional` 형식이 되었지만,
> - 실제로 메뉴의 요리 중 `Optional.empty()`를 값으로 갖는 요리는 존재하지 않는다.
>   - 처음부터 존재하지 않는 요리의 키는 맵에 추가되지 않기 때문!
>   - `groupingBy` 컬렉터는 스트림의 첫 번째 요소를 찾은 이후에야 그룹화 맵에 새로운 키를 (게으르게) 추가함
> - 리듀싱 컬렉터가 반환하는 형식을 사용하는 상황이므로 굳이 `Optional` 래퍼를 사용할 필요가 없음

<br>

#### case 4-1. Optional을 없애서 개선하기

따라서, 마지막 그룹화 연산에서 맵의 모든 값을 Optional로 감쌀 필요가 없으므로 Optional값 삭제 가능

- `Collectors.collectingAndThen`
  - 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있음
  - **적용할 컬렉터**와 **변환 함수**를 **인수**로 받아 **다른 컬렉터**를 **반환**함
    - 반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 함
    - collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑함

```java
Map<Dish.Type, Dish> mostCaloricByType = 
	menu.stream()
		.collect(groupingBy(Dish::getType,     // 분류함수
				    collectingAndThen(
					 maxBy(comparinglnt(Dish::getCalories)),    // 감싸인 컬렉터 
			 Optional::get)));     // 변환함수
```

- 반환되는 컬렉터 : 기존 컬렉터의 래퍼 역할을 함
- collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑함

> 💡 **Tip**<br>리듀싱 컬렉터는 절대 `Optional.empty()`를 반환하지 않으므로 안전한 코드!

```java
{ FISH=salmon, OTHER=pizza, MEAT=pork }
```

<br>

#### 참고 - 중첩 컬렉터의 작동 원리

![image](https://github.com/deingvelop/modern-java-in-action/assets/100582309/7ef125f3-0949-4a69-8092-a60b73d4f948)


- 컬렉터는 점선으로 표시되어 있음
1. `groupingBy` - 가장 바깥쪽에 위치하면서 요리의 종류에 따라 메뉴 스트림을 세 개의 서브스트림으로 그룹화함
2. `groupingBy` 컬렉터는 `collectingAndThen` 컬렉터를 감쌈 → 두 번째 컬렉터는 그룹화된 세 개의 서브스트림에 적용된다.
3. `collectingAndThen` 컬렉터는 세 번째 컬렉터 `maxBy`를 감싼다.
  - 리듀싱 컬렉터가 서브스트림에 연산을 수행한 결과에 `collectingAndThen`의 `Optional::get` 변환 함수가 적용됨
- `groupingBy` 컬렉터가 반환하는 맵의 분류 키에 대응하는 세 값이 각각의 요리 형식에서 가장 높은 칼로리의 음식

<br>

### 2. 그룹화된 요소들 조작하기

- 요소를 그룹화한 후에는 각 결과 그룹의 요소를 조작하는 연산이 필요함

### Collectors.filtering

#### 필요한 이유? 

> `filter` + 그룹화 하면 될 것 같지만, 그러면 filter에서 걸러진 종류는 결과 Map에서 아예 key값 자체가 사라짐!

예시) 500 칼로리가 넘는 요리만 필터링하여 그룹화하기
```java
Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream()
						     .filter(dish -> dish.getCalories() > 500)
						     .collect(groupingBy(Dish::getType));
```

```java
// 결과 Map - FISH key값 실종!
{
	OTHER=[french fries, pizza],
	MEAT=[pork, beef]
}
```

#### 해결 방법

> 두 번째 Collector 안으로 filter를 이동시킴

```java
Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream()
						     .collect(groupingBy(Dish::getType, 
						     	      		 filtering(dish -> dish.getCalories() > 500, toList())));
```

- `filtering`
  - Collectors 클래스의 또 다른 정적 팩토리 메서드
  - Predicate를 인수로 받음 → 이 Predicate로 각 그룹의 요소와 필터링 된 요소를 재그룹화<br>
  ⇒따라서, 목록이 비어있는 FISH도 항목으로 추가됨!

```java
{
	OTHER=[french fries, pizza],
	MEAT=[porkz beef],
	FISH=[]
}
```


### Collectors.mapping

- 매핑 함수와 각 항목에 적용한 함수를 모으는 데 사용하는 또 다른 컬렉터를 인수로 받는 메서드

예시) 그룹의 각 요리를 관련 이름 목록으로 변환하기
```java
Map<Dish.Type, List<String>> dishNamesByType = menu.stream()
						   .collect(groupingBy(Dish::getType, 
						   		       mapping(Dish::getName, toList())));
```

- groupingBy와 연계해 세 번째 컬렉터를 사용해서 일반 맵이 아닌 flatMap 변환을 수행할 수 있음

예시) 다음과 같이 (해시)태그 목록을 가진 각 요리로 구성된 맵이 있을 때, 각 형식의 요리의 태그를 추출하기
```java
Map<String, List<String>> dishTags = new HashMap<>();
dishTags.put("pork", asList("greasy", "salty"));
dishTags.put("beef"z asList("salty", "roasted")); 
dishTags.put("chicken", asList("fried", "crisp")); 
dishTags.put("french fries", asList("greasy", "fried")); 
dishTags.put("rice", asList("light", "natural")); 
dishTags.put("season fruit", asList("fresh", "natural")); 
dishTags.put("pizza", asList("tasty"z "salty")); 
dishTags.put("prawns", asList("tasty", "roasted")); 
dishTags.put("salmon", asList("delicious"z "fresh"));
```

```java
Map<Dish.Type, Set<String>> dishNamesByType =
menu.stream().collect(groupingBy(Dish::getType, 
			 	 flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
```

- `flatMapping` : 두 수준의 리스트를 한 수준으로 평면화하기
  - 각 요리에서 태그 리스트를 얻어야 하기 때문!
  - 각 그룹에 수행한 flatMapping 연산 결과를 수집해서 리스트가 아니라 집합으로 그룹화해 중복 태그를 제거하는 원리

```java
// 위 연산의 결과!
{
	MEAT=[saltyz greasy, roasted, fried, crisp], 
	FISH=[roasted, tasty, fresh, delicious], 
	OTHER=[salty, greasy, natural, light, tasty, fresh, fried]
}
```


### groupingBy와 함께 사용하는 다른 collector

- 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때 : `groupingBy`에 두 번째 인수로 전달한 컬렉터를 사용하면 됨!

#### groupingBy + summingInt

예시) 메뉴에 있는 모든 요리의 칼로리 합계를 구하려고 만든 컬렉터를 재사용할 수 있음

```java
Map<Dish.Type, Integer> totalCaloriesByType =
	menu.stream()
	    .collect(groupingBy(Dish::getType,
				summingInt(Dish::getCalories)));
```

#### groupingBy + mapping

- `mapping`
  - 스트림의 인수를 변환하는 함수와 변환 함수의 결과 객체를 누적하는 컬렉터를 인수로 받음
  - 입력 요소를 누적하기 전에 매핑 함수를 적용해서 다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환함

예시) 각 요리 형식에 존재하는 모든 Caloric Level값을 도출하기

```java
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
	menu.stream().collect(
		groupingBy(Dish::getType, mapping(dish -> {
			if (dish.getCalories() <= 400) return CaloricLevel.DIET;
			else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
			else return CaloricLevel.FAT; },
		toSet() )));
```

```java
{
	OTHER=[DIET, NORMAL], 
	MEAT=[DIET, NORMAL, FAT], 
	FISH=[DIET, NORMAL]
}
```

여기서 Set의 형식을 정하고 싶다면?
⇒ `toCollection`을 이용하면 원하는 방식으로 결과를 제어할 수 있다.

```java
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = 
	menu.stream().collect(
		groupingBy(Dish::getType, mapping(dish -> {
			if (dish.getCalories() <= 400) return CaloricLevel.DIET; 
			else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL; 
			else return CaloricLevel.FAT; }, 
		toCollection(HashSet::new) )));

```
# 6.4 분할
분할은 **분할 함수**라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.

분할 함수는 Boolean을 반환하므로 맵의 키 형식은 Boolean이며, **`true` 또는 `false`를 갖는 두 개의 그룹으로 분류** 된다.
```java
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
// 결과 : {false=[pork, beef, chicken, prawns, salmon], true=[french fires, rice, season fruit, pizza]}
```
맵의 키로써 `true`, `false`가 들어간 걸 확인할 수 있다.<br>
true의 키값으로 맵에서 모든 채식 요리를 얻을 수도 있다.
```java
List<Dish> vegetarianDishes = partitionedMenu.get(true); //채식인 요리
```
메뉴 리스트로 생성한 스트림을 프레디케이트로 필터링해도 같은 결과를 얻을 수 있다.
```java
List<Dish> vegetarianDishes = menu.stream().filter(Dish::isVegetarian).collect(toList());
```
## 6.4.1 분할의 장점 
분할 함수가 반환하는 **true, false 두 가지 요소의 스트림 리스트를 모두 유지**한다는 것이 분할 함수의 장점이다.

또한 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드된 버전의 `partitioningBy` 메서드도 있다.
```java
Map<Boolean, Map<Dish.Type, List<Dish>>> partitionedMenu = menu.stream().collect(partitioningBy(
        Dish::isVegetarian, groupingBy(Dish::getType));
// {false = {FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, true = {OTHER=[french fires, rice, season fruit, pizza]}}
```
이전 코드를 활용하면 채식 요리와 채식이 아닌 요리의 각각의 그룹에서 가장 칼로리가 높은 요리도 찾을 수 있다.
```java
Map<Boolean, Dish>> partitionedMenu = menu.stream().collect(partitioningBy(
  Dish::isVegetarian, collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get));
// {false = pork, true = pizza}
```
## 6.4.2 숫자를 소수와 비소수로 분할하기
정수 n을 인수로 받아서 2에서 n까지의 자연수를 소수와 비소수로 나누는 프로그램을 구현해보자.
먼저 주어진 수가 소수인지 아닌지 판별하는 프레디케이트를 구현한다.
```java
public boolean isPrime(int candidate) {
    return IntStream.range(2, candidate).noneMatch(i -> candidate % i == 0);
    //스트림의 모든 정수로 candidate를 나눌 수 없으면 true를 반환   
}
```
이제 n개의 숫자를 포함하는 스트림을 만든 다음, `isPrime` 메서드를 프레디케이트로 이용하고 `partitioningBy` 컬렉터로 리듀스해서 숫자를 소수와 비소수로 분할할 수 있다.
```java
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n).boxed().collect(partitioningBy(candidate -> isPrime(candidate)));
}
```
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
# 6.6 커스텀 컬렉터를 구현해서 성능 개선하기
앞서 작성한 소수, 비소수 분할 코드를 `커스텀 컬렉터`를 이용해서 성능을 개선해 보자
````java
// n이하의 자연수를 소수와 비소수로 분류하는 메서드
public Map<Boolean, List<Integer>> partitionPrimes(int n){
    return IntStream.rangeClosed(2,n).boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
}

//제곱근 이하로 대상 caindiditc의 숫자 범위를 제한한 isPrime 메서드
/**
 * 2에서 대상의 제곱근(candidateRoot)까지의 수 중
 * 대상이 나누어떨어지는 수가 없는 경우(noneMatch)는 true 소수이다,
 * 있는 경우는 false 소수가 아니다를 반환하는 프레디케이트
 */
public boolean isPrime(int candidate){
    int candidateRoot = (int) Math.sqrt((double)candidate);
    return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
}

````
커스텀 컬렉터를 이용하면 성능을 더 개선할 수 있다.
## 6.6.1 소수로만 나누기
우선 소수로 나누어 떨어지는지 확인해서 대상의 범위를 좁힐 수 있다.<br>
제수(나눗셈에서 나누는 수)가 소수가 아니면 소용없으므로 제수를 `현재 숫자 이하에서 발견한 소수`로 제한할 수 있으며, 주어진 숫자가 소수인지 확인하기 위해 지금까지 발견한 소수 리스트에 접근해야 한다.<br>
우리가 살펴본 컬렉터로는 컬렉터 수집 과정에서 부분 결과에 접근할 수 없지만, 커스텀 컬렉터 클래스를 사용해서 해결할 수 있다.

-> 중간 결과 리스트가 있다면 isPrime 메서드로 중간 결과 리스트를 전달하도록 구현한다.
```java
public static boolean isPrime(List<Integer> primes, Integer candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate); // 먼저 candidate의 제곱근을 구한다.
    return primes.stream() 
                 .takeWhile(i -> i <= candidateRoot) // candidate 값보다 작은 소수 목록(primes)을 가져온다.
                 .noneMatch(i -> candidate % i == 0); // candidate 수를 각 소수로 나눈 나머지가 0인지 확인 -> 어떤 소수로도 나누어 떨어지면 candidate는 소수가 X
}
```
(위 코드에 대하여)
- 대상 숫자의 제곱근보다 작은 소수만 사용하도록 코드를 최적화 해야한다.<br>
- 다음 소수가 대상의 루트보다 크면 소수로 나누는 **검사를 중단**하는 방식으로 성능을 개선한다.
  - -> 리스트와 프레디케이트를 인수로 받아 프레디케이트를 만족하는 긴 요소로 이루어진 리스트를 반환하는 `takeWhile`이라는 메소드를 구현한다. (`takeWhile`은 java9버전에서 지원하므로 직접 구현해야한다.)
- 위의 숫자형 스트림에서 `rangeClosed`를 이용해 대상의 제곱근 이하의 값까지로만 범위를 제한한 것처럼,
  - `takeWhile`의 쇼트서킷을 이용해 제곱근 이하의 값까지만 체크한다.

새로운 `isPrime` 메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자. Collector 클래스를 선언하고 Collector 인터페이스에서 요구하는 메서드 다섯 개를 구현해야한다. 
### 1단계 : Collector 클래스 시그니처 정의
```java
public interface Collector<T, A, R>
```
- T는 스트림 요소의 형식
- A는 중간 결과를 누적하는 객체의 형식
- R은 collect 연산의 최종 결과 형식을 의미

소수와 비소수를 참과 거짓으로 나누기 위해 정수로 이루어진 스트림에서 누적자와 최종 결과 형식이 `Map<Boolean, List<Integer>>`인 컬렉터를 구현해야 한다.
```java
public class PrimeNumbersCollector
    implements Collect<Integer, 
        Map<Boolean, List<Integer>>,
        Map<Boolean, List<Integer>>>
```
### 2단계 : 리듀싱 연산 구현
먼저 supplier 메서드로 누적자를 만드는 함수를 반환해야 한다.
```java
public Supplier<Map<Boolean, List<Integer>>> supplier() {
  return () -> new HashMap<Boolean, List<Integer>>() {{
    put(true, new ArrayList<Integer>());
    put(false, new ArrayList<Integer>());
  }};
}
```
위 코드에서 누적자로 만들 맵을 만들면서 true, false 키와 빈 리스트로 초기화 했다.

다음으로 스트림 요소를 어떻게 수집할지 결정하는 `accumulator` 메서드를 만든다.
이제 언제든지 원할 때 수집 과정의 중간 결과, 즉 지금까지 발견한 소수를 포함하는 누적자에 접근할 수 있다.
```java
public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
  return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
    acc.get( isPrime(acc.get(true), candidate) ) //isPrime 결과에 따라 소수/비소수 리스트를 만든다.
      .add(candidate); //candidate를 알맞은 리스트에 추가한다.
  };
}
```
지금까지 발견한 소수 리스트 `acc.get(true)`와 소수 여부를 확인할 수 있는 candidate를 인수로 isPrime 메서드를 호출했다. 그리고 호출 결과를 알맞은 리스트에 추가한다.
### 3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)
이번에는 병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다. 예제에서는 두 번째 맵의 소수 리스트와 비소수 리스트의 모든 수를 첫 번째 맵에 추가하는 연산이면 충분하다.
```java
public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
  return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
    map1.get(true).addAll(map2.get(true));
    map1.get(false).addAll(map2.get(false));
  };
}
```
알고리즘 자체가 순차적이어서 컬렉터를 실제로 병렬로 사용할 순 없으므로 combiner 메서드는 빈 구현으로 남겨두거나 exception을 던지도록 구현하면 된다.
### 4단계 : finisher 메서드와 컬렉터의 characteristics 메서드
accumulator의 형식은 컬렉터 결과 형식과 같으므로 변환 과정은 필요없다. 따라서 항등 함수 identity를 반환하도록 finisher 메서드를 구현한다.
```java
public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
  return Function.identity();
}
```
### 최종 Custom Collector 코드
````java
public class PrimNumberCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    /**
     * 수집과정의 중간결과, 즉 지금까지 발견한 소수를 포함하는 누적자로 사용할 맵을 만들고,
     * 소수와 비소수를 수집하는 두 개의 리스트를 각각 true, false 키와 빈 리스트로 초기화 한다. (p226)
     */
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<>() {{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    /**
     * 스트림의 요소를 수집한다. (p226)
     * 누적 리스트 acc에서 isPrime 메서드 결과에 따라 소수(true), 비소수(false) 리스트에 candidate를 추가 한다.
     */
    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
           acc.get( isPrime( acc.get(true), //지금까지 발견한 소수 리스트(supplier true 키 분할 리스트)를 isPrime으로 전달
               candidate) )
              .add(candidate);  //isPrime 메서드의 결과에 따라 맵에서 알맞은 리스트를 받아 현재 candidate를 추가
        };
    }

    private static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
                     .takeWhile(i -> i <= candidateRoot)
                     .noneMatch(i -> candidate % i == 0);
    }

    /**
     * 알고리즘이 순차적이기에 실제 병렬로 사용할 수는 없지만, 학습용으로 만든 메서드. 여기선 실제 사용되지 않는다.
     * 각 스레드에서 만든 누적 결과를 병합하여 반환한다. (p228)
     */
    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, 
            Map<Boolean, List<Integer>> map2) -> {
                map1.get(true).addAll(map2.get(true)); //두 번째 맵의 소수 리스트를 첫 번째 맵의 소수 리스트에 추가
                map1.get(false).addAll(map2.get(false)); //두 번째 맵의 비소수 리스트를 첫 번째 맵의 비소수 리스트에 추가
            return map1;
        };
    }

    /**
     * 본래 누적자를 최종결과로 변환하는 역할을 하지만, (p227)
     * 여기선 누적자인 accumulator가 이미 최종 결과와 같으므로 변환 과정이 필요 없어 항등 함수를 반환하도록 구현한다.
     */
    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    /**
     * 발견한 소수의 순서에 의미가 있으므로 CONCURRENT, UNORDERED는 해당되지 않고, IDENTITY_FINISH만 설정한다.
     * 추후 최적화를 위한 힌트 제공 (p229)
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
}
````
### Collector 인터페이스를 별도 구현 없이 사용
코드는 간결하지만, 가독성 및 재사용성은 떨어짐
```java
static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollectorV2(int n) {
    return IntStream.rangeClosed(2, n).boxed()
            .collect(
                    () -> new HashMap<Boolean, List<Integer>>() {{    //발행
                        put(true, new ArrayList<>());
                        put(false, new ArrayList<>());
                    }},
                    (acc, candidate) -> {    //누적
                        acc.get(isPrime(acc.get(true), candidate)).add(candidate);
                    },
                    (map1, map2) -> {   //병합
                        map1.get(true).addAll(map2.get(true));
                        map1.get(false).addAll(map2.get(false));
                    }
            );
}
```