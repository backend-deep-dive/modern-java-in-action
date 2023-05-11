
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
