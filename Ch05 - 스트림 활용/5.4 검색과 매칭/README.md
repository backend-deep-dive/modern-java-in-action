# 5.4 검색과 매칭

```
anyMatch 
(-> bool)

스트림 원소 적어도 하나가 조건에 부합하는지

allMatch
(-> bool)

스트림 원소 모두 조건에 부합하는지

noneMatch 
(-> bool)

스트림 원소 모두 조건에 부합하지 않는지 (allMatch의 정반대)

findAny 
(-> Optional<T>)

스트림 원소 중 조건에 부합하는 원소 아무거나 1개

findFirst 
(-> Optional<T>)

스트림 원소 중 조건에 부합하는 맨 처음 원소 1개
```

## 5.4.1 프레디케이트가 적어도 한 요소와 일치하는지 확인

```java
if (menu.stream().anyMatch(Dish::isVegetarian)) {
	System.out.println("채식 요리가 적어도 하나는 있다!")
}
```

적어도 한 요소는 채식 요리가 있는지 확인한다.

## 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

```java
boolean isHealthy = menu.stream().allMatch(dish -> dish.getCalories() < 1000);
```

```java
boolean isHealthy = menu.stream().noneMatch(dish -> dish.getCalories() >= 1000);
```

위와 아래는 동일한 연산을 한다.

## 쇼트서킷 평가

전체 스트림을 처리하지 않았더라도 결과를 반환할 수 있으면 결과를 반환한다.

anyMatch, allMatch, noneMatch, findFirst, findAny 등의 연산은 모두 쇼트 서킷 연산이다.

무한 스트림에서 유용한 연산이다.

5.7장에서 무한 스트림에 대한 예제를 살펴본다.

참고:
https://kwangyulseo.com/2016/10/19/%ED%95%98%EC%8A%A4%EC%BC%88-%EB%AC%B4%ED%95%9C-%EB%A6%AC%EC%8A%A4%ED%8A%B8/

```haskell
let infinite_integers = [1 .. ]
[1, 2, 3, 4, 5, 6, 7, ... ]
-- 1부터 무한까지 정수 리스트

let infinite_evens = map (*2) infinite_integers
[2, 4, 6, 8, ... ]
-- 2부터 무한까지 짝수의 리스트

take 10 infinite_evens
[2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
--2부터 무한까지 나타내는 짝수 리스트에서 10개를 추출
```

위 코드를 자바로 바꾸면

```java
var infinite_integers = Stream.iterate(1, i -> i + 1);

var infinite_evens = infinite_integers.map(n -> n * 2);

var x = infinite_evens
		.limit(10)
		.collect(Collectors.toList());

x = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
```

이로써 자바도 무한의 개념을 손쉽게 다룰 수 있게 되었다!

## 5.4.3 요소 검색

```java
Optional<Dish> dish = menu.stream()
.filter(Dish::isVegetarian)
.findAny();
```

### Optional이란?

값의 존재나 부재를 표현하는 컨테이너 클래스이다.

프로그래머가 null check하는 것을 까먹으면 대참사가 발생하기 때문에, 프로그래머에게 null check을 강제하는 보조 클래스.

#### isPresent -> true

값이 있으면 참을 반환하고, 없으면 거짓을 반환한다. (비추)

#### ifPresent( T -> void )

값이 있으면 주어진 람다를 실행한다.

#### get()

값이 있으면 값을 반환하고, 없으면 NoSuchElementException을 일으킨다. (비추)

#### orElse(T other)

값이 있으면 값을 반환하고, 없으면 other(기본값)을 반환한다.


## 5.4.4 첫번째 요소 찾기

```java
List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
Optional<Integer> firstSquareDivisibleByThree = someNumbers
.stream()
.map(n -> n * n)
.filter(n -> n % 3 == 0)
.findFirst(); // 9
```

### findFirst vs. findAny

병렬성 때문이다. 요소의 반환 순서가 상관없다면 병렬 스트림에서는 제약이 적은 findAny를 사용한다.

> findFirstN, findAnyN도 있을까?




