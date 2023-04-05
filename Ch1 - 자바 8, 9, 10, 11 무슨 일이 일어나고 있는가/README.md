## 담당자

- 1.1 역사의 흐름은 무엇인가? : 어정윤
- 1.2 왜 아직도 자바는 변화 하는가? : 송민진
- 1.3 자바 함수 : 이홍섭
- 1.4 스트림 : 박상욱
- 1.5 디폴트 메서드와 자바 모듈 : 홍승아
- 1.6 함수형 프로그래밍에서 가져온 다른 유용한 아이디어 : 김대현 

## 진행 날짜
- 2023년 3월 30일 (목요일)
---
# 1.1. 역사의 흐름은 무엇인가?
자바 역사를 통틀어 가장 큰 변화가 자바 8에서 일어났다.

자바 버전이 올라감에 따라 생긴 크고 작은 변화 덕분에 프로그램을 더 쉽게 구현할 수 있게 되었다.

ex) 사과 목록 무게순 정렬

| 고전적 코드

```java
Collections.sort(inventory, new Comparator<Apple>() {
    public int compare(Apple apple1, Apple apple2){
        return apple1.getWeight().compareTo(apple2.getWeight());
    }
});
```

| 자바 8 이용

```java
inventory.sort(Comparator.comparing(Apple::getWeight));
```

멀티코어 CPU 대중화와 같은 하드웨어적 변화도 자바 8에 영향을 미쳤다.

지금까지 대부분의 자바 프로그램은 코어 중 하나만을 사용했다. 자바 8이 등장하기 이전에는 나머지 코어를 활용하려면 스레드를 사용하는 게 좋았을 지도 모른다. 하지만 스레드를 사용하면 관리하기 어렵고 많은 문제가 발생할 수 있다.

자바는 이러한 병렬 실행 환경을 쉽게 관리하고 에러가 덜 발생하는 방향으로 진화하려 노력했다.

- 자바 1.0: 스레드(thread), 락(lock), 메모리 모델(memory model) 지원

  └ 특별 전문가로 구성된 프로젝트 팀이 아닌 한 이와 같은 저수준 기능을 온전히 활용하기 어렵다.

- 자바 5: 스레드 풀(thread pool), 병렬 실행 컬렉션(concurrent collection) 등 아주 강력한 도구 도입

- 자바 7: 병렬 실행에 도움을 줄 수 있는 포크/조인 프레임워크 제공
  
  └ 여전히 개발자가 활용하기 쉽지 않다.
  
- 자바 8: 병렬 실행을 새롭고 단순한 방식으로 접근할 수 있는 방법 제공

  └ **'간결한 코드'**, **'멀티코어 프로세서의 쉬운 활용'** 기반

    - 자바 8에서 제공하는 새로운 기술
        - 스트림 API: 병렬 연산 지원
          
          (스트림을 이용하면 `synchronized` 키워드(에러를 자주 일으키고 멀티코어 CPU보다 비용이 훨씬 비쌈)를 사용하지 않아도 된다.)
          
        - 메소드에 코드를 전달하는 기법: 새롭고 간결한 방식으로 동작 파라미터화(behavior parameterization) 구현 가능
          
        - 인터페이스의 디폴트 메소드

- 자바 9: 리액티브 프로그래밍이라는 병렬 실행 기법 지원
  
  └ RxJava( ≒ 리액티브 스트림 툴킷)를 표준적인 방식으로 지원

자바 8 기법은 **함수형 프로그밍(functional-style programming)** 에서 위력을 발휘한다.

코드를 전달하거나 조합해서 자바의 강력한 프로그래밍 도구로 활용할 수 있다.
# 1.2. 왜 아직도 자바는 변화하는가?

- 우리는 시공을 초월하는 완벽한 언어를 원하지만, 현실적으로 그런 언어는 존재하지 않는다.
- 모든 언어는 장단점을 가지고 있다.
  > 📌 **예시**
  > - C, C++
  > 	- 장점 : 작은 런타임 풋프린트 - 운영체제와 다양한 임베디드 시스템에서 인기를 끌고 있음
  > 	- 단점 : 낮은 프로그래밍 안전성 - 프로그램이 예기치 않게 종료되거나 바이러스 등이 침투할 수 있는 보안 구멍이 있을 수 있음
  > - Java, C#
  > 	- 안전성 ↑ - 런타임 풋프린트에 여유가 있는 어플리케이션에서는 C, C++을 압도함

- 새로운 언어가 등장하면 진화하지 않은 기존 언어는 사장된다. 
- 단, Java는 1995년 첫 베타 버전이 공개된 이후로 경쟁 언어를 대신하며 커다란 생태계를 성공적으로 구축했다.
- Java 8의 3가지 프로그래밍 개념을 조금 더 자세히 알아보자.
  1) Stream
  2) Behavior Parameterization
  3) Shared Mutable Data & Parallelism

<br>

## 1.2.1 프로그래밍 언어 생태계와 Java

### Java의 출발
- 처음부터 많은 유용한 라이브러리를 포함하는 잘 설계된 객체지향 언어로 시작
- 처음부터 스레드, 락을 이용한 동시성도 소소하게 지원했음
- 코드를 JVM 바이트 코드로 컴파일함 → Java가 인터넷 애플릿 프로그램의 주요 언어가 됨
- 다양한 임베디드 컴퓨팅 분야(스마트카드, 토스터, 셋톱박스, 자동차 브레이크 시스템 등)를 성공적으로 장악 중
  > **📌 참고**<br>
  > Scala, Groovy와 같은 경우, 중요도가 `JVM(자바 가상 머신)`, `바이트코드` > `Java 언어`일 경우 Java를 대체하기도 했음!
  > 특히, JVM의 최신 업데이트 덕분에 경쟁 언어는 JVM에서 더 부드러운 실행이 가능해짐 + 자바와 상호동작도 가능해짐

### Java와 객체지향
- 객체지향은 1990년대, 두 가지 이유로 각광 받음
  1. **캡슐화** - C에 비해 소프트웨어 엔지니어링적인 문제 ↓
  2. 객체 지향의 **정신적 모델** - 윈도우 95 ~ 의 WIMP 프로그래밍 모델에 쉽게 대응할 수 있었음
- 초기 브라우저들에서 Java 모델과 Java 코드 애플릿을 안전하게 실행할 수 있었음
   → 자바가 대학으로 깊숙히 자리 잡음
   → 졸업생들이 업계에서 활용하기 시작
- 하드웨어가 발전함에 따라 `프로그래머의 시간`이 `애플리케이션 실행 시간`보다 더 중요한 요소로 부각되었고, Java가 더욱 힘을 얻음

### Java도 마주한 새로운 바람
#### 1) 병렬 프로세싱
- 프로그래머들이 빅데이터(TB 이상의 DataSet)를 직면하며, **멀티코어 컴퓨터**와 **컴퓨팅 클러스터**의 필요성이 커짐
- Java 8 - 더욱 다양한 프로그래밍 도구 제공
  - 자바에 없던 완전히 새로운 개념들을 추가 → 현재 시장에서 요구하는 기능을 효과적으로 제공
  - 멀티코어 병렬성이 강화됨

#### 2) 큰 시스템 설계
- 외부에서 큰 하위시스템 컴포넌트를 추가하고, 다른 벤더가 만든 컴포넌트를 이용해 개발하는 사례 ↑
- Java 8 - default 메소드 제공
- Java 9 - module 제공

<br>

## 1.2.2 Stream Processing (스트림 처리)

#### **Stream**
: 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임
- 프로그램 : 입력 스트림에서 데이터를 한 개씩 읽어 들이고, 출력 스트림으로 데이터를 한 개씩 기록함
  ⇒ 어떤 프로그램의 출력 스트림은 다른 프로그램의 입력 스트림이 될 수 있음
  > - 입력 스트림 : `System.in`
  > - 출력 스트림 : `System.out`

#### Unix의 병렬 실행
```unix
// 파일의 단어를 소문자로 바꾼 후, 사전 순으로 단어를 정렬했을 때 마지막 3개 단어 출력
cat file1 file2 | tr "[A-Z]" "[a-z]" | sort | tail -3
```
- `sort` : 여러 행의 스트림을 입력으로 받아, 여러 행의 스트림을 출력으로 만들어냄
  - 따라서, `cat`, `tr`이 완료되지 않은 시점에서 sort가 행을 처리하기 시작할 수 있음

#### Java의 병렬 처리
- Java 8 - java.util.stream 패키지에 스트림 API가 추가됨
  - `Stream<T>` : T 형식으로 구성된 일련의 항목을 의미함

- **복잡한 파이프라인 처리**
  - 위의 Unix 명령어가 복잡한 파이프라인을 구성했던 것처럼, 스트림은 파이프라인을 만드는 데에 필요한 많은 메서드를 제공함
  - 기존에는 한 번에 한 항목을 처리했던 반면, Java 8 이후에는 (마치 DB 쿼리처럼) 작업을 고수준으로 추상화해서 일련의 스트림으로 처리 가능해짐

- **여러 CPU 코어에 할당**
  - 스트림 파이프라인을 이용해서, 입력 부분을 여러 CPU 코어에 쉽게 할당 가능
  - 스레드를 사용하지 않고도 공짜로 병렬성을 얻을 수 있음

<br>

## 1.2.3. Behavior Parameterization (동작 파라미터화)

#### Behavior Parameterization
- 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블럭 (코드 블럭의 실행은 나중으로 미뤄짐)
  ⇒ 코드 블럭에 따라 메서드의 동작이 파라미터화됨
- 즉, 메서드를 다른 메서드의 인수로 넘겨주는 기능

#### Java 8
- Java 8 이전에는 메서드를 다른 메서드로 전달할 방법이 없었음
- Java 8 이후 동작 파라미터화로 인해, 코드 일부를 API로 전달하는 기능이 가능해짐
  즉, 메서드(코드)를 다른 메서드의 인수로 넘겨주는 것이 가능
- 함수형 프로그래밍 분야의 기술을 응용해서 동적 파라미터를 활용하는 방법도 있음

> 💡 **동작 파라미터화**가 중요한 이유!<br>
> Stream API는 연산의 동작을 파라미터화할 수 있는 코드를 전달한다는 사상에 기초하기 때문

<br>

## 1.2.4. Shared Mutable Data & Parallelism (공유 가변 데이터와 병렬성)

#### 공짜 병렬성?
- 스트림 메서드로 전달하는 코드의 동작 방식을 조금 바꾸면 병렬성을 공짜로 얻을 수 있음

#### 스트림과 병렬성
- 스트림 메서드로 전달하는 코드는 다른 코드와 동시에 실행되더라도 안전하게 실행될 수 있어야 함
- 공유된 변수나 객체가 있으면 병렬성에 문제 발생! (ex: 두 개의 프로세스가 공유된 변수를 동시에 바꿀 때?)

#### Synchronized vs Stream
- **`synchronized`** (~ Java 7)
  - 공유된 가변 데이터 보호 규칙 생성 가능
  - 단, 시스템 성능에 악영향 有 - 다중 프로세싱 코어에서 사용시 아주 비효율적일 수 있음
- **`stream`** : 기존의 스레드 API보다 쉽게 병렬성 활용 가능

#### 병렬성 있는 코드
- 다른 코드와 동시에 실행하더라도 안전하게 실행할 수 있는 코드를 만들려면 **공유된 가변 데이터(shared mutable data)** 에 접근하지 않아야 함
- 이러한 코드들을 순수 함수(pure function), 부작용 없는 함수(side-effect-free function), 상태 없는 함수(stateless function)라고 부름
- 함수형 프로그래밍 패러다임의 핵심적인 사항 ( ↔ 명령형 프로그래밍 : 일련의 가변 상태로 프로그래밍을 정의함)
  > 즉, 함수가 (수학적인 함수처럼) 정해진 기능만 수행하며, 다른 부작용은 일으키지 않는다는 개념과 관련이 있음!

<br>

## 1.2.5. 자바가 진화해야 하는 이유

#### Java의 진화
1. (Java 5) `Generic`의 출현
   - 컴파일 시 더 많은 에러를 검출할 수 있음
   - `List` → `List<String>` - 리스트의 유형을 알 수 있어 가독성이 좋아짐

2. (Java 8) 고전적인 객체 지향에서 벗어나, 함수형 프로그래밍으로 다가섬
   - 함수형 프로그래밍 - 우리가 하려는 작업이 최우선시되며, 그 수행 방법은 별개로 취급함
   - `Iterator` → `for-each`
   <br>
   
    > 극단적으로 생각하면 전통적인 객체지향 프로그래밍과 함수형 프로그래 밍은 완전 상극이다. 자바 8에서 함수형 프로그래밍을 도입함으로써 두 가지 프로그래밍 패러다임의 장점을 모두 활용할 수 있게 되었다.
    > ⇒ 즉, 어떤 문제를 더 효율적으로 해결할 수 있는 다양한 도구를 얻게 된 것!
  
#### 결론
언어는 하드웨어나 프로그래머 기대의 변화에 부응하는 방향으로 변화해야 한다!
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
# 1.4 스트림

거의 모든 자바 애플리케이션은 컬렉션을 **만들고 활용한다**.
하지만 컬렉션으로 모든 문제가 해결되는 것은 아니다.
예를 들어 고가의 트랜잭션만 필터링한 다음 통화로 결과를 그룹화 해보자.

```java
private static void Transaction() {
        // todo :: 그룹화된 트랜잭션을 더할 Map 생성
        Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();

        // todo :: 트랜잭션 리스를 반복
        for (Transaction transaction : transactions) {
            if (transaction.getPrice() > 100) {
                // todo :: 트랜잭션의 통화 추출
                Currency currency = transaction.getCurrency();
                List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);

                // todo :: 현재 통화의 그룹화된 맵에 항목이 없으면 새로 만든다.
                if (transactionsForCurrency == null) {
                    transactionsForCurrency = new ArrayList<>();
                    transactionsByCurrencies.put(currency, transactionsForCurrency);
                }

                // todo :: 현재 탐색된 트랜잭션을 같은 통화의 트랜잭션 리스트에 추가한다.
                transactionsForCurrency.add(transaction);

            }
        }
    }
```

다음처럼 많은 코드를 구현해야하고, 가독성이 떨어져 알아보기도 이해하기도 힘들다.
위의 코드를 보면 같은 항목이 중첩된 제어 흐름 문장이 많아 더욱 더 한번에 이해하기 난해하다.

그럼 **스트림 API**를 이용하여 다음 문장을 해결해보자.

```java
private static void Transcation() {
        Map<Currency, List<Transaction>> transactionsByCurrencies =
                transactions.stream()
                        .filter((Transaction t -> t.getPrice() > 1000) // 고가의 트랜잭션 필터링
                        .collect(groupingBy(Transaction::getCurrency)); // 통화로 그룹화함
    }
```

지식이 없다면 위 코드를 이해하기 어렵다.
이후에 더 자세히 스트림에 대해 설명할테니 지금은 스트림 API를 활용하면 컬렉션 API와는 상당히 다른 방식으로 데이터를
처리 할 수 있다는 사실만 기억하자.

컬렉션에서는 반복 과정을 직접처리해야 했다.

즉, for-each 문을 사용해 각 요소를 반복하면서 작업을 수행했다.
즉 컬렉션의 요소를 반복문을 통해 직접 탐색하는 것이다.
이런 방식의 반복을 **외부 반복**이라고 한다.

반면 스트림 API를 이용하면 이러한 루프를 신경 쓸 필요가 없다.
스트림 API에서는 라이브러리 내부에서 모든 데이터가 처리된다.
이와 같은 반복을 **내부 반복**이라고 한다.

나중에 스트림에 대해 깊게 배우겠지만 먼저 간단히 소개하자면
스트림이 알아서 반복을 처리하고 그 결과를 어딘가에 따로 저장한다.
스트림의 내부 반복은 람다 표현식을 인수로 받기 때문에, 어떤 작업을 수행할지만 지정하면 모든 것이 알아서 처리된다.

조금 이해하기 쉽게 설정하자면 컬렉션의 **내부 반복**은 어떤 상자에 아이템을 담아야 할때, 아이템을 하나씩 담아야 한다면
스트림의 **외부 반복**은 어떤 상자에 아이템을 담아야 할때, 병렬적으로 동시에 담을 수 있다.

이러한 차이점은 멀티코어 컴퓨터에서 더욱 두드러진다.

컬렉션으로 방대한 요소를 가진 목록을 반복한다면 단일 CPU로 처리되어 시간이 오래 걸릴 수 있다.
하지만 스트림은 어떤가 코어가 8개인 CPU라면 8개의 코어를 전부 사용해 병렬적으로 작업을 수행하기에 컬렉션보다 8배 빠르게 작업할 수 있다!


## 1.4.1 멀티스레딩은 어렵다.

이전 자바 버전에서 제공하는 스레드 API로 멀티스레딩 코드를 구현해서 병렬성을 이용하는 것은 쉽지않다.
멀티스레드 환경에서는 동시에 공유된 자원에 접근하고, 데이터를 갱신할 수 있다.

결과적으로 스레드를 잘 제어하지 못하면 원치않은 방식으로 데이터가 바뀔 수 있다.

말로 한번 풀어보자.

우리는 2가지의 스레드를 예시로 사용할 것이다.

스레드1, 스레드2 그리고 
이 두 개의 스레드가 공유할 자원인 int = 100 이 있다.

위에서 잠깐 설명했듯이 멀티스레드 환경에서는 공유된 자원에 동시에 접근한다.

```멀티스레드
1. 스레드1이 공유된 자원 100에 접근했다.
2. 스레드2도 공유된 자원 100에 접근했다.
3. 스레드1이 공유된 자원 100을 103으로 수정했다.
4. 스레드1이 변경한 공유 자원 103을 저장했다. (+3)
5. 그럼 스레드2가 공유 자원이 103으로 변경된 것을 알 수 있을까? 이미 변경전에 공유 자원 100을 가지고 오는 처리를 했는데???
6. 스레드2가 이미 가져온 공유된 자원 100을 105로 수정했다. (+5)
7. 스레드2가 변경한 공유 자원 105을 저장했다.
8. 스레드1이 (+3)을 했고, 스레드2가 (+5)를 공유된 자원에 각각 했으니 결과값은 108인가?
9. 아니다 각자 처음에 공유된 자원을 가지고 가장 마지막에 처음 받은 공유 자원 100을 수정한 결과값인 105로 수정된다.
10. 이건 우리가 원하는 결과가 아니다.
```

자바8의 스트림 API는 컬렉션으로 자료를 처리할 때 생기는 모호함과 반복적인 코드 문제
그리고 멀티코어 활용의 어려움 이라는 문제는 모두 해결했다.

처음 컬렉션으로 작성된 코드를 보면 무언가 비슷한 코드가 반복되는 것을 확인할 수 있다.
이처럼 스트림은 자주 반복되는 패턴으로 주어진 조건에 따라 **필터링**을 하거나, 데이터를 **추출**하거나, 데이터를 **그룹화**하는 기능이 있다.

또 하나 예시를 들어보자.

```
두 개의 멀티코어를 가진 컴퓨터(2개의 CPU를 가진 컴퓨터)에서 리스트를 필터링 할때, 
하나의 CPU는 필터링 해야할 리스트의 앞부분을, 또 하나의 CPU는 필터링 해야할 리스트의 뒷부분을 처리하도록 요청할 수 있다.
이것을 **포킹 단계**라고 한다. 병렬처리!!

그리고 각각의 CPU는 맡은 부분을 처리한다.

각각의 CPU가 작업을 처리한 후 하나의 CPU가 두 결과를 정리힌다.
```

지금은 스트림 API나 컬렉션 API나 비슷한 동작 방식을 가지고 있다고 생각할 수도 있다.
중요한건 **컬렉션은 어떻게 데이터에 접근하고 데이터를 저장하는데 초점이 맞춰져 있는 반면**,
**스트림은 데이터에 어떤 계산을 할 것인지 묘사하는 것에 중점을 둔다는 것을 명심하자**

또한 스트림은 스트림 내의 요소를 쉽게 병렬처리하게 환경을 제공한다는 것이 핵심이다.

다시 한번 언급하지만 스트림과 람다 표현식을 이용하면 '병렬성을 공짜로' 얻을 수 있다.
하지만 데이터가 적은 부분에서는 스트림으로 병렬 처리하는 것이 성능이 더 나쁠 수 있다.

```java
// 스트림으로 구현한 단순 순차 처리 (병렬 처리 아니에요)
List<Apple> heavyApples = 
            inventory.stream().filter((Apple a) -> a.getWeight() > 150)
                    .collect(toList());
                    
// 스트림으로 구현한 병럴 처리
List<Apple> heavyApples =
            inventory.parallelstream().filter((Apple a) -> a.getWeight() > 150)
                    .collect(toList());
```
# 1.5. 디폴트 메서드와 자바 모듈

### 자바 8 이전 문제점

요즘은 외부에서 만들어진 컴포넌트를 이용해 시스템을 구축하는 경향이 있다. 이와 관련해 지금까지 자바에서는 특별한 구조가 아닌 평범한 자바 패키지 집합을 포함하는 JAR 파일을 제공하는 것이 전부였다. 
이러한 패키지의 인터페이스를 바꿔야 하는 상황에서는 인터페이스를 구현하는 모든 클래스의 구현을 바꿔야 하는 문제점이 있었다.

자바 8, 9에서는 이러한 문제점에 대해 아래와 같이 해결할 수 있다.
- 자바 9

모듈 시스템은 모듈을 정의하는 문법을 제공한다. 이를 이용해 패키지 모음을 포함하는 모듈을 정의할 수 있다. 모듈 덕분에 JAR 같은 컴포넌트에 구조를 적용할 수 있으며 문서화와 모듈 확인 작업이 용이해졌다. (14장)

- 자바 8

인터페이스를 쉽게 바꿀 수 있도록 `디폴드 메서드`를 지원한다. (13장)

디폴트 메서드는 특정 프로그램을 구현하는 데 도움을 주는 기능이 아니라 미래에 프로그램이 쉽게 변화할 수 있는 환경을 제공하는 기능이다.

<br>

### 어떻게 기존의 구현을 고치지 않고도 이미 공개된 인터페이스를 변경할 수 있을까?

자바 8은 구현 클래스에서 구현하지 않아도 되는 메서드를 인터페이스에 추가할 수 있는 기능을 제공하며, 메서드 본문은 클래스 구현이 아니라 인터페이스 일부로 포함된다.

디폴트 메서드를 이용하면 기존의 코드를 건드리지 않고도 원래의 인터페이스 설계를 자유롭게 확장할 수 있다. 자바 8에서는 인터페이스 규격명세에서 `default`라는 새로운 키워드를 지원한다.

예를 들어 자바 8에서는 List에 직접 sort 메서드를 호출할 수 있다. 이는 자바 8의 List 인터페이스에 다음과 같은 디폴트 메서드 정의가 추가되었기 때문이다.
```java
//자바 8이후 메서드 참조 적용
default void sort(Comparator<? super E> C) {
  Collections.sort(this, c);
}
```

<br>

전반적으로 디폴트 메서드와 자바 모듈은 보다 유연하고 유지 관리 가능한 코드를 작성하는 데 도움이 되는 동시에 애플리케이션에 더 나은 보안 및 구성을 제공해주는 강력한 기능이다.
# 1.6 함수형 프로그래밍에서 가져온 다른 유용한 아이디어

## Optional

```java
if (obj != null && obj ...){
    ...
}
```
사용을 자제하자.

토니 호아레(quick sort의 발명자)는 2009년 QCon London에서 다음과 같은 말을 했다.

"널을 만든 건 정말 뼈아픈 실수였다."

### Null References - The Billion Dollar Mistake

<https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare/>

프로그래밍 언어에 모든 타입에 들어갈 수 있는 NULL이란 개념을 넣지 말았어야 했다.
NULL이 도입되면서 Null Pointer Exception은 온갖 버그를 일으켰다.
Null 대신 Optional을 사용한다면 해당 오류를 컴파일 타임에 잡아낼 수 있다.

참고: 프로그래밍 교육에서 실습 언어의 선택
<http://ropas.snu.ac.kr/~kwang/paper/position/edu.pdf>

자세한 설명은 11장에서 계속...

## Pattern Matching

보통 패턴 매칭이라고 하면 정규식 패턴 매칭 (regular expression pattern matching)를 떠올리고는 한다. 실제로 구글에 자바 패턴 매칭이라고 하면 regex에 대한 글이 아직까지는 더 많은 걸 확인할 수 있다.

이 책에서 소개하는 패턴 매칭은 모양새가 비슷해보여도 완전히 다른 개념이다.
단순히 string 패턴을 매칭하는 것이 아니라, 오브젝트 구조 자체를 매칭할 수 있어 특정 상황에서 조건문을 매우 간결하고 엄밀하게 작성할 수 있다.
If-then-else나 switch문보다 훨씬 강력한 고급 조건문을 만들 수 있다.

책에서는 패턴 매칭을 완벽하게 지원하지 않는다고 소개했지만, 자바가 점점 발전하면서 현대의 자바에서는 나름 성과를 거두고 있다.

참고:

- (책 링크) Pattern Matching Preview - 자바 14 <https://openjdk.org/jeps/305>
- Pattern Matching - 자바 16 <https://openjdk.org/jeps/394>
- Pattern Matching for switch Preview - 자바 19 <https://openjdk.org/jeps/427>
-

### 패턴매칭의 장점

1. 더 간결한 조건문을 작성할 수 있다.
1. 특정 언어나 상황에서 Exhaustive matching을 통해 프로그래머의 실수를 컴파일 타임에 방지할 수 있다.
   즉, 조건문에서 가능한 모든 경우를 프로그래머가 직접 명시하거나 예외 처리 해주지 않으면 컴파일 에러가 발생한다.

### java 에서의 패턴 매칭

```java
if (obj instance of String) {
   String s = (String) obj;
   if (s.length() > 5) {
      // ...
   }
}

// after pattern matching support

if (obj instanceof String s && s.length() > 5) {
   ...
}
```

위 코드 예시처럼 여러 줄을 필요로 하는 instanceof 체크와 캐스팅을 단 한 줄로 줄일 수 있다.

### 타 언어에서의 패턴 매칭

<img width="588" alt="image" src="https://user-images.githubusercontent.com/97447334/229984893-1425ecb7-cece-4407-99d3-f31588ce8623.png">

설명은 아주 간단한데 푸는 난이도는 그렇지 않은 간단한 계산기 문제이다.

이와 같은 문제를 패턴 매칭 없이 해결하기 위해 자바에서는 매번 수십 줄의 instanceof 체크와 캐스팅, if-else 문을 작성해야 한다.

그러나 책에서 언급된 하스켈, 오카멜 등의 함수형 언어에서 지원하는 강력한 패턴 매칭 기능을 사용하면 단 몇 줄로 코드를 줄일 수 있다.

#### Ocaml에서의 패턴 매칭

[위 문제에 대한 예시 Solution](calculator-ocaml/main.ml)

<https://try.ocamlpro.com>

위 링크에서 실행하면 된다.

#### Python에서의 패턴 매칭

<https://peps.python.org/pep-0636/>

```python
from dataclasses import dataclass

@dataclass
class Point:
    x: int
    y: int

def where_is(point):
    match point:
        case Point(x=0, y=0):
            print("Origin")
        case Point(x=0, y=y):
            print(f"Y={y}")
        case Point(x=x, y=0):
            print(f"X={x}")
        case Point():
            print("Somewhere else")
        case _:
            print("Not a point")
```

파이썬도 3.10 버전부터 훌륭한 패턴 매칭을 지원한다.

### java 패턴매칭의 미래

> Future JEPs will enhance the Java programming language with pattern matching for other language constructs, such as switch expressions and statements.

아직 다른 함수형 언어들에서 지원하는 강력한 패턴 매칭 기능들을 모두 지원하는 것은 아니지만, 이를 따라잡으려고 꾸준히 발전하고 있다.

<https://openjdk.org/projects/jdk/20/>

<img width="493" alt="image" src="https://user-images.githubusercontent.com/97447334/229984906-01fd000a-e9d1-4dec-9694-c4d498c2f494.png">

위는 2023년 3월 21일에 릴리즈된 자바 20의 기능들이다.

- Record Patterns
- Pattern Matching for switch

등 다양한 고급 패턴 매칭 기능들이 추가되고 있다.
시간이 남으면 살펴보자.

자바 20을 설치한 뒤 각각의 폴더에 들어가 다음과 같이 실행할 수 있다.

<img width="725" alt="image" src="https://user-images.githubusercontent.com/97447334/229984950-3a638daf-3795-49d8-9012-d4241cc9765b.png">


#### Pattern Matching for switch


<img width="596" alt="image" src="https://user-images.githubusercontent.com/97447334/229984972-ce67c912-ec8c-421d-962c-4c8055e8f809.png">

#### Record Patterns

<img width="486" alt="image" src="https://user-images.githubusercontent.com/97447334/229985784-b4ebdc63-564d-4511-9c3d-5fc6e289e9e8.png">

#### java pattern matching 아쉬운 점

아직 좀 더 유용한 형태의 Record Pattern을 지원하려면 많은 시간이 걸릴 것 같다.

``` java

void printSum(Object obj) {
    if (obj instanceof Point(1234, int _)) { // does not work...
      System.out.println("x is 1234");
    }
    else if (obj instanceof Point(int _, int _)) {
      ...
    }
}

```

위 수도 자바코드처럼 Object destructuring과 동시에 값을 비교할 수 있으면 더 유용할 것이다.


## Optional과 Pattern Matching을 동시에 설명하는 이유가 무엇일까?

둘을 같이 쓰면 유용하다!

``` java

Optional<ABC> maybeABC;
if (maybeABC instanceof ABC abc) {
   ...
} else {
   raise Exception!!!
}

```

위와 같이 사용한다면 
```java
ABC abc = maybeABC.get();
```
한 줄을 생략할 수 있다.

물론 현재는 패턴 매칭 기능이 빈약하기 때문에 옵셔널을 if-else문보다는 ifPresent, orElse 와 같은 내장 메서드와 함께 사용하는 것을 권장한다.

다른 언어에서는 위와 같이 Optional을 사용할 때에 else 문을 작성하지 않는다면 강제적으로 컴파일 에러를 발생시킨다. 이를 통해 안전하게 NullPointerException을 방지한다. (Exhaustive pattern matching)

## CompletableFuture

<https://stackoverflow.com/questions/37403210/concept-of-promises-in-java>

15, 16, 17에서 비동기 프로그래밍에 대해 더 자세히 설명하겠지만, 찾아보니 JS의 프로미스와 비슷한 개념인 듯 하다.

이 책의 가장 중요한 챕터들 중 하나인 것 같다.