# 2.4 실전 예제
## 2.4.1 Comparator로 정렬하기
자바 8의 List에는 sort 메서드가 포함되어 있다. 다음과 같은 인터페이스를 갖는 `java.util.Comparator` 객체를 이용해서 sort의 동작을 파라미터화 할 수 있다.
```java
//java.util.Comparator
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```
Comparator를 구현해서 sort 메서드의 동작을 다양화 할 수 있다.<br>
예를들어 무게가 적은 순서로 목록에서 사과를 정렬하는 것도 가능하다.
```java
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) { 
        return a1.getWeight().compareTo(a2.getWeight());
    }
});
```
요구사항이 바뀌면 새로운 요구사항에 맞는 Comparator를 만들어 sort메서드에 전달할 수 있다.<br>
위 코드를 람다 표현식으로 이용하면 아래와 같은 코드가 된다.
```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight)));
```
## 2.4.2 Runnable로 코드 블록 실행하기
자바 8까지는 Thread 생성자에 객체만을 전달할 수 있었으므로 보통 결과를 반환하지 않는 void run 메소드를 포함하는 익명 클래스가 Runnable 인터페이스를 구현하도록 하는 것이 일반적인 방법이었다.<br>
자바에서는 Runnable 인터페이스를 이용해서 실행할 코드 블록을 지정할 수 있다.
```java
// java.lang.Runnable
public interface Runnable {
    void run();
}
```
Runnable을 이용해서 다양한 동작을 스레드로 실행할 수 있다.
```java
Thread t = new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello");
    }
});
```
위 코드를 자바 8부터 지원하는 람다 표현식을 이용하면 아래와 같다.
```java
Thread t = new Thread(() -> System.out.println("Hello"));
```