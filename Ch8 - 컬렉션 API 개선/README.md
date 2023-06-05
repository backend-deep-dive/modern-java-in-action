## 담당자

- 8.1 컬렉션 팩토리 : 김대현
- 8.2 리스트와 집합 처리 : 홍승아
- 8.3 맵 처리 : 어정윤
- 8.4 개선된 ConcurrentHashMap : 송민진 
## 진행 날짜
- 2023년 6월 1일 (목)

## 통합 정리
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
# 8.2 리스트와 집합 처리

자바 8에서는 List, Set 인터페이스에 다음과 같은 메서드를 추가했다.

- removeIf : 프레디케이트를 만족하는 요소를 제거한다.
- replaceAll : UnaryOperator 함수를 이용해 요소를 바꾼다. 리스트에서 사용할 수 있다.
- sort : 리스트를 정렬한다.

이 메서드는 새로운 결과를 만드는 스트림 동작과 달리 호출한 기존 컬렉션 자체를 바꾼다.

컬렉션을 바꾸는 동작은 에러를 유발하며 복잡함을 더하기 때문에 자바 8에서는 removeIf와 replaceAll을 추가했다.

## 8.2.1 removeIf 메서드
다음은 숫자로 시작되는 참조 코드를 가진 트랜잭션을 삭제하는 코드다.

```java
for(Transaction transaction : transactions) {
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    transaction.remove(transaction);
  }
}
```

코드를 실행해보면 ConcurrentModificationException을 일으킨다.

forEach 루프는 Iterator 객체를 사용하므로 위 코드는 다음과 같이 해석된다.

```java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
  Transaction transaction = iterator.next();
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    transactions.remove(transaction);
    // 반복자를 사용해 리스트를 순회하면서, 리스트를 직접 수정하고 있음 -> 문제 발생
  }
}
```

Iterator 객체를 통해 소스를 질의하고, Collection 객체 자체에 remove()를 호출해 요소를 삭제하고 있다. 

따라서 반복자의 상태와 컬렉션의 상태가 동기화되지 않는다.

Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출해줘야 정상적으로 동작한다.

```java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
  Transaction transaction = iterator.next();
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    iterator.remove();
  }
}
```

위의 복잡한 코드 패턴은 자바 8의 removeIf 메서드로 바꿀 수 있다. 
removeIf는 삭제할 요소를 가리키는 프레디케이트를 인수로 받는다.

```java
transactions.removeIf(transaction ->
  Character.isDigit(transaction.getReferenceCode().charAt(0))); // True인 경우 제거
```

## 8.2.2 replaceAll 메서드

replaceIf처럼 요소를 제거하는 게 아니라 바꿔야 하는 상황일 때, replaceAll을 사용할 수 있다.

List 인터페이스의 replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.

아래는 스트림 API를 사용한 예제이다.

```java
referenceCodes.stream() // [a12, C14, b13]
    .map(code -> Character.toUpperCase(code.charAt(0)) + code.subString(1))
    // 스트림의 각 요소에 대해 함수를 적용하여 새로운 스트림을 생성
    .collect(Collectors.toList())
    .forEach(System.out::println);
```

```text
// 결과
A12
C14
B13
```

하지만 이 코드는 새 문자열 컬렉션을 만든다. 기존 컬렉션을 바꾸려면 ListIterator 객체를 이용해야 한다.

```java
for(ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {
  String code = iterator.next();
  iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
  // 변경된 값을 iterator.set() 메서드를 사용하여 현재 요소에 대입하여 수정
}
```

코드가 복잡해졌다. 그리고 이처럼 컬렉션 객체와 Iterator 객체를 혼용하면 반복과 변경이 동시에 이루어져 쉽게 문제를 일으킨다.

자바 8의 replaceAll 메서드를 사용하면 간단하게 구현할 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));

```

다음 8.3절에서는 Map 인터페이스에 추가된 새 기능을 설명한다.
# 8.3. 맵 처리

자바 8에서는 Map 인터페이스에 디폴트 메소드를 추가했다.

### 8.3.1. forEach 메소드

맵에서 키와 값을 반복하는 작업을 위해 자바 8에서부터 Map 인터페이스는 BiConsumer(키와 값을 인수로 받음)를 인수로 받는 `forEach` 메소드를 제공한다.

```java
// forEach 메소드 미사용
for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
    String friend = entry.getKey();
    Integer age = entry.getValue();
    System.out.println(friend + " is " + age + " years old");
}

// forEach 메소드 사용
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```

### 8.3.2 정렬 메소드

`Entry.comparingByValue`, `Entry.comparingByKey`를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
        entry("Cristina", "Matrix"),
        entry("Olivia", "James Bond"));

favouriteMovies.entrySet()
        .stream()
        .sorted(Entry.comparingByKey()) // 사람의 이름을 알파벳 순으로 스트림 요소 처리
        .forEachOrdered(System.out::println);
```

> #### HashMap 성능
> 자바 8에서는 HashMap의 내부 구조를 바꿔 성능을 개선했다. 
> 
> 기존의 맵의 항목은 많은 키가 같은 해시코드를 반환하는 상황이 되면 O(n)의 시간이 걸리는 LinkedList로 버킷을 반환해야 하므로 성능이 저하되었다.
> 
> 최근에는 버킷이 너무 커지면 O(log(n))의 시간이 소요되는 정렬된 트리를 이용해 동적으로 치환해 충돌이 일어나는 요소 반환 성능을 개선했다. (키가 Comparable 형태여야 정렬된 트리를 지원)

### 8.3.3 getOrDefault 메서드

키가 존재하지 않으면 결과가 null이 반환되므로 NullPointerException을 방지하기 위해 요청 결과의 null 여부를 확인해야 했다.

기본값을 반환하는 `getOrDefault` 메서드는 첫 번째 인수로 키를, 두 번째 인수로 기본값을 받아 맵에 키가 존재하지 않으면 두 번째 인수로 받은 기본값을 반환한다.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));   // James Bond 출력
System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix"));  // Matrix 출력
```

키가 존재하더라도 값이 null인 상황에서는 `getOrDefault`가 null을 반환할 수 있다.

### 8.3.4 계산 패턴

키 존재 여부에 따라 어떤 동작을 실행하고 결과를 저장해야하는 상황이 필요한 때가 있다.

다음의 3가지 연산이 이런 상황에 도움이 된다.

- `computeIfAbsent` : 제공된 키에 해당하는 값이 없으면(혹은 null) 키를 이용해 새 값을 계산하고 맵에 추가한다.
- `computeIfPresent` : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
- `compute` : 제공된 키로 새 값을 계산하고 맵에 저장한다.

| Map을 이용한 캐시 구현

```java
// 캐시
Map<String, byte[]> dataToHash = new HashMap<>();

// 각 라인을 SHA-256의 해시 값으로 계산해서 저장하기 위한 계산 객체
MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

// 키가 없다면 line과 계산된 해시 값이 key,value로 들어감
lines.forEach(line -> dataToHash.computeIfAbsent(line, this::calculateDigest));

// 키의 해시를 계산해서 반환
private byte[] calculateDigest(String key) {
    return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
}
```

| 키가 존재하지 않으면 값을 반환

아래 코드는 키가 존재하면 기존 값에, 존재하지 않으면 새로운 리스트에 'Star Wars'가 추가 된다.

```java
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
        .add("Star Wars");
```

### 8.3.5 삭제 패턴

자바 8에서는 키가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메서드를 제공한다.

| 자바 8 이전

```java
String key = "Raphael";
String value = "Jack Reacher 2";
if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
   favouriteMovies.remove(key);
   return true;
} else {
   return false;
}
```

| 자바 8 이후

```java
favouriteMovies.remove(key, value);
```

### 8.3.6 교체 패턴

맵의 항목을 바꾸는데 사용하는 메서드

- `replaceAll` : BiFunction을 적용한 결과로 각 항목의 값을 교체한다.
- `replace` : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있다.

```java
Map<String, String> favouriteMovies = new HashMap<>();
favouriteMovies.put("Raphael", "Star Wars"); 
favouriteMovies.put("Olivia", "james bond"); 

favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase()); 
```

### 8.3.7 합침

`putAll` 메서드를 이용하여 두 맵을 합칠 수 있다.

그러나 `putAll`은 중복된 키가 있다면 제대로 동작하지 않는다.

이때는 중복된 키를 어떻게 합칠지 결정하는 `BiFunction`을 인수로 받는 `merge` 메서드를 사용한다.

| putAll 사용

```java
Map<String, String> family = Map.ofEntries(entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(entry("Raphael", "Star Wars"));

Map<String, String> everyone = new HashMap<>(family);
everyone.putAll(friends);
```

| merge 사용

중복된 키가 있으면 두 값을 연결하고 없으면 (key, value) 그대로 저장한다.

```java
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((key, value) -> everyone.merge(key, value, (movie1, movie2) -> movie1 + " & " + movie2));
System.out.println(everyone);
```

| merge를 이용한 초기화 검사

원하는 값이 초기화되어 있으면 +1, 초기화 되어있지 않아 null이면 (movieName, 1) 저장한다.

```java
moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
```
# 8.4 개선된 ConcurrentHashMap

## ConcurrentHashMap

- **동시성 친화적**이며 **최신 기술**을 반영한 HashMap 버전
- 내부 자료구조의 **특정 부분만 잠가** **동시 추가, 갱신 작업**을 허용함
- 동기화된 Hashtable 버전에 비해 **읽기/쓰기 연산 성능이 월등**하다 
- (참고로, 표준 HashMap은 비동기로 동작함)

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable { 

	public V get(Object key) {} 
	
	public boolean containsKey(Object key) { } 
	
	public V put(K key, V value) { 
		return putVal(key, value, false);
	} 
	
	...

}
```

### 참고 : Hashtable Class
```java
public class Hashtable<K,V> extends Dictionary<K,V> implements Map<K,V>, Cloneable, java.io.Serializable { 

	public synchronized int size() { } 
	
	@SuppressWarnings("unchecked") 
	public synchronized V get(Object key) { } 
	
	public synchronized V put(K key, V value) { } }
```
- 메소드 전체에 `synchronized` 키워드가 존재 (메소드 전체가 임계구역으로 설정됨)
- 다만, 동시에 작업을 하려해도 객체마다 Lock을 하나씩 가지고 있기 때문에 동시에 여러 작업을 해야할 때 병목현상이 발생할 수 밖에 없음

### 참고 : HashMap
```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {

	public V get(Object key) {} 
	public V put(K key, V value) {} 

}
```

- `synchronized` 키워드가 존재하지 않음
- `Map` 인터페이스를 구현한 클래스 중에서 성능이 제일 좋다고 할 수 있음
- `Multi-Thread` 환경에서 사용할 수 없다는 특징

<br>

## 8.4.1. 연산
- `forEach` : 각 key-value 쌍에 주어진 액션을 수행
- `reduce` : 모든 key-value 쌍을 제공된 reduce 함수를 이용해 결과로 합침
- `search` : null이 아닌 값을 반환할 때까지 각 key-value 쌍에 함수를 적용

### 연산의 종류
- key-value로 연산 : `forEach`, `reduce`, `search`
- key로 연산 : `forEachKey`, `reduceKeys`, `searchKeys`
- value로 연산 : `forEachValue`, `reduceValues`, `searchValues`
- Map.Entry로 연산 : `forEachEntry`, `reduceEntries`, `searchEntries`


### 연산시 유의사항
- 아래의 연산들은 ConcurrentHashMap의 상태를 잠그지 않고 연산을 수행 → 연산에 제공한 함수는 **계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 함**
- 병렬성 기준값(threshold)를 지정해야 함
  - 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산을 실행함
  - `기준값 = 1` : 공통 스레드 풀을 이용해 병렬성을 극대화함
  - `기준값 = Long.MAX_VALUE` : 한 개의 스레드로 연산을 실행함
  - (SW 아키텍처가 고급 수준의 최적화가 아니라면) 기준값 규칙을 따르는 것이 좋음
  
  ```java
  public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable {
  	private static final int DEFAULT_CAPACITY = 16; // 동시에 업데이트를 수행하는 쓰레드 수 
  	private static final int DEFAULT_CONCURRENCY_LEVEL = 16; 
  }
  ```
  - `DEFAULT_CONCURRENCY_LEVEL` : 동시에 작업 가능한 쓰레드 수
  - `DEFAULT_CAPACITY` : 버킷의 수
  - 즉, 여러 쓰레드에서 ConcurrentHashMap 객체에 동시에 데이터를 삽입, 참조하더라도 그 데이터가 다른 세그먼트에 위치하면 서로 락을 얻기 위해 경쟁하지 않는 것!
  

### 연산 활용 예제
`reduceValues`를 활용하여 맵의 최댓값을 찾는 코드
```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Integer> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
```
- int, long, double 등의 기본값에는 전용 each reduce 연산이 제공됨 → 이를 잘 활용하면 박싱 작업을 할 필요가 없고, 효율적으로 작업을 처리할 수 있음 (ex: `reduceValuesToInt`, `reduceKeysToLong`)

<br>

## 8.4.2. 계수
- **ConcurrentHashMap** 클래스 : Map의 매핑 개수를 반환하는 `mappingCount` 메서드를 제공함
  - 키와 값의 매핑의 수
  - 
- 기존의 size 메서드 대신 새 코드에는 int를 반환하는 `mappingCount` 메서드를 사용하는 것이 좋음 → 매핑의 개수가 int의 범위를 넘어서는 이후의 상황을 대처할 수 있기 때문!

<br>

## 8.4.3. 집합뷰
-  `keySet` : 자기 자신을 집합 뷰로 반환하는 새 메서드
	```java
	// Java code to illustrate the keys() method
	import java.util.*;
	import java.util.concurrent.*;

	public class ConcurrentHashMapDemo {
		public static void main(String[] args)
		{

			// Creating an empty ConcurrentHashMap
			ConcurrentHashMap<Integer, String> hash_map
				= new ConcurrentHashMap<Integer, String>();

			// Mapping string values to int keys
			hash_map.put(10, "Geeks");
			hash_map.put(15, "4");
			hash_map.put(20, "Geeks");
			hash_map.put(25, "Welcomes");
			hash_map.put(30, "You");

			// Displaying the HashMap
			System.out.println("Initial Mappings are: "
							+ hash_map);

			// Using keySet() to get the set view of keys
			System.out.println("The set is: "
							+ hash_map.keySet());
		}
	}
	```

	```
	// output
	Initial Mappings are: {20=Geeks, 25=Welcomes, 10=Geeks, 30=You, 15=4}
	The set is: [20, 25, 10, 30, 15]
	```
- Map을 바꾸면 집합도 바뀌고, 반대로 집합을 바꾸면 맵도 영향을 받음
- `newKeySet` : ConcurrentHashMap으로 유지되는 집합 만들기
	```java
	import java.util.Set;  
	import java.util.concurrent.*;   

	class ConcurrentHashMapnewKeySetExample1 {   
	     public static void main(String[] args)   
	     {   
		 Set<String> hashmap = ConcurrentHashMap.newKeySet();  
		 hashmap.add("AA");  
		 hashmap.add("BBB");   
		 hashmap.add("CCC");   
		 hashmap.add("DDD");    
		 System.out.println(" Mappings : "+ hashmap);   
	     }   
	}
	```
	```
	// output
	Mappings : [AA, CCC, BBB, DDD]
	```
