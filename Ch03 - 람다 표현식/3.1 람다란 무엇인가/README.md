# 3.1. 람다란 무엇인가?

람다 표현식이란 메소드로 전달할 수 있는 익명 함수를 단순화한 것이다.

### 람다의 특징

1. 익명

   보통의 메소드와 달리 이름이 없으므로 **익명**이라 표현

2. 함수

   람다는 메소드처럼 특정 클래스에 종속되지 않으므로 함수라고 부름

   (메소드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트 포함)

3. 전달

   람다 표현식을 메소드 인수로 전달하거나 변수로 저장 가능

4. 간결성

   익명 클래스처럼 많은 자질구레한 코드 구현할 필요 없음

### 람다 표현식이 중요한 이유

- 람다를 이용해 간결한 방식으로 코드 전달 가능

ex) 커스텀 Comparator 객체

| 기존 코드

```java
Comparator<Apple> byWeight = new Comparator<Apple>() {
    public int compare(Apple apple1, Apple apple2) {
        return apple1.getWeight().compareTo(apple2.getWeight());
    }
};
```

| 람다 표현식 사용

```java
Comparator<Apple> byWeight = (Apple apple1, Apple apple2) -> apple1.getWeight().compareTo(apple2.getWeight());
```

람다 표현식을 이용하면 compare 메소드의 바디를 직접 전달하는 것처럼 코드를 전달할 수 있다.

### 람다 표현식 구성

1. 파라미터 리스트

   Comparator의 compare 메소드 파라미터(사과 두 개)
   
2. 화살표

   화살표(->)는 람다의 파라미터 리스트와 바디를 구분

3. 람다 바디

   두 사과의 무게 비교(람다의 반환값에 해당)

### 람다 예제

1. 불리언 표현식

   ```java
   (List<String> list) -> list.isEmpty()
   ```

2. 객체 생성

   ```java
   () -> new Apple(10)
   ```

3. 객체에서 소비

   ```java
   (Apple apple) -> {
        System.out.println(apple.getWeight());
   }
   ```

4. 객체에서 선택/추출

   ```java
   (String s) -> s.length()
   ```

5. 두 값을 조합

   ```java
   (int a, int b) -> a * b
   ```

6. 두 객체 비교

   ```java
   (Apple apple1, Apple apple2) -> apple1.getWeight().compareTo(apple2.getWeight())
   ```