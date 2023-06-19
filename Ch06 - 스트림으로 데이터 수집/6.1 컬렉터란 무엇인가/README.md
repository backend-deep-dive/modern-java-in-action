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
