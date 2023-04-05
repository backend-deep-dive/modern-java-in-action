# 동작 파라미터화

참 또는 거짓을 반환하는 함수를 프레디케이트 (predicate) 라고 한다.

<https://www.cs.rochester.edu/u/nelson/courses/csc_173/predlogic/predicates.html#:~:text=A%20predicate%20is%20a%20boolean,a%20predicate%20with%20no%20arguments.>

사과를 인풋으로 받고 참, 거짓을 반환하는 ApplePredicate 인터페이스를 선언한다.

![](images/20230404202722.png)

이후 다양한 버전의 ApplePredicate을 구현하면서 코어 로직과 필터링 로직을 분리할 수 있다.

![](images/20230404202854.png)

이를 전략 디자인 패턴이라고 한다.

## 전략 디자인 패턴

Strategy pattern

<https://en.wikipedia.org/wiki/Strategy_pattern>

OCP를 지키는 패턴이다.

lambda를 사용할 수 없던 시절에 고대의 개발자들이 그와 비슷한 작업을 할 수 있도록 노력한 흔적이다. 현대에는 단순히 인자로 lambda를 넘겨주면 된다.

## 2.2.1 네 번째 시도: 추상적 조건으로 필터링

![](images/20230404205607.png)

필터에서 프레디케이트를 사용하는 방법이다.

### 코드/동작 전달하기

동작 파라미터화의 장점: 컬렉션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다.

![](images/20230404205745.png)

결국 노란색으로 색칠된 로직이 중요한 부분이다. 그 주변에 public class, test메서드 등은 오직 고대 자바의 문법적 한계를 극복하기 위해 필요한 boilerplate 코드일 뿐이다.


## 퀴즈 2-1: 유연한 prettyPrintApple 메서드 구현하기

format printer도 매우 비슷한 방식으로 구현할 수 있다. test 대신 print 혹은 format string 하는 단일 함수를 가진 SAM (Single Abstract Method) 인터페이스를 만들면 된다.

![](images/20230404210518.png)

## 실습

### 윈도우

 2-2 폴더에 들어가서 powershell을 연 뒤 ./run.ps1 을 실행하면 된다.

### 유닉스

> 주의: 테스트 해보지 않음.

터미널에서 ./run.sh를 실행하면 된다.