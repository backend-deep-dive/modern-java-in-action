# 1.3 자바 함수

프로그래밍 언어에서 `함수`라는 용어는 메서드 특히 `정적 메서드`와 같은 의미로 사용된다. 하지만 자바의 함수는 이에 더해 `수학적인 함수`처럼 사용되며 부작용을 일으키지 않는 함수를 의미한다.<br>
자바 8에서는 멀티코어에서 병렬 프로그래밍을 활용할 수 있는 스트림과 연계될 수 있도록 함수를 새로운 값의 형식으로 추가했다.<br>
<br>
`'함수를 값처럼 취급한다'`라는 개념을 알기 전 우리는 `일급 시민`(일급 객체)에 관하여 불리는 용어의 의미를 알아야한다.<br>
> **일급 시민**이란
> - 미국 시민 권리에서 유래되어 **바꿀 수 있는 값**을 말한다
> - 예를들어 int, double 형식의 기본값 및 String 형식의 객체
> - 하지만 구조체(클래스나 메서드)와 같은 경우 값의 구조를 표현하는데 도움이 될 수 있지만, 그 자체로 값이 될수는 없다.
>   - 이것은 `이급 시민`에 해당한다.<br>
>   
> 이렇게 이급 시민의 메서드를 런타임에 전달 할 수 있다면, 즉 메서드를 일급 시민으로 만들면 프로그래밍에 유용하게 사용할 수 있을 것이다.

## 메서드와 람다를 일급 시민으로

### 메서드 참조

자바 8이전에는 메서드가 값이 될 수 없었기 때문에 메서드를 객체로 `인스턴스화`하여 전달해야 했다.<br>
하지만 자바 8의 메서드 참조(::)를 이용하면 메서드 자체가 함수의 파라미터로 전달될 수 있다.<br>
아래는 같은 디렉토리의 숨겨진 파일을 찾는 예시 코드이다.

```java
//자바 8 이전
File[]hiddenFiles=new File(".").listFiles(new FileFilter(){
    public boolean accept(File file){
        return file.isHidden(); // 숨겨진 파일 필터링
    }
})
```

```java
//자바 8이후 메서드 참조 적용
File[]hiddenFiles=new File(".").listFiles(File::isHidden);
```

- `isHidden`이라는 함수는 준비되어 있으므로 자바 8의 메서드 참조::('이 메서드를 값으로 사용하라'는 의미)를 이용해 값을 직접 전달할 수 있게 되었다.

### 람다 : 익명 함수

자바 8에서는 메서드를 일급값으로 취급할 뿐 아니라 `람다`를 포함하여 함수도 값으로 취급할 수 있다.<br>
직접 메서드를 정의할 수도 있지만, 이용할 수 있는 편리한 클래스나 메서드가 없을 때 새로운 람다 문법을 이용하면 간결하게 코드를 구현할 수 있다.<br>
람다 문법 형식으로 구현된 프로그램을 함수형 프로그래밍, 즉 '함수를 일급값으로 넘겨주는 프로그램을 구현한다'라고 한다.

## 코드 넘겨주기 : 예제

Apple 클래스와 `getColor` 메서드가 있고, Apples 리스트를 포함하는 변수 inventory가 있다고 가정하고, 이때 원하는 조건에 맞는 리스트를 반환하는 프로그램을 구현해 보자.<br>
이때 사과의 무게가 150이상인 사과만 필터링 하고 싶으면 아래 처럼 코드를 작성할 수 있을 것이다.

```java
public static List<Apple> filterHeavyApples(List<Apple> inventory){
    List<Apple> result = new ArrayList<>();
        for(Apple apple:inventory){
            if(apple.getWeight() > 150){
                result.add(apple);
            }
        }
        return result;
}
```

여기서 필터링 하는 기준이 바뀐다면 (무게 -> 컬러등) if문 안의 코드만 바뀌고 완전히 같은 구조의 코드가 된다.<br>
이는 복사 & 붙여넣기식의 좋지 않은 코드 작성법이다. 가장 큰 이유는 해당 코드에서 버그가 발생할 경우 모든 코드를 고쳐야 한다.
<br>

하지만 자바 8에서는 코드를 인수로 넘겨줄 수 있으므로 filter메서드를 중복으로 구현할 필요가 없다.

```java
public static boolean isHeavyApple(Apple apple){
        return apple.getWeight() > 150;
}

public interface Predicate<T> {
    boolean test(T t);
}

static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) { //조건 판별 함수가 Predicate 파라미터로 전달
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if (p.test(apple)) { //조건이 맞는지 확인
            result.add(apple);
        }
    }
    return result;
}
```
>`Predicate`란
> - 인수로 값을 받아 true 또는 false를 반환하는 함수를 의미하는 용어
> - 여러 조건을 검증하기 위해 코드를 복사 & 붙여넣기 하는 방식이 아닌 검증 메서드만 추가로 작성하면 된다.

`filterApples()` 메서드는 `filterApples(inventory, Apple::isHeavyApple)`과 같이 호출하여 조건을 판별할 수 있다.

## 메서드 전달에서 람다로
위의 코드 예시에서 `isHeavyApple()`, `isGreenApple()`처럼 한두 번만 사용할 메서드를 매번 정의하는 것은 귀찮은 일이다.<br>
자바 8에서는 이런 문제도 간단하게 람다(익명함수)로 해결할 수 있다.

`filterApples(inventory, (Apple a) -> a.getWeight() > 150);`<br>
`filterApples(inventory, (Apple a) -> GREEN.equals(a.getColor);`<br>
위 두 가지를 합쳐서 아래처럼 코드를 구현할 수도 있다.<br>
`filterApples(inventory, (Apple a) -> a.getWeight() > 150 || GREEN.equals(a.getColor()));`<br>

하지만 람다가 복잡한 복잡한 동작을 수행하는 상황이라면, 익명 람다보다는 코드가 수행하는 일을 잘 설명하는 이름을 가진 메서드를 정의하고 메서드 참조를 활용하는 것이 바람직하다.<br>
**코드의 명확성이 우선시 되어야 하기 때문이다.**