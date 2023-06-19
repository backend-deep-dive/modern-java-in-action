## Collection

- 거의 모든 자바 애플리케이션은 컬렉션을 만들고 처리하는 과정을 포함함
- 컬렉션 : 데이터를 그룹화하고 처리할 수 있음 → 대부분의 프로그래밍 작업에 사용됨
- 완벽한 컬렉션 관련 연산을 지원하는 것은 매우 어려움

### Collection + Stream = ?
- 스트림을 사용하면, SQL 질의 언어에서처럼 우리가 기대하는 것이 무엇이지 직접 표현할 수 있다. 즉, 질의를 어떻게 구현해야 할지 명시할 필요가 없으며, 구현은 자동으로 구현된다.
	```sql
	SELECT name FROM dishes WHERE calorie < 400
	```
	
	```java
	List<String> result = menu.stream()
				  .filter(d -> d.getCalories() < 400) 
				  .map(Dish::getName)
				  .collect(toList());
	```

### ParallelStream
- 그러면, 많은 요소를 포함하는 커다란 컬렉션은? → 성능을 높이려면 → 멀티코어 아키텍처로 병렬처리 필요!<br>  ⇒ 이 역시 스트림으로 해결 가능
<br>

# Stream이란?

- Java 8 API에 새로 추가된 기능
- 선언형으로 컬렉션 데이터를 처리할 수 있음 (특히 반복을 멋지게 처리 가능!)
- 멀티스레드 코드르 구현하지 않아도 데이터를 **투명하게 병렬로 처리**할 수 있음

### Stream 적용 전
Java 7 - 저칼로리의 요리명을 반환하고，칼로리를 기준으로 요리를 정렬하기
```java
List<Dish> lowCaloricDishes = new ArrayList<>();

for(Dish dish : menu) {    // 누적자로 요소 필터링
	if(dish.getCalories() < 400) {
		lowCaloricDishes.add(dish);
	}
}

Collections.sort(lowCaloricDishes, new Comparator<Dish>() {    // 익명 클래스로 요리 정렬
	public int compare(Dish dishl, Dish dish2) {
		return Integer.compare(dish1.getCalories(), dish2.getCalories());
	}
});

List<String> lowCaloricDishesName = new ArrayList<>();
for(Dish dish : lowCaloricDishes) {
	lowCaloricDishesName.add(dish.getName());    // 정렬된 리스트를 처리하면서 요리 이름 선택 
}
```

- `lowCaloricDishes` : 가비지 변수. 즉, 컨테이너 역할만 하는 중간 변수

### Stream 적용 후
```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList; 

List<String> lowCaloricDishesName = menu.stream()
					.filter(d -> d.getCalories() < 400)    // 400칼로이 이하의 요리 선택
					.sorted(comparing(Dish:: getCalories))    // 칼로리로 요리 정렬 
					.map(Dish::getName)    // 요리명 추출
					.collect(toList());    // 모든 요리명을 리스트에 저장
```

### +) ParallelStream 적용
멀티코어 아키텍처에서 병렬로 실행할 수 있게 됨 (이로 인한 장점들은 7장에서 설명!)
```java
List<String> lowCaloricDishesName = menu.parallelStream()
					.filter(d -> d.getCalories() < 400)
					.sorted(comparing(Dishes::getCalories))
					.map(Dish::getName)
					.collect(toList());
```

<br>

## Stream이 소프트웨어 공학적으로 주는 이득
- **선언형으로 코드를 구현**할 수 있음
  - 즉, Loop, if문 등의 제어 블록을 사용할 필요 없이 '저칼로리의 요리만 선택하라’ 같은 동작의 수행 지정 가능
  - 선언형 코드와 동작 파라미터화(즉, 람다 표현식)를 활용하면 변하는 요구사항에 쉽게 대응 가능

- 여러 빌딩 블록 연산을 연결해서 **복잡한 데이터 처리 파이프라인**을 만들 수 있다
   ![image](https://user-images.githubusercontent.com/100582309/233820466-b7f88474-07e5-4e6a-b16b-15d96047c7bf.png)
  - 여러 블록을 연결해도 가독성과 명확성이 유지됨
  - filter 메서드의 결과 → sorted 메서드로，다시 sorted 결과 → map 메서드로, map 메서드의 결과 → collect로 연결됨<br><br>
    > 💡 **High-level building block (고수준 빌딩 블록)**<br>
    > - `filter`, `sorted`, `map`, `collect` 등
    > - 특정 스레딩 모델에 제한되지 않고 어떤 상황에서든 자유롭게 사용 가능
    > - 내부 설계 : 단일 스레드 모델에 사용 가능. 다만, 멀티코어 아키텍처를 최대한 투명하게 활용할 수 있게 구현되어 있음
    > - 따라서 **데이터 처리 과정을 병렬화**하면서 **스레드와 락을 걱정할 필요가 없음**!

<br>

### Stream으로 데이터 수집

> 6장에서는 다음과 같은 코드가 가능해진다!

Dish의 종류에 따라 요리를 그룹화하는 코드
```java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
```

```java
{ 
	FISH=[prawns, salmon],
	OTHER=[french fries, rice, season fruit, pizza], 
	MEAT=[porkz beef, chicken]
}
```

<br>

## 정리 - Stream API의 특징

- **선언형**<br>더 간결하고 가독성이 좋아짐

- **조립할 수 있음**<br>유연성이 좋아짐

- **병렬화**<br>성능이 좋아짐

<br>

---

#### 번외1) Collection 제어에 유용한 라이브러리
- **Guava**
  - 구글에서 만든 인기 라이브러리
  - `Multimap`, `Multiset` 등의 추가적인 컨테이너 클래스를 제공

- **Apache Commons Collections**
  - 위와 같은 비슷한 기능 제공

- **Lambdaj**
  - 마리오 푸스코가 만듦
  - (함수형 프로그래밍에서 영감을 받음) 선언형으로 컬렉션을 제어하는 다양한 유틸리티 제공

> Java 8 덕분에 선언형으로 컬렉션을 제어하는 공식 라이브러리들이 생긴 것임!

<br>

#### 번외 2) 다음 챕터부터 사용할 주요 예제 재료

```java
List<Dish> menu = Arrays.asList(
	new Dish("pork", false, 800, Dish.Type.MEAT), 
	new Dish("beef", false, 700, Dish.Type.MEAT), 
	new Dish("chicken", false, 400, Dish.Type.MEAT),
	new Dish("french fries", true, 530z Dish.Type.OTHER),
	new Dish("rice", true, 350, Dish.Type.OTHER), 
	new Dish("season fruit", true, 120, Dish.Type.OTHER), 
	new Dish("pizza", true, 550, Dish.Type.OTHER), 
	new Dish("prawns", false, 300, Dish.Type.FISH), 
	new Dish("salmon", false, 450, Dish.Type.FISH)
);
```

```java
public class Dish { 
	private final String name; 
	private final boolean vegetarian; 
	private final int calories; 
	private final Type type;
	
	public Dish(String name, boolean vegetarian, int calories, Type type) {
		this.name = name; 
		this.vegetarian = vegetarian;
		this.calories = calories; 
		this.type = type;
	} 
	
	public String getName() {
		return name; 
	}
	
	public boolean isVegetarian() {
		return vegetarian;
	}
	
	public int getCalories() {
		return calories;
	}
	
	public Type getType() {
		return type; 
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public enum Type { MEAT, FISH, OTHER }
	
	}
```
