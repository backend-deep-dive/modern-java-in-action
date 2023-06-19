# 3.8 람다 표현식을 조합할 수 있는 유용한 메서드

간단한 여러개의 람다 표현식을 조함해서 복잡한 람다 표현식을 만들 수 있다.

자바에서는 함수끼리의 연산을 돕는 디폴트 메서드를 통해 구현한다.

>특정 상황에서 범용적으로 사용되는 디폴트 메서드들이 미리 구현되어 있는 경우가 99퍼센트이니 외우면 된다.

간단하게 자주 쓰이는

- Comparator
- Predicate
- Pipe, Compose

를 살펴본다.

### 3.8.1  Comparator 조합

### 역정렬

Comparator.comparing 에 reversed 라는 default method가 있다.

해당 메서드를 콜하면, 정렬 순서를 바꾼 함수 인터페이스의 인스턴스를 생성한다.

즉, 

``` java
return a1.getWeight().compareTo(a2.getWeight())

return a2.getWeight().compareTo(a1.getWeight())
```

위 대신 아래를 사용한 함수 인터페이스 구현체를 전달하여 역순으로 정렬한다.

### thenComparing

정렬 조건을 추가하고 싶다면 thenComparing을 통해 간단히 추가할 수 있다.

```java
inventory.sort(comparing(Apple:getWeight)
			  .reversed()
			  .thenComparing(Apple::getCountry));
```

## 3.8.2 Predicate 조합

- negate
- and
- or

3가지 메서드를 제공한다.

주의: 순서는 왼쪽에서 오른쪽으로 연결된다.

A || B && C 라면 A || (B && C) 로 해석되는 통념과 달리
a.or(b).and(c)는 (A || B) && C 로 해석된다.

### 3.8.3 Function 조합

함수형 언어에서 자주 쓰이는 andThen (pipe)와 compose가 디폴트 메서드로 제공된다.

예를 들어 가상의 함수형 언어에서 
f, g, h 함수가 있을때

1. composeExample(x) -> f . g . h x 를 실행하면 f(g(h(x))) 가 실행된다. 이것이 compose 이다.
2. PipeExample(x) -> f | g | h . x 를 하면 h(g(f(x))) 가 실행된다. 이것이 pipe이다. 

> unix sh에서 쓰이는 파이프도 이러한 함수 컴포지션의 일종이라고 보면 된다.

자바에서는

```java

Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = x -> x / 4;

Function<Integer, Integer> composeExample = f.compose(g.compose(h));
Function<Integer, Integer> pipeExample = f.andThen(g).andThen(h);
```

로 표현한다.

### 둘 다 존재하는 이유

나름의 이유가 있다.

1. 전통적으로 함수형 언어에 둘 다 존재한다.
2. 자바에서 사용하기 훨씬 편하다.

https://stackoverflow.com/questions/43849066/java-8-functions-compose-and-andthen

기존 Function A에서 새로운 Function B를 붙일 때는 A.andThen(B)가 편하고 직관적이다.

하지만 Function이 아닌 기존 자바 메서드 C 다음에 동작 Function D를 추가하고 싶은 거라면

(Function<U, V> D).andThen(C) 를 해야만 한다.

그럴 경우 C.compose(D)가 훨씬 편하다.
