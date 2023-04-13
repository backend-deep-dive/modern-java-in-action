# Method Reference (메서드 참조)


## 메서드 참조란?

- Java 8의 새로운 기능
- 특정 메서드만을 호출하는 람다 표현식의 축약형
- 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다.

- 때로는 람다 표현식보다 더 가독성이 좋으며 자연스러울 수 있다.
	- 람다 표현식
		```java
		inventory.sort((Apple a1, Apple a2) ->
						a1,getWeight().compareTo(a2.getWeight()));
		```
	
	- 메서드 참조 적용 후
		```java
		inventory.sort(comparing(Apple::getWeight));
		```

> 📌 **언제 유용한가?** <br>
> - 람다가 "이 메서드를 직접 호출해!"라고 명령할 경우 → 메서드명을 직접 참조하는 것이 편리함 <br>
> - 이 때 명시적으로 메서드명을 참조할 수 있음 → **가독성 향상!**

<br>

## 활용 방법
**구분자 `::` 붙이기** (괄호 필요 X)
- `Apple::getWeight` : Apple 클래스에 정의된 `getWeight`의 메서드 참조<br>
   (= 람다표현식 `(Apple a) -> a.getWeight()`)

<br>

## 예제
- **예시 1**
	- 람다 표현식
	```java
	() -> Thread.currentThread().dumpStack()
	```
	
	- 메서드 참조 적용 후
	```java
	Thread.currentThread()::dumpStack
	```

- **예시 2**
	- 람다 표현식
	```java
	(str, i) -> str.substring(i)
	```
	
	- 메서드 참조 적용 후
	```java
	String::substring
	```

- **예시 3**
	- 람다 표현식
	```java
	(String s) -> System.out.printIn(s) (String s) System.out::printin
	-> this.isValidName(s)
	```
	
	- 메서드 참조 적용 후
	```java
	System.out::printin
	this::isValidName
	```

> 💡 **참고**<br>
> 즉, 메서드 참조는<br>
> - 새로운 기능 X<br>
> - 하나의 메서드를 참조하는 람다를 더 편리하게 표현할 수 있는 문법!<br>
> - 같은 기능을 더 간결하게 구현할 수 있다!

<br>

## 람다 표현식 -> 메서드 참조

### 대표 예제

> **배경 지식**
> - `List`의 `sort` 메서드 : 인수로 Comparator을 기대한다.
> - `Comparator` : `(T, T) -> int`라는 함수 디스크립터를 갖는다.
> - `String` 클래스 : `compareToIgnoreCase`라는 메서드를 갖는다.

**람다 표현식**
```java
List<String> str = Arrays.asList("a","b","A","B");
str.sort((s1, s2) -> s1.compareToIgnoreCase(s2));
```

**메서드 참조로 변경**
```java
List<String> str = Arrays.asList("a","b","A","B");
str.sort(String::compareToIgnoreCase)；
```

<br>

>💡 **알고 가기**
> - 컴파일러는 **메서드 참조**가 **주어진 함수형 인터페이스**와 호환하는지 확인한다. (like 람다 표현식의 형식을 검사하던 방식)
> - 따라서, 메서드 참조는 콘텍스트 형식과 일치해야 한다.


<br>

## 메서드 참조 방법

1. **정적 메서드 참조**
    - `Integer::parseInt`
    
2. **다양한 형식의 인스턴스 메서드 참조**
    - `String::length`
    - 메서드 참조를 이용해서 람다 표현식의 파라미터로 전달할 때 사용

3. **기존 객체의 인스턴스 메서드 참조**
    - `expensiveTransaction::getValue` 
       (Transaction 객체를 할당받은 expensiveTransaction 지역변수가 있고, 
        Transaction 객체에는 getValue 메서드가 있음)
    -  현존하는 외부 객체의 메서드를 호출할 때 사용
    - 비공개 헬퍼 메서드를 정의한 상황에서 유용하게 활용 가능
   ```java
   private boolean isValidName(String string) {
	   return Character.isUpperCase(string.charAt(0));}
   ```
       
	```java
	filter(words, this::isValidName)
	```
<br>

![image](https://user-images.githubusercontent.com/100582309/231714945-614e8c1b-093f-4889-95a1-7a9e722d85c5.png){:width="30%" height="30%"}

<br>

### 예제 1 - ①번 방법
: 정적 메서드 참조

**람다 표현식**
```java
ToIntFunction<String> stringToInt = (String s) -> Integer.parselnt(s);
```

**메서드 참조로 변경**
```java
Function<String, Integer> stringToInteger = Integer::parselnt;
```

<br>

### 예제 2 - ②번 방법
: 다양한 형식의 인스턴스 메서드 참조

**람다 표현식**
```java
BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
```

**메서드 참조로 변경**
```java
BiPredicate<List<String>, String> contains = List::contains;
```

<br>

### 예제 3 - ③번 방법
: 기존 객체의 인스턴스 메서드 참조 (비공개 헬퍼 메서드에 유용)

**람다 표현식**
```java
Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);
```

**메서드 참조로 변경**
```java
Predicate<String> startsWithNumber = this::startsWithNumber
```

<br>

## 생성자 참조

`new` 키워드를 이용해서 기존 생성자의 참조를 만들 수 있다. 
(정적 메소드의 참조를 만드는 방법과 비슷!)

- **case 1** : 기본 생성자 참조
	```java
	Supplier<Apple> c1 = Apple::new;
	Apple a1 = c1.get();          // Supplier의 get 메서드를 호출해서 새로운 Apple 객체를 만들 수 있음
	```

	> 참고 : 람다 표현식 방식
	> ```java
	> Supplier c1 = () -> new Apple();        // 디폴트 생성자를 가진 Apple 생성
	> Apple 0000000000
	> a1 = c1.get();
	> ```

<br>

- **case 2** : 인수가 있는 생성자 참조
	```java
	Function<Integer, Apple> c2 = Apple::new;    // Apple(Integer weight)의 생성자 참조!
	Apple a2 = c2.apply(110);
	```

<br>

- **case 2-1** : 여러 무게들을 담은 리스트를 활용하여, 다양한 무게의 사과 리스트 생성하기
	```java
	List weights = Arrays.asList(7, 3, 4, 10);
	List<Apple> apples = map(weights, Apple::new);
	public List<Apple> map(List<Integer> list, Function<Integer, Apple> f) {
		List<Apple> result = new ArrayList<>();
		for(Integer i : list) {
			result.add(f.apply(i));
		}
		return result;
	}
	```

<br>

- **case 3** : 2개의 인수를 갖는 생성자 참조
  - `BiFunction` 인터페이스와 같은 시그니처 -> `BiFunction` 활용
	```java
	BiFunction<Color, Integer, Apple> c3 = Apple::new;
	Apple a3 = c3.apply(GREEN, 110);
	```

<br>

- **case 3-1** : String으로 과일 종류, Integer로 무게를 줬을 때, 해당 종류의 과일을 생성하기
	```java
	static Map<String, Function<Integer, Fruit>> map = new HashMap<>();
	static {
		map.put("apple", Apple::new);
		map.put("orange", Orange::new);
	}
	```

	```java
	public static Fruit giveMeFruit(String fruitType, Integer weight) {
		return map.get(fruitType.toLowerCase())
				  .apply(weight);
	}
	```

<br>

- **case 4** : 인수가 3개 이상인 생성자 참조는?<br>
  현재 이런 시그니처를 갖는 함수형 인터페이스는 제공되지 않음 -> 직접 다음과 같은 함수형 인터페이스를 만들어야 한다.
	```java
	public interface TriFunction<T, U, V, R> {
		R apply(T t, U u, V v);
	}
	```

	```JAVA
	TriFunction<Integer, Integer, Integer, Color> colorFactory = Color::new;
	```
