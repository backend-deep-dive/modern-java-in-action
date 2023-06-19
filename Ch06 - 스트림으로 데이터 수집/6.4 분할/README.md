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