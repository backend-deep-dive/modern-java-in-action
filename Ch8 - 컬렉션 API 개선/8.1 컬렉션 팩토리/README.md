8장. 컬렉션 API 개선

# 8.1 컬렉션 팩토리


A, B, C를 포함하는 리스트를 만들고 싶다.

```java
List<String> friends = new ArrayList<>();
friends.add("A");
friends.add("B");
friends.add("C");
```

너무 verbose 하다...

```java
List<String> friends = Arrays.asList("A", "B", "C");
```

너무 편하다. 백준에서 많이 본 것 같다.

### UnsupportedOperationException 예외 발생

하지만 위 예시에서

```java
friends.add("D");
```

를 하는 순간 터진다. 고정 크기 배열로 만들어졌기 때문이다...

### Set 만들기

Arrays.asSet() 이라는 메서드가 아쉽게도 없기 때문에,
일단 리스트를 만들고 그것을 HashSet의 생성자로 전달한다.

```java
Set<String> friends = new HashSet<>(
Arrays.asList("A", "B", "C")
);
```

혹은 stream API를 사용한다.

```java
Set<String> friends = Stream.of("A", "B", "C")
.collect(Collectors.toSet());
```
오 깔끔하다.

하지만 두 방법 모두 불필요한 단계 하나씩을 건너야 하고, 매끄럽지 못하고 내부적으로 불필요한 객체 할당을 필요로 한다.

파이썬의 

```python
friends = set(["A", "B", "C"])
```

에 비교하면 너무 코드량이 많다.

다행인 사실은 자바9에서 리스트, 집합, 맵을 쉽게 만들 수 있는 **팩토리 메서드**를 지원한다!


## 8.1.1 리스트 팩토리

### List.of

```java
List<String> friends = List.of("A", "B", "C");
System.out.println(friends);
```

하지만 뭔가 이상하다.

add 를 하면 또 에러가 난다.

위 리스트는 불변 리스트이기 때문이다.

set()을 할 수도 없다. 

null 요소도 들어갈 수 없다.

좀 불편(?)하다.

하지만 이론적으로 아름다운 함수형 프로그래밍을 위해서 감수할 만한 제약 조건이라고 한다.

### 오버로딩

```java
static <E> List<E> of(E e1, E e2, E e3);
static <E> List<E> of(E e1, E e2, E e3, E e4);
static <E> List<E> of(E... elements);
```

이렇게 최적화를 위한 오버로딩이 되어 있다. 최대한 추가 배열 할당을 막기 위해서... 최적화를 위한 자바 API 개발자들의 노력이다...

## 8.1.2 집합 팩토리

```java
Set<String> friends = Set.of("A", "B", "C");

System.out.println(friends);
```

of 를 통해 제공한 요소 중 중복이 있으면 IllegalArgumentException을 내뱉는다.

## 8.1.3 맵 팩토리

```java
Map<String, Integer> ageOfFriends = 
Map.of("A", 1, "B", 2, "C", 3);
```

열 개 이하의 키와 값 쌍을 가진 작은 맵은 이 메소드가 유용하다.

10개 이상의 맵은 Map.Entry<K, V> 객체를 인수로 받는 가변 인수로 구현된 Map.ofEntries 팩토리 메서드를 이용하자.

```java
import static java.util.Map.entry;

Map<String, Integer> ageOfFriends = Map.ofEntries(
entry("A", 1),
entry("B", 2),
entry("C", 3)
);
```
## 퀴즈

```java
List<String> actors = List.of("A", "B");
actors.set(0, "C");
System.out.println(actors);
```

위 코드의 실행 결과는?

<details>
  <summary>답</summary>
  <p>UnsupportedOperationException</p>
</details>

이번 절에서는 리스트, 집합, 맵을 생성하는 법을 배웠다.
8.2절에서는 이들을 처리하는 패턴을 배운다.
