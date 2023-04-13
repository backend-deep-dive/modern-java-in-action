# 3.2 어디에, 어떻게 람다를 사용할까?

함수형 인터페이스 문맥에서 사용할 수 있다.

Functional Interface

## 3.2.1 함수형 인터페이스

정확히 하나의 추상 메서드를 지정하는 인터페이스

(디폴트 메서드들은 많아도 상관 없음.)

``` java
public interface Comparator<T> {
	int compare(T o1, T o2);
}
```

### 퀴즈 3-2

``` java
public interface Adder {
	int add(int a, double b);
}

public interface SmartAdder extends Addr {
	int add(double a, double b);
}

public interface Nothing {

}
```

Adder만이 함수형 인터페이스이다. SmartAdder 같은 경우는 오버로딩된 add 함수 2개가 존재하기 때문에 함수형 인터페이스가 아니다.

함수형 인터페이스에 람다나 익명클래스를 전달하면 컴파일러가 자동으로 해당 함수형 인터페이스의 인스턴스로 변환해준다.

```java
Runnable r1 = () -> System.out.println("Hello world 1");

Runnable r2 = new Runnable() {
	public void run(){
		System.out.println("Hello world 2");
	}
};

public static void process(Runnable r){
	r.run();
}

process(r1);
process(r2);
process(() -> System.out.println("Hello World 3"));

```


## 3.3.2 함수 디스크립터

함수 디스크립터: 무엇을 설명하고자 하는지 잘 모르겠다;;

함수 시그니처: 함수의 인풋과 아웃풋의 타입을 설명해 놓은 표현식.

함수형 인터페이스의 시그니처와 람다식의 시그니처가 일치해야 한다는 것을 강조한다.

### 람다와 메소드 호출

람다식에서 한개의 void를 리턴하는 메서드 호출은 중괄호를 생략할 수 있다.

### 20장, 21장

람다를 함수형 인터페이스로 받지 말고, 람다 고유의 타입을 추가하는 것은 어떨까?

언어 설계 상 어렵고 복잡해지기 때문에 개발자들이 그러지 않기로 결정함.

### 퀴즈 3-3
``` java
Predicate<Apple> p = (Apple p) ->a.getWeight();
```

위 코드는 어디가 잘못되었을까?

뒤에 있는 람다식은 Apple을 인자로 받고 Int를 리턴하는 함수 시그니처를 가지고 있다. 

반면 Predicate p는 true / false 를 리턴하는 Apple -> boolean 시그니처를 가지고 있어 서로의 시그니처가 불일치한다.

### @FunctionalInterface

어떠한 인터페이스에 해당 어노테이션을 선언했는데 실제 구현은 함수형 인터페이스가 아닐 경우 컴파일 에러를 발생시켜 개발자의 실수를 막는다. 또한 코드를 읽는 이에게 하여금 해당 인터페이스의 사용 방법을 직관적으로 알려준다. 
