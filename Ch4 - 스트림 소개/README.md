## 담당자

- 4.1 스트림은 무엇인가 : 송민진
- 4.2 스트림 시작하기 : 김대현
- 4.3 스트림과 컬렉션 : 어정윤
- 4.4 스트림과 연산 : 홍승아
## 진행 날짜
- 2023년 4월 23일 (리요일)

## 통합 정리
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

## 정리 - Stream API의 특징

- **선언형**<br>더 간결하고 가독성이 좋아짐
- **조립할 수 있음**<br>유연성이 좋아짐
- **병렬화**<br>성능이 좋아짐

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
# 4.2 스트림 시작하기

## 연속된 요소

콜렉션과의 차점
콜렉션은 데이터 위주, 스트림은 계산, 연산 위주

## 소스

데이터 제공 소스로부터 데이터를 순차적으로 소비한다. 스트림 요소의 순서는 소스 순서와 같다.

## 데이터 처리 연산

데이터베이스와 비슷한 연산을 제공한다.

- map (update)
- filter (= select where)
- reduce (= group by)
- find (select where like %x%)
- match (select where regexp)
- sort (order by)

등으로 데이터를 조작할 수 있다.


## 파이프라이닝

스트림 연산끼리 연결하여 파이프라인을 구성한다.

즉, 
```
stream().
AAA().
BBB().
CCC().
.collect()
```

위와 같은 코드가 있을 때, AAA()가 모두 실행되고 난 다음에야 BBB()가 실행되는 것이 아니라,

**AAA, BBB, CCC로 이루어진 스트림 파이프라인이 구성**되고 난 다음에 스트림이 소스를 소비한다.

이 덕분에 lazy evaluation, short circuit같은 최적화도 쉽게 얻을 수 있다. (5장에서 설명)

## 내부 반복

반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리, 스트림은 내부 반복을 지원한다. 4.3.2절에서 설명

```java
  public static final List<Dish> menu = Arrays.asList(
      new Dish("pork", false, 800, Dish.Type.MEAT),
      new Dish("beef", false, 700, Dish.Type.MEAT),
      new Dish("chicken", false, 400, Dish.Type.MEAT),
      new Dish("french fries", true, 530, Dish.Type.OTHER),
      new Dish("rice", true, 350, Dish.Type.OTHER),
      new Dish("season fruit", true, 120, Dish.Type.OTHER),
      new Dish("pizza", true, 550, Dish.Type.OTHER),
      new Dish("prawns", false, 400, Dish.Type.FISH),
      new Dish("salmon", false, 450, Dish.Type.FISH)
  );
```

```java
  public static void main(String[] args) {
    List<String> names = menu.stream()
        .filter(dish -> {
          System.out.println("filtering " + dish.getName());
          return dish.getCalories() > 300;
        })
        .map(dish -> {
          System.out.println("mapping " + dish.getName());
          return dish.getName();
        })
        .limit(3)
        .collect(toList());
    System.out.println(names);
  }
```

![](attachments/Pasted%20image%2020230423163643.png)

내부 반복이 적용되어 pork 먼저 스트림을 전부 탄 다음에 beef가 stream에 소비되는 것을 알 수 있다 (**내부 반복**).

limit3의 제한에 걸려서 french fries나 rice같은 객체들이 소비되기 전에 스트림 연산이 종료되었다 (**short circuit**).

5장에서 이 예제를 더 자세히 설명한다.

**4.3에서 컬랙션 API와 스트림 API의 차이점에 대해 설명한다.**
# 4.3. 스트림과 컬렉션

공통점

- **연속**된 요소 형식의 값을 저장하는 자료구조 인터페이스 제공

차이점

| |스트림|컬렉션|
|:---:|:---:|:---:|
|데이터 계산 시점|요청할 때만 요소 계산|컬렉션 추가 전 계산|
|데이터 추가 가능|불가능|가능|
|제조 방식|요청 중심 제조/즉석 제조|생상자 중심 제조|
|반복 방식|내부 반복|외부 반복|

## 4.3.1. 딱 한 번만 탐색할 수 있다

반복자와 마찬가지로 스트림도 **한 번만** 탐색할 수 있다. (탐색된 스트림의 요소는 소비됨)

## 4.3.2. 외부 반복과 내부 반복

컬렉션 인터페이스는 사용자가 직접 요소를 반복해야 한다. (외부 반복, external iteration)

스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가 저장해준다. (내부 반복, internal iteration)

| 컬렉션: for-each 루프 이요한 외부 반복

```java
List<String> names = new ArrayList<>();
for (Dish dish : menu) {
    names.add(dish.getName());
}
```

for-each 구문을 이용하면 Iterator 객체 이용할 때보다 더 쉽게 컬렉션을 반복할 수 있다.

| 컬렉션: 내부적으로 숨겨진 반복자를 사용한 외부 반복

```java
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while (iterator.hasNext()) {    // 명시적 반복
    Dish dish = iterator.next();
    names.add(dish.getName());
}
```

| 스트림: 내부 반복

```java
List<String> names = menu.stream()
        .map(Dish::getName) // 요리명 추출
        .collect(Collectors.toList());  // 파이프라인 실행(반복자 필요 없음)
```

내부 반복이 더 좋은 이유

1. 작업을 투명하게 병렬로 처리할 수 있다.
   
2. 더 최적화된 다양한 순서로 처리할 수 있다.

외부 반복에서는 병렬성을 <U>**스스로 관리**</U>해야 한다.

병렬성 스스로 관리란

- 병렬성 포기

- `synchronized`로 관리

스트림은 내부 반복을 사용하므로 반복 과정을 우리가 신경 쓰지 않아도 된다.

하지만 이와 같은 이점을 누리려면 filter나 map 같이 반복을 숨겨주는 연산 리스트가 미리 정의되어 있어야 한다. (동작 파라미터화 활용 가능)
# 4.4. 스트림 연산

스트림 인터페이스의 연산은 크게 두 가지로 구분할 수 있다.

- 중간 연산 itermediate operation
    
    연결할 수 있는 스트림 연산
    
    filter, map, limit 등으로 서로 연결하여 파이프라인을 형성한다.
    
- 최종 연산 terminal operation
    
    스트림을 닫는 연산
    
    collect로 파이프라인을 실행한 다음 닫는다.
    

```java
List<String> names = menu.stream() // 요리 리스트에서 스트림 얻기
   .filter(dish -> dish.getCalories() > 300) // 중간 연산
   .map(Dish::getName) // 중간 연산
   .limit(3) // 중간 연산
   .collect(toList()); // 스트림을 리스트로 변환
```

![](4.4 스트림 연산/attachments/Untitled.png)

## 4.4.1. 중간 연산

filter나 sorted 같은 중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해 질의로 만들 수 있다.

중간 연산의 중요한 특징은 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것, 즉 게으르다(lazy)는 것이다. 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한번에 처리하기 때문이다.

> 람다가 현재 처리 중인 요리를 출력해서 스트림 파이프라인 확인
> 

```java
List<String> names = menu.stream()
        .filter(dish -> {
          System.out.println("filtering " + dish.getName());
          return dish.getCalories() > 300;
        })
        .map(dish -> {
          System.out.println("mapping " + dish.getName());
          return dish.getName();
        })
        .limit(3)
        .collect(toList());
    System.out.println(names);
```

스트림의 게으른 특성 덕분에 **쇼트서킷**, **루프 퓨전** 등의 최적화 효과를 얻을 수 있다.(다음 장에서 자세히 설명한다)

> - 쇼트 서킷: 모든 것을 iteration 하지 않고 일찍 끝나는 것
> - 루프 퓨전: 다른 연산이 한 과정으로 병합되는 것
> - ex. steam.filter.map.limit 의 코드가 있으면 filter, map 이 한 과정으로 병합되고, limit 이 쇼트서킷 된다.
> 

## 4.4.2. 최종 연산

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 이를 통해 List, Integer, void 등 스트림 이외의 결과가 반환된다.

아래의 forEach는 menu의 각 요리에 람다를 적용한 다음 void를 반환하는 최종 연산이다. System.out.println을 forEach에 넘겨주면 menu에서 만든 스트림의 모든 요리를 출력한다.

```java
menu.stream().forEach(System.out::println);
```
> 1. stream() 메서드를 사용하여 List<Dish> 객체 menu를 Dish 객체의 스트림으로 변환한다. 이렇게 하면 중간 스트림 작업이 생성된다.
> 2. 스트림에 forEach 최종 연산을 적용한다. (System.out::println 람다식을 인수로 사용)
> 3. forEach 작업은 스트림의 각 요소를 반복하며, 각 Dish 개체에 대해 println 메서드가 호출된다.

> 이 코드는 `menu` 목록의 각 `Dish` 개체를 가져와서 `println` 메서드를 사용하여 콘솔에 출력한 다음 모든 개체가 처리될 때까지 다음 `Dish` 개체로 이동한다.

## 4.4.3. 스트림 이용하기

스트림 이용 과정은 세 가지로 요약할 수 있다.

- 질의를 수행할 (컬렉션 같은) 데이터 소스
- 스트림 파이프라인을 구성할 중간 연산 연결
- 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

**표 4-1. 중간 연산**

| 연산 | 형식 | 반환 형식 | 연산의 인수 | 함수 디스크립터 |
| --- | --- | --- | --- | --- |
| filter | 중간 연산 | Stream<T> | Predicate<T> | T -> boolean |
| map | 중간 연산 | Stream<R> | Function<T, R> | T -> R |
| limit | 중간 연산 | Stream<T> |  |  |
| sorted | 중간 연산 | Stream<T> | Comparator<T> | (T, T) -> int |
| distinct | 중간 연산 | Stream<T> |  |  |

**표 4-2. 최종 연산**

| 연산 | 형식 | 반환 형식 | 목적 |
| --- | --- | --- | --- |
| forEach | 최종 연산 | void | 스트림의 각 요소를 소비하면서 람다를 적용한다 |
| count | 최종 연산 | long(generic) | 스트림의 요소 개수를 반환한다 |
| collect | 최종 연산 |  | 스트림을 소비해서 리스트, 맵, 정수 형식의 컬렉션을 만든다 |