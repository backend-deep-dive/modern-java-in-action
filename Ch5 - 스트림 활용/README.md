## 담당자

- 5.1 필터링 : 박상욱
- 5.2 스트림 슬라이싱 : 송민진 
- 5.3 매핑 : 어정윤
- 5.4 검색과 매칭 : 김대현 
- 5.5 리듀싱 : 이홍섭
- 5.6 실전 연습 : 홍승아
- 5.7 숫자형 스트림 : 어정윤
- 5.8 스트림 만들기 : 박상욱
## 진행 날짜
- 2023년 4월 27일 (목요일)
- 2023년 5월 11일 (목요알)
## 통합 정리

스트림 API는 내부 반복 뿐 아니라 코드를 병렬로 실행할지 여부도 결정할 수 있다.
이러한 일은 순차적인 반복을 단일 스레드로 구현하는 외부 반복으로는 달성할 수 없다.

스트림 API가 지원하는 연산을 이용해서
- 필터링
- 슬라이싱
- 매핑
- 검색
- 매칭
- 리듀싱
등 다양한 데이터 처리 질의를 표현할 수 있다.

**5.1 에서는 스트림의 요소를 선택하는 방법**
**즉, 프레디케이트 필터링 방법과 고유 요소만 필터링하는 방법을 배워보자**

## 5.1.1 - 프레디케이트로 필터링

스트림 인터페이스는 filter 메서드를 지원한다.

	Menu Stream 중 Predicate 를 인수로 넣었을 때 true로 일치하는 모든 요소 스트림을 반환

```java
Stream<T> filter(Predicate<? super T> predicate);
```

```java
// .filter() -> Predicate<T>를 파라미터로 받아서 Predicate 가 true 인 모든 요소를 포함하는 스트림을 반환하는 메소드  
// .collect() -> Stream의 데이터를 변형 등의 처리를 하고 원하는 자료형으로 변환해 준다. (List, Set 등) 

List<Dish> vegetarianMenuLam = Dish.menu.stream()  
        .filter(dish -> dish.isVegetarian())  
        .collect(Collectors.toList());  
  
List<Dish> vegetarianMenuRef = Dish.menu.stream()  
        .filter(Dish::isVegetarian)  
        .collect(Collectors.toList());  
  
// vegetarianMenuLam -> [french fries, rice, season fruit, pizza]  
// vegetarianMenuRef -> [french fries, rice, season fruit, pizza]
```

![스크린샷 2023-04-26 오후 1 51 35](https://user-images.githubusercontent.com/96435200/234473044-e8e5742f-8e33-4967-97fc-c777b3bac0a4.png)

---

## 5.1.2 - 고유 요소 필터링

스트림은 고유 요소로 이루어진 스트림을 반환하는 distinct 메서드도 지원한다.
(고유 여부는 스트림에서 만든 객체의 hashCode, equals로 결정된다.)

```java
List<Integer> numbers = Arrays.asList(1,2,1,3,3,2,4);  
numbers.stream()  
        .filter(i -> i % 2 == 0)  
        .distinct()  
        .forEach(System.out::println);
```

![스크린샷 2023-04-26 오후 2 04 03](https://user-images.githubusercontent.com/96435200/234474549-a1e8d1d2-488c-4c37-8dab-b52282a99464.png)

# 스트림 슬라이싱

## 1. Predicate를 이용한 슬라이싱

Predicate 조건을 활용해서, 스트림의 요소를 효과적으로 슬라이싱하여 선택할 수 있는 기능들

### takeWhile
: 조건이 어긋나면 슬라이싱하는 메서드

#### 특징
- Java 9부터 지원하는 메서드
- 무한 스트림에서도 동작함

> **filter와의 차이**
> - `filter` : 조건에 대해 다 검사하며 참인것만 다음으로 넘어감
> - `takeWhile` : 조건에 대해 참이 아닐경우 바로 거기서 멈춤

```java
List specialMenu = Arrays.asList( 
	new Dish("seasonal fruit", true, 120, Dish.Type.OTHER), 
	new Dish("prawns", false, 300, Dish.Type.FISH), 
	new Dish("rice"z true, 350, Dish.Type.OTHER), 
	new Dish("chicken", false, 400, Dish.Type.MEAT), 
	new Dish("french fries", true, 530, Dish.Type.OTHER));
```

위와 같이 칼로리가 오름차순으로 정렬되어 있는 리스트의 경우, 320칼로리가 넘을 때 끊는 것이 효율이 더 좋다.

```java
List slicedMenul = specialMenu.stream()
							  .takeWhile(dish -> dish.getCalories() < 320)
							  .collect(toList());
```

### dropWhile
: Predicate가 false가 등장하는 시점부터, 남은 요소들만 반환함
- `takeWhile`과 반대되는 기능

#### 특징
- Java 9부터 지원하는 메서드
- 무한 스트림에서도 동작함

<br>

## 2. Stream 축소

### skip(n)
: 처음 n개의 요소를 제외한 스트림을 반환하는 메서드

#### 특징
- n개 이하의 요소를 포함하는 스트림에 `skip(n)`을 호출하면 빈 스트림 반환됨
- `limit(n)`과 상호 보완적인 연산을 수행함

```java
List dishes = menu.stream() 
				  .filter(d -> d.getCalories() > 300)
				  .skip(2)
				  .collect(toList());
```
# 5.3. 매핑

스트림 API의 map과 flatMap 메소드는 특정 데이터를 선택하는 기능을 제공한다.

## 5.3.1. 스트림의 각 요소에 함수 적용하기

스트림은 함수를 인수로 받은 map 메소드를 지원한다.

인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.

```java
List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(Collectors.toList());
```

## 5.3.2. 스트림 평면화

영단어가 담긴 리스트에서 각 단어의 알파벳을 포함하는 리스트를 반환한다고 가정하자.

예를 들어, ["Hello", "World"]에서 ["H", "e", "l", "o", "W", "r", "d"]를 포함하는 리스트가 반환되어야 한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .distinct()                     // Stream<String[]>
        .collect(Collectors.toList());  // List<String[]>
```

위 코드에서 map으로 전달한 람다는 각 단어의 String[]을 반환해서 원하는 반환값을 얻을 수 없다.

문자열 배열을 받아 문자열 스트림을 만드는 `Arrays.stream()` 메소드를 사용해보자.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .map(Arrays::stream)            // Stream<Stream<String>>
        .distinct()                     // Stream<Stream<String>>
        .collect(Collectors.toList());  // List<Stream<String>>
```

위 방법은 원하던 결과인 List<String>이 아닌 List<Stream<String>>을 반환해준다.

### flapMap 사용

flatMap 메소드는 스트림의 각 값을 다른 스트림으로 만든 다음 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .flatMap(Arrays::stream)        // Stream<String>
        .distinct()                     // Stream<String>
        .collect(Collectors.toList());  // List<String>
```
# 5.3. 매핑

스트림 API의 map과 flatMap 메소드는 특정 데이터를 선택하는 기능을 제공한다.

## 5.3.1. 스트림의 각 요소에 함수 적용하기

스트림은 함수를 인수로 받은 map 메소드를 지원한다.

인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.

```java
List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(Collectors.toList());
```

## 5.3.2. 스트림 평면화

영단어가 담긴 리스트에서 각 단어의 알파벳을 포함하는 리스트를 반환한다고 가정하자.

예를 들어, ["Hello", "World"]에서 ["H", "e", "l", "o", "W", "r", "d"]를 포함하는 리스트가 반환되어야 한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .distinct()                     // Stream<String[]>
        .collect(Collectors.toList());  // List<String[]>
```

위 코드에서 map으로 전달한 람다는 각 단어의 String[]을 반환해서 원하는 반환값을 얻을 수 없다.

문자열 배열을 받아 문자열 스트림을 만드는 `Arrays.stream()` 메소드를 사용해보자.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .map(Arrays::stream)            // Stream<Stream<String>>
        .distinct()                     // Stream<Stream<String>>
        .collect(Collectors.toList());  // List<Stream<String>>
```

위 방법은 원하던 결과인 List<String>이 아닌 List<Stream<String>>을 반환해준다.

### flapMap 사용

flatMap 메소드는 스트림의 각 값을 다른 스트림으로 만든 다음 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .flatMap(Arrays::stream)        // Stream<String>
        .distinct()                     // Stream<String>
        .collect(Collectors.toList());  // List<String>
```
# 5.5 리듀싱

- 리듀싱이라는 단어는 “줄이다”라는 의미를 가진다.
- 스트림에서 리듀싱은 스트림의 모든 요소를 하나의 값으로 줄이는 작업을 수행한다.
- `메뉴에서 칼로리가 가장 높은 요리는?`,`메뉴의 모든 칼로리의 합계는?`처럼 모든 스트림 요소를 반복적으로 처리해서 결과를 도출하는 작업을 수행할 수 있다.

## 사용 목적

- 리듀싱은 대개 스트림의 모든 요소를 하나의 값으로 합치기 위해 사용된다.
    - 총합
    - 평균값
    - 최댓값
    - 최솟값

`reduce`는 두 개의 인수를 갖는다.

- 초기값
- 스트림의 두 요소를 합쳐서 하나의 값으로 만드는데 사용할 람다

## 5.5.1 요소의 합

먼저 for-each 루프를 이용해서 리스트의 숫자 요소를 더하는 코드를 확인해보자.

```java
int sum = 0;
for (int x : numbers) {
	sum += x;
}
```

위 코드는 reduce를 사용해서 다음과 같이 변경할 수 있다.

```java
int sum = numbers.stream().reduce(0, (a,b) -> a + b);
```

메서드 참조로 Integer 클래스의 sum 메서드를 사용하면 더 간결하게 구현 가능하다.

```java
int sum = numbers.stream().reduce(0, Integer::sum);
```

### 초기값 없음

초기값을 받지 않도록 오버로드된 reduce도 있다. 하지만 이 reduce는 Optional 객체를 반환한다.

```java
Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
```

스트림에 아무 요소도 없다면 초깃값이 없으므로 reduce는 합계를 반환할 수 없기 때문이다.

## 5.5.2 최댓값과 최솟값

reduce 연산은 새로운 값을 이용해서 스트림의 모든 요소를 소비할 때까지 람다를 반복 수행한다.

이를 통해 최댓값과 최솟값을 찾을 때도 reduce를 활용할 수 있다.

```java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
```

### reduce 메서드의 장점
기존의 단계적 반복으로 합계를 구하는 것 vs reduce를 이용하는 것의 차이
- 스트림의 reduce 사용시 : 내부 반복이 추상화되면서 내부 구현에 벙렬로 reduce를 실행할 수 있게 된다.
- 기존의 단계적 반복 : 예) sum 변수를 공유해야 하므로 쉽게 병렬화하기 어렵다.

### 병렬 처리시 주의 사항
>reduce의 초기값으로 인해서 의도되지 않은 결과가 나올 수 있다.<br>
>`parallel()` 키워드를 붙임으로써 병렬로 처리되도록 할 수 있다.<br>
> reduce가 아니더라도 병렬 처리를 했을 때 결과에 영향을 줄지에 대한 확인이 반드시 필요하다.
### 스트림 연산 : 상태 있음과 상태 없음
- 상태 있음 : 이전 요소와 관련된 정보를 저장하는 데 사용되는 것
- 상태 없음 : 각 요소를 독립적으로 처리하며, 이전 요소와 관련된 정보를 유지하지 않는 

스트림 연산은 각각 다양한 작업을 수행한다. 따라서 각각의 연산은 내부적인 상태를 고려해야한다.

`map`, `filter`등은 입력 스트림에서 각 요소를 받아 0 또는 결과를 출력 스트림으로 보낸다.<br>
따라서 이들은 보통 상태가 없는, 즉 `내부 상태를 갖지 않는 연산(stateless operation)`이다.

`reduce`, `sum`, `max` 같은 연산은 결과를 누적할 내부 상태가 필요하다. 하지만 내부 상태는 int, double등과 같이 작은 값이며, 스트림에서 처리하는 요소 수와 관계없이 한정(bounded)되어 있다.

반면 `sorted`나 `distinct` 같은 연산을 수행하기 위해서는 과거의 이력을 알고있어야 한다.<br> 
예를 들어 어떤 요소를 출력스트림으로 추가하려면 `모든 요소가 버퍼에 추가되어 있어야 한다.`<br>
따라서 데이터 스트림의 크기가 크거나 무한이라면 문제가 생길 수 있다. 이러한 연산을 `내부 상태를 갖는 연산(stateful opertaion)`이라 한다.
>상태 있음 연산은 일부 작업을 수행할 때 유용하다.<br>
>반면에 상태 없음 연산은 각 요소를 독립적으로 처리하고 이전 요소와 관련된 정보를 유지하지 않아도 되므로, 병렬 처리에 적합하며 데이터 처리 파이프라인에서 사용하기에 효율적이다.

## 5.6. 실전 연습

지금까지 배운 스트림을 실제 사용해서 아래 질문들을 해결해보자.

1. 2011년에 일어난 모든 트랜잭션을 찾아 오름차순으로 정렬하시오.
2. 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.
3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.
4. 모든 거래자의 이름을 알파벳 순으로 정렬해서 반환하시오.
5. 밀라노에 거래자가 있는가?
6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.
7. 전체 트랜잭션 중 최댓값은 얼마인가?
8. 전체 트랜잭션 중 최솟값은 얼마인가?

다음과 같은 거래자(Trader) 리스트와 트랜잭션(Transaction) 리스트를 이용한다.

```java
Trader raoul = new Trader("Raoul", "Cambridge");
Trader mario = new Trader("Mario", "Milan");
Trader alan = new Trader("Alan", "Cambridge");
Trader brian = new Trader("Brian", "Cambridge");

List<Transaction> transactions = Arrays.asList(
    new Transaction(brian, 2011, 300),
    new Transaction(raoul, 2012, 1000),
    new Transaction(raoul, 2011, 400),
    new Transaction(mario, 2012, 710),
    new Transaction(mario, 2012, 700),
    new Transaction(alan, 2012, 950)
);
```

클래스 정의는 다음과 같다.

**Trader.java**

```java
public class Trader {

    private String name;
    private String city;

    public Trader(String n, String c) {
        name = n;
        city = c;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

  @Override
  public String toString() {
    return String.format("Trader: "+this.name + " in " + this.city);
  }

}
```

**Transaction.java**

```java
public class Transaction {

    private Trader trader;
    private int year;
    private int value;

    public Transaction(Trader trader, int year, int value) {
        this.trader = trader;
        this.year = year;
        this.value = value;
    }

    public Trader getTrader() {
        return trader;
    }

    public int getYear() {
        return year;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return "{" + this.trader + ", " + "year: " + this.year + ", " + "value: " + this.value + "}";
    }
}
```

### 1. 2011년에 일어난 모든 트랜잭션을 찾아 오름차순으로 정렬하시오.

```java
List<Transaction> tr2011 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011) // 2011년에 발생한 트랜잭션을 필터링하도록 프레디케이트를 넘겨줌
                .sorted(comparing(Transaction::getValue)) // 트랜잭션 값으로 요소 정렬
                .collect(toList()); // 결과 리스트의 모든 요소를 리스트로 반환
---
[{Trader: Brian in Cambridge, year: 2011, value: 300}, {Trader: Raoul in Cambridge, year: 2011, value: 400}]
```

### 2. 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.

```java
List<String> cities = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity()) // 트랜잭션과 관련한 각 거래자의 도시 추출
                .distinct()
                .collect(toList());
---
[Cambridge, Milan]
```

distinct() 대신 스트림을 집합으로 변환하는 toSet()을 사용할 수 있다. 6장에서 설명한다.

```java
import static java.util.stream.Collectors.toSet;

Set<String> cities2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .collect(toSet());
---
[Milan, Cambridge]
```

### 3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.

```java
List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader) // 트랜잭션의 모든 거래자 추출
                .filter(trader -> trader.getCity().equals("Cambridge")) // Cambridge의 거래자만 선택
                .distinct() // 중복 제거
                .sorted(comparing(Trader::getName)) // 결과 스트림의 거래자를 이름으로 정렬
                .collect(toList());
---
[Trader: Alan in Cambridge, Trader: Brian in Cambridge, Trader: Raoul in Cambridge]
```

### 4. 모든 거래자의 이름을 알파벳 순으로 정렬해서 반환하시오.

```java
String traderStr = transactions.stream()
                .map(transaction -> transaction.getTrader().getName()) // 모든 거래자의 이름을 문자열 스트림으로 추출
                .distinct() // 중복된 이름 제거
                .sorted() // 이름을 알파벳 순으로 정렬
                .reduce("", (n1, n2) -> n1 + n2); // 각각의 이름을 하나로 연결하여 결국 모든 이름을 연결
---
AlanBrianMarioRaoul
```

위 코드는 각 반복 과정에서 모든 문자열을 반복적으로 연결해서 새로운 문자열 객체를 만들기 때문에 효율성이 부족하다. 6장에서는 `joining()`을 이용해 더 효율적으로 해결하는 방법을 설명한다. (`joining()`은 내부적으로 `StringBuilder`를 이용한다.)

```java
boolean milanBased = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan")); // anyMatch에 프레디케이트를 전달해서 밀라노에 거래자가 있는지 확인
        System.out.println(milanBased);
```

```java
import static java.util.stream.Collectors.joining;

String traderStr = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .collect(joining());
```

### 5. 밀라노에 거래자가 있는가?

```java
boolean milanBased = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan")); // anyMatch에 프레디케이트를 전달해서 밀라노에 거래자가 있는지 확인
---
true
```

### 6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.

```java
transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity())) // Cambridge에 거주하는 거래자의 트랜잭션을 선택
                .map(Transaction::getValue) // 이 거래자들의 값 추출
                .forEach(System.out::println); // 각 값을 출력
---
300
1000
400
950
```

### 7. 전체 트랜잭션 중 최댓값은 얼마인가?

```java
int highestValue = transactions.stream()
                .map(Transaction::getValue) // 각 트랜잭션의 값 추출
                .reduce(0, Integer::max); // 결과 스트림의 최댓값 계산
---
1000
```

### 8. 전체 트랜잭션 중 최솟값은 얼마인가?

```java
Optional<Transaction> smallestTransaction = transactions.stream()
                .reduce((t1, t2) ->
                    t1.getValue() < t2.getValue() ? t1: t2); // 각 트랜잭션 값을 반복 비교해서 가장 작은 트랜잭션 검색
---
Optional[{Trader: Brian in Cambridge, year: 2011, value: 300}]
```

스트림은 Comparator를 인수로 받는 min과 max 메서드를 제공한다.

```java
Optional<Transaction> smallestTransaction2 = transactions.stream()
                .min(comparing(Transaction::getValue));
System.out.println(smallestTransaction2);
System.out.println(smallestTransaction2.map(String::valueOf).orElse("No transactions found"));
---
Optional[{Trader: Brian in Cambridge, year: 2011, value: 300}]
{Trader: Brian in Cambridge, year: 2011, value: 300}
```
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
