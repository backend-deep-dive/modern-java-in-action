
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
