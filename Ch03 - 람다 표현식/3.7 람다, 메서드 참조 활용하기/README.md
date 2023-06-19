
## 3.7.1 1단계 : 코드전달

처음에 다룬 사과 리스트를 다양한 정렬 기법으로 정렬하는 문제로 돌아가보자

List API에서 sort 메서드를 제공하므로 직접 정렬 메서드를 구현할 필요는 없다.
sort 메서드는 다음과 같은 시그니처를 갖는다.

```java
void sort(Comparator<? super E> c)
```

sort 메서드는 Comparator 객체를 인수로 받아 두 사과를 비교한다.

```java
@FunctionalInterface
public interface Comparator<T> {
	int compare(T o1, o2);
}
```

객체 안에 동작을 포함시키는 방식으로 다양한 전략을 전달할 수 있다.
즉, 이제 'sort의 동작은 파라미터화 되었다'라고 말할 수 있다.

<img width="476" alt="스크린샷 2023-04-12 오후 5 34 14" src="https://user-images.githubusercontent.com/96435200/231401000-1ae9562f-cab0-4cc8-8647-c1f1d38badf0.png">


즉, sort에 전달된 compare의 정렬 전략에 따라 sort의 동작이 달라질 것이다.

```java
inventory.sort(new AppleComparator());
```

---

## 3.7.2 2단계 : 익명 클래스 사용

한 번만 사용할 Comparator를 위 코드처럼 구현하는 것보다는 익명 클래스를 이용하는 것이 좋다.

<img width="348" alt="스크린샷 2023-04-12 오후 5 40 10" src="https://user-images.githubusercontent.com/96435200/231402352-9a034aa9-6cf3-47fe-aa8d-08383cbd0ca1.png">


---

### 3.7.3 3단계 : 람다 표현식 사용

한 번만 사용할 익명 클래스를 만든 건 좋았으나 코드가 장황하다.
함수형 인터페이스를 기대하는 곳 어디에서나 람다 표현식을 사용할 수 있으니 사용해보자.

<img width="322" alt="스크린샷 2023-04-12 오후 5 49 37" src="https://user-images.githubusercontent.com/96435200/231404807-652ea1eb-6e4d-4a89-a15c-63b40195ce4a.png">


자바 컴파일러는 람다 표현식이 사용된 콘텍스트를 활용해서 람다의 파라미터 형식을 추론한다.
사진의 두 번째 코드처럼 더 줄일 수 있다.

이 코드의 가독성을 더 향상시킬 수 없을까?
Comparator는 Comparable 키를 추출해서 Comparator 객체로 만드는 Function 함수를 인수로 받는
정적 메서드 comparing을 포함한다.

<img width="633" alt="스크린샷 2023-04-12 오후 6 01 50" src="https://user-images.githubusercontent.com/96435200/231408415-30a809c4-976b-414d-a52c-d63d87a87d21.png">

```java
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
```

이제 코드를 다음처럼 간소화할 수 있다.
```java
Comparator<Apple> c = Comparator.comparing(apple -> apple.getWeight());
```


---


## 3.7.4 4단계 : 메서드 참조 사용

메서드 참조를 이용하면 람다 표현식의 인수를 더 깔끔하게 전달할 수 있으니
메서드 참조를 이용해서 코드를 조금 더 간소해보자.

```java
(java.util.Comparator.comparaing은 정적으로 임포트했다고 가정)
inventory.sort(comparing(Apple::getWeight));
```

단지 코드만 짧아진 것이 아니라 코드의 의미도 명확해졌다.
즉, 코드 자체로 'Apple을 weight별로 비교해서 inventory를 sort하라'는 의미를 전달할 수 있다.

