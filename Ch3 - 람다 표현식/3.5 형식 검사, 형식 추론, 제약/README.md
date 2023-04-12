# 3.5 형식 검사, 형식 추론, 제약 

## 3.5.1 형식 검사

람다가 사용되는 콘텍스트를 이용해서 람다의 형식(Type)을 추론할 수 있다.
어떤 콘텍스트(문맥, 예를 들면 람다가 전달될 메서드 파라미터나 람다가 할당되는 변수 등)에서
기대되는 람다 표현식의 형식을 **대상 형식**이라고 부른다.

```java
List<Apple> heavierThan150g = 
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

다음과 같은 순서로 형식 확인 과정이 진행된다.

	1. filter 메서드의 선언을 확인한다.
	2. filter 메서드는 두 번째 파라미터로 Predicate<Apple> 형식(대상 형식)을 기대한다.
	3. Predicate<Apple>은 test라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스다.
	4. test 메서드는 Apple을 받아 boolean을 반환하는 함수 디스크립터를 묘사한다.
	5. filter 메서드로 전달된 인수는 이와 같은 요구사항을 만족해야 한다.

---

	Tip. 함수 디스크립터
	보통 자바의 람다 표현식의 사용 용도를 두 가지로 정의하는데, 하나는 람다 함수로서의 용도 그리고
	다른 하나는 람다 표현식 문법으로 표현한 것을 의미한다고 보면 된다.
	그 중 함수 디스크럽터는 단어 그대로 함수가 어떤 입력 값을 받고 어떤 반환 값을 주는지에 대한 설명을
	람다 표현식 문법으로 표현한 것을 의미한다.

	예를 들어, () -> void 는 파라미터 리스트가 없으며, void를 반환하는 함수를 의미하는 디스크럽터이며,
	(int, int) -> double은 두 개의 int 파라미터로 받아 double형 자료를 반환하는 함수를 설명한다.
	즉, 추상 메서드가 어떤 역할을 하는지 간략하게 설명한 것이다.

---

![스크린샷 2023-04-11 오후 6 06 34](https://user-images.githubusercontent.com/96435200/231112686-36da7757-ad7e-4423-855e-0645bf5582e6.png)

``` java
	1. 람다가 사용된 컨텍스트는 무엇인가? 우선 filter의 정의를 확인하자.
	
	 public <T> List<T> filter(List<T> list, Predicate<T> p) { -- filter 메서드
		List<T> results = new ArrayList<>();
		for(T t: list) {
			if(p.test(t)) {
					result.add(t);
			}
			return results;
		} 
	 }

	2. 대상 형식은 Predicate<Apple>이다. (T는 Apple로 대치된다!)
	   
	3. Predicate<Apple> 인터페이스의 추상 메서드는 무엇인가?
	   @FunctionalInterface
	   public interface Predicate<T> { -- Predicate 인터페이스
	        boolean  test(T t);
	   }

	4. Apple을 인수로 받아 boolean을 반환하는 test 메서드다!
	5. 함수 디스크립터는 Apple -> boolean이므로 람다의 시그니처와 일치한다!
		람다도 Apple을 인수로 받아 boolean을 반환하므로 코드 형식 검사가 성공적으로 완료된다.
		
```

![스크린샷 2023-04-11 오후 6 20 18](https://user-images.githubusercontent.com/96435200/231119284-7da6ac2e-bd62-4da0-9e97-69cb669d98e8.png)

* 람다 표현식이 예외를 던질 수 있다면 추상 메서드도 같은 예외를 던질 수 있도록 throws로 선언해야 한다.


---

## 3.5.2 같은 람다, 다른 함수형 인터페이스

대상 형식이라는 특징 때문에 같은 람다 표현식이라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다.

예를 들어 이전에 살펴본 Callable과 PrivilegedAction 인터페이스는 인수를 받지 않고 제네릭 형식 T를 반환하는 함수를 정의한다.

```java
	Callable<Integer> c = () -> 42;
	PrivilegedAction<Integer> p = () -> 42;

	 @FunctionalInterface
	 public interface Callable<V> {
		V call() throws Exception; -- 인수를 받지않고 제네릭 형식을 반환한다.
	 }

	public interface PrivilegedAction<T> {
		T run(); -- 역시 인수를 받지 않고 제네릭 형식을 반환한다.
	}
  
  
  	Tip. 다이아몬드 연산자
	주어진 클래스 인스턴스 표현식을 두 개 이상의 다양한 콘텍스트에 사용할 수 있다.
	이때 인스턴스 표현식의 형식 인수는 콘텍스트에 의해 추론된다.
	List<String> listOfStrings = new ArrayList<>(); -- String 타입으로 추론된다.
	Map<String, Integer> mapOfContext = new HashMap<>(); -- String, Integer 타입으로 추론된다.
  
  
  	Tip. 특별한 void 호환 규칙
	람다의 바디에 일반 표현식이 있으면 void를 반환하는 함수 디스크립터와 호환된다.
	물론 파라미터 리스트도 호환되어야 한다.

	// Predicate 는 boolean 반환값을 갖는다.
	Predicate<String> p = s -> list.add(s);
	// Consumer는 void 반환값을 갖는다.
	Consumer<String> b = s -> list.add(s);

	public interface List<E> extends Collections<E> {
			boolean add(E e);
	}

	@FunctionalInterface
	public interface Predeicate<T> {
			boolean test(T t);
	}

	@FunctionalInterface
	public interface Consumer<T> {
			void accept(T t); -- Consumer의 콘텍스트는 return으로 void를 기대하지만 boolean을 반환
											-- 유효한 코드
	}
	
```
  
  <img width="350" alt="스크린샷 2023-04-11 오후 7 57 54" src="https://user-images.githubusercontent.com/96435200/231140469-1f376ffa-776d-40e0-b599-cbe7ed1d6605.png">
  결과 : bool = true;
  
  할당문 콘텍스트, 메서드 호출 콘텍스트(파라미터, 반환값), 형변환 콘텍스트 등으로 표현식의 형식을 추론할 수 있다.
  
  ---
  
  ## 퀴즈 3-4 형식 검사 문제. 다음 코드를 컴파일할 수 없는 이유는?
  
```java
Object o = () -> {System.out.println("Tricky example");}

지금 람다 표현식의 콘텍스트는 Object 이다.
하지만 Object 는 클래스이지 함수형 인터페이스가 아니다.
따라서 () -> void 형식의 함수 디스크립터를 갖는 Runnable로 대상 형식을 바꿔서 문제를 해결하는 방법이 있다.
또한 람다 표현식을 명시적으로 대상 형식을 제공하는 Runnable로 캐스팅해서 문제를 해결할 수도 있다.
```

<img width="345" alt="스크린샷 2023-04-11 오후 8 11 32" src="https://user-images.githubusercontent.com/96435200/231143159-8499796e-dc68-49e3-af54-c3b0065cac33.png">

결과 : Tricky example

<img width="335" alt="스크린샷 2023-04-11 오후 8 20 55" src="https://user-images.githubusercontent.com/96435200/231145919-4f86d184-546f-4f26-a806-9df5bc5635a8.png">
두번째 캐스팅 방법은 같은 함수형 디스크립터를 가진 두 함수형 인터페이스를 갖는 메소드를 오버로딩할 때 이와 같은 기법을 활용할 수 있다.<br>
즉, 어떤 메소드의 시그니처가 사용되어야 하는지를 명시적으로 구분하도록 람다를 캐스트할 수 있다.<br>

```
예를 들어 execute(() -> {}) 라는 람다 표현식이 있다면 Runnable과 Action의 함수 디스크립터가
같으므로 누구를 가리키는지가 명확하지 않다.
```

<img width="397" alt="스크린샷 2023-04-11 오후 8 45 49" src="https://user-images.githubusercontent.com/96435200/231153311-613a7b4c-9eac-4484-9666-6c96f1117695.png">

```
하지만 다음처럼 캐스트 형식을 확실히 지정해주면 누구를 호출할 것인지가 명확해진다.
```
<img width="251" alt="스크린샷 2023-04-11 오후 8 47 04" src="https://user-images.githubusercontent.com/96435200/231153535-0229791a-725c-4bc8-81e7-d590e0b0779d.png">


---

## 3.5.3 형식 추론

우리 코드를 좀 더 단순화할 수 있는 방법이 있다.<br>
자바 컴파일러는 람다 표현식이 사용된 콘텍스트(대상 형식)를 이용해서<br>
람다 표현식과 관련된 함수형 인터페이스를 추론한다.<br>

즉, 대상 형식을 이용해서 함수 디스크립터를 알 수 있으므로 컴파일러는 람다의 시그니처도 추론할 수 있다.<br>
결과적으로 컴파일러는 람다 표현식의 파라미터 형식에 접근할 수 있으므로 람다 문법에서 이를 생략 할 수 있다.<br>

<img width="466" alt="스크린샷 2023-04-11 오후 9 18 57" src="https://user-images.githubusercontent.com/96435200/231160296-10920bdf-67df-4c8d-9518-04e403a83046.png">

<img width="467" alt="스크린샷 2023-04-11 오후 9 19 10" src="https://user-images.githubusercontent.com/96435200/231160544-ac753076-52df-4f8c-befc-9393fb4538fa.png">

상황에 따라 명시적으로 형식을 포함하는 것이 좋을 때도 있고 형식을 배제하는 것이 가독성을 향상시킬 때도 있다.<br>
어떤 방법이 좋은지 정해진 규칙은 없다.<br>
개발자가 스스로 어떤 코드가 가독성을 향상 시킬수 있는지 결정해야햔다.<br>

```
Tip. 람다에 형식 추론 대상 파라미터가 하나 뿐일때는 해당 파라미터명을 감싸는 괄호도 생략할 수 있다.
```
---

## 3.5.4 지역 변수 사용
지금까지 살펴본 모든 람다 표현식은 인수를 자신의 바디 안에서만 사용했다.<br>
하지만 람다 표현식에서는 익명 함수가 하는 것처럼 **자유 변수**(파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)를 활용할 수 있다.<br>
이와 같은 동작을 **람다 캡처링**이라고 부른다.

<img width="416" alt="스크린샷 2023-04-11 오후 9 27 32" src="https://user-images.githubusercontent.com/96435200/231162152-ae4322b4-43ac-4bfd-bf7b-bf9e6c0c3fee.png">

하지만 자유 변수에도 약간의 제약이 있다.<br>
람다는 인스턴스 변수와 정적 변수를 자유롭게 캡처할 수 있지만 그러려면 지역 변수는 명시적으로 final로 선언되어 있어야 하거나<br>
실직적으로 final로 선언된 변수와 똑같이 사용되어야 한다.<br>
즉, 람다 표현식은 한 번만 할당할 수 있는 지역 변수를 캡처할 수 있다.<br>

<img width="412" alt="스크린샷 2023-04-11 오후 9 30 06" src="https://user-images.githubusercontent.com/96435200/231162702-92ba6dde-53ed-4f1f-b87a-b0774324aecf.png">

```
예를 들어 다음 예제는 portNumber에 값을 두 번 할당하므로 컴파일할 수 없는 코드다.
```
<img width="699" alt="스크린샷 2023-04-12 오전 9 14 05" src="https://user-images.githubusercontent.com/96435200/231315304-1ddd9c6d-e564-4d2f-82d7-71c25a6b0f72.png">
---

## 지역 변수의 제약

왜 지역 변수에 이런 제약이 필요할까?<br>
우선 내부적으로 인스턴스 변수와 지역 변수는 저장되는 위치가 다르다.<br>
인스턴스 변수는 힙에 저장되는 반면 지역 변수는 스택에 저장된다.<br>
람다에서 지역 변수에 바로 접근할 수 있다는 가정하에 람다가 스레드에서 실행된다면 변수를 할당ㄹ한 스레드가 사라져서 <br>
변수 할당이 해제되었는데도 람다를 실행하는 스레드에서는 해당 변수에 접근하려 할 수 있다.<br>
따라서 자바 구현에서는 원래 변수에 접근을 허용하는 것이 아니라 자유 지역 변수의 복사본을 제공한다.<br>
따라서 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 한 번만 값을 할당해야 한다는 제약이 생긴다.
  
