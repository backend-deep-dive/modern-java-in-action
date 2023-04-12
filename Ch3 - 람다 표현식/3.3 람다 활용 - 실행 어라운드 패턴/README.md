# 3.3 람다 활용 - 실행 어라운드 패턴

자원처리(DB 파일처리 등)에 사용하는 순환 패턴은 자원을 열고, 처리한 다음에, 자원을 닫는 순서로 이루어진다.<br>
설정-작업-정리 형식의 코드를 **실행 어라운드 패턴**(execute around pattern)이라고 부른다.

자바 7에서는 try-with-resource 구문을 사용한다. 이를 사용하면 자원을 명시적으로 닫을 필요가 없으므로 간결한 코드를 구현하는 데 도움을 준다.

- 예제: 파일에서 한 행을 읽는 코드

```java
public String processFile() throws IOException {
  try ( BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    return br.readLine(); // 실제 작업
  }
}
```

try 괄호 안에 Open, Close할 자원을 명시하고 중괄호 안에 Process 로직을 넣는다.

## 3.3.1. 1단계: 동작 파라미터화를 기억하라

위 코드는 요구사항 변경에 대응하지 못한다. processFile의 동작을 파라미터화하면 기존의 설정, 정리 과정을 재사용하고 메서드만 다른 동작을 수행하도록 할 수 있다.

함수형 인터페이스에 람다를 사용하기 위해, BufferReader를 인수로 받아서 String을 반환하는 람다를 구현해보자.

- 한 번에 두 행 출력하는 코드

```java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 3.3.2. 2단계: 함수형 인터페이스를 이용해서 동작 전달

함수형 인터페이스 자리에 람다를 사용할 수 있다. (람다는 파라미터로 ‘코드’를 넘기는 기술로, ‘코드’를 담을 ‘그릇’이 필요한데 함수형 인터페이스가 그 역할을 한다.)

따라서 BufferedReader → String과 IOException을 throw할 수 있는 시그니처와 일치하는 함수형 인터페이스 BufferedReaderProcessor를 생성한다.

```java
@FunctionalInterface
public interface BufferedReaderProcessor {
  String process(BufferedReader b) throws IOException; *// 추상 메서드 1개 -> process라는 이름으로 BufferedReader 객체를 입력 받고 String을 반환*
}
```

정의한 인터페이스를 processFile 메서드의 인수로 전달할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
  // ...
}
```

## 3.3.3. 3단계: 동작 실행

이제 BufferedReaderProcessor에 정의된 process 메서드의 시그니처(BufferedReader -> String)와 일치하는 람다를 전달할 수 있다.

람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으며 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리된다. 

따라서 processFile 바디 내에서 BufferedReaderProcessor 객체의 process를 호출할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
  try (BufferedReader br = new BufferedReader(new fileReader("data.txt"))) {
    return p.process(br); // BufferedReader 객체 처리
  }
}
```

## 3.3.4. 4단계: 람다 전달

이제 람다를 이용해서 다양한 동작을 processFile 메서드로 전달할 수 있다.

```java
String oneLine = processFile((BufferedReader br) -> br.readline());
String twoLines = processFile((BufferedReader br) -> br.readline() + br.readline());
```

이렇듯 언제든 변화 가능한 process 코드를 파라미터로 분리하여 언제든 대응 가능하도록 구성한 패턴이 실행 어라운드 패턴이다. 그리고 실행 어라운드 패턴에서 코드를 넘길 때 사용하는 기술이 람다(Lambda)이다.

## 정리
![](images/picture3-3.png)
