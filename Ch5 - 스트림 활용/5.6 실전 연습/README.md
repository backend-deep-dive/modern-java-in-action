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