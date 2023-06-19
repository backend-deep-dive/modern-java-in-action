
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

