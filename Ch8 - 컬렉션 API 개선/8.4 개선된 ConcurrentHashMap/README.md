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
