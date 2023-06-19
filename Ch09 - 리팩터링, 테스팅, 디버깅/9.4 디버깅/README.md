# 9.4 디버깅

람다 표현식과 스트림을 사용하면 기존의 디버깅 기법이 잘 먹혀들지 않는다는 단점이 있다...

- 스택 트레이스
- 로깅

## 9.4.1 스택 트레이스 확인

### 책 예시 (자바 10 기준)

![](Pasted%20image%2020230615141151.png)

책 출판 시간대 기준 과거에는 람다를 디버깅하기 어려웠다.

#### 자바 11

![](Pasted%20image%2020230615141249.png)

자바 11에서도 해결되지 않았다.

#### 자바 17

![](Pasted%20image%2020230615141508.png)

이제는 해결된 듯 하다!

#### 자바 20

![](Pasted%20image%2020230615141426.png)

자바 17과 20의 차이는 딱히 뚜렷하게 보이지 않는다.

---

## 9.4.2 정보 로깅

스트림의 파이프라인 연산 디버깅할 때는 forEach보다는 **peek**를 쓰는것이 좋다.

### forEach 단점

forEach는 terminal operation이기 때문에 스트림을 소비하고 끝이 난다. 여기서 더 연결할 수가 없다.

```java

numbers.stream()
...
.forEach(System.out::println);

// 끝. 연계 불가능

```

### peek

forEach와 똑같으면서도 스트림을 연결할 수 있음. 자신이 활용한 스트림 요소를 파이프라인의 다음 연산으로 그대로 전달한다.

```java

List<Integer> result = Stream.of(2, 3, 4, 5)
    .peek(x -> System.out.println("taking from stream: " + x))
    .map(x -> x + 17)
    .peek(x -> System.out.println("after map: " + x))
    .filter(x -> x % 2 == 0)
    .peek(x -> System.out.println("after filter: " + x))
    .limit(3)
    .peek(x -> System.out.println("after limit: " + x))
    .collect(toList());

```

![](Pasted%20image%2020230615142133.png)

---

## (책 x) 디버거 브레이크 포인트

![](Pasted%20image%2020230615140703.png)

IntelliJ에서 람다에 BreakPoint를 걸어준다!
