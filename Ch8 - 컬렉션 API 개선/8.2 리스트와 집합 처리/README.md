# 8.2 리스트와 집합 처리

자바 8에서는 List, Set 인터페이스에 다음과 같은 메서드를 추가했다.

- removeIf : 프레디케이트를 만족하는 요소를 제거한다.
- replaceAll : UnaryOperator 함수를 이용해 요소를 바꾼다. 리스트에서 사용할 수 있다.
- sort : 리스트를 정렬한다.

이 메서드는 새로운 결과를 만드는 스트림 동작과 달리 호출한 기존 컬렉션 자체를 바꾼다.

컬렉션을 바꾸는 동작은 에러를 유발하며 복잡함을 더하기 때문에 자바 8에서는 removeIf와 replaceAll을 추가했다.

## 8.2.1 removeIf 메서드
다음은 숫자로 시작되는 참조 코드를 가진 트랜잭션을 삭제하는 코드다.

```java
for(Transaction transaction : transactions) {
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    transaction.remove(transaction);
  }
}
```

코드를 실행해보면 ConcurrentModificationException을 일으킨다.

forEach 루프는 Iterator 객체를 사용하므로 위 코드는 다음과 같이 해석된다.

```java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
  Transaction transaction = iterator.next();
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    transactions.remove(transaction);
    // 반복자를 사용해 리스트를 순회하면서, 리스트를 직접 수정하고 있음 -> 문제 발생
  }
}
```

Iterator 객체를 통해 소스를 질의하고, Collection 객체 자체에 remove()를 호출해 요소를 삭제하고 있다. 

따라서 반복자의 상태와 컬렉션의 상태가 동기화되지 않는다.

Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출해줘야 정상적으로 동작한다.

```java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
  Transaction transaction = iterator.next();
  if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
    iterator.remove();
  }
}
```

위의 복잡한 코드 패턴은 자바 8의 removeIf 메서드로 바꿀 수 있다. 
removeIf는 삭제할 요소를 가리키는 프레디케이트를 인수로 받는다.

```java
transactions.removeIf(transaction ->
  Character.isDigit(transaction.getReferenceCode().charAt(0))); // True인 경우 제거
```

## 8.2.2 replaceAll 메서드

replaceIf처럼 요소를 제거하는 게 아니라 바꿔야 하는 상황일 때, replaceAll을 사용할 수 있다.

List 인터페이스의 replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.

아래는 스트림 API를 사용한 예제이다.

```java
referenceCodes.stream() // [a12, C14, b13]
    .map(code -> Character.toUpperCase(code.charAt(0)) + code.subString(1))
    // 스트림의 각 요소에 대해 함수를 적용하여 새로운 스트림을 생성
    .collect(Collectors.toList())
    .forEach(System.out::println);
```

```text
// 결과
A12
C14
B13
```

하지만 이 코드는 새 문자열 컬렉션을 만든다. 기존 컬렉션을 바꾸려면 ListIterator 객체를 이용해야 한다.

```java
for(ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {
  String code = iterator.next();
  iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
  // 변경된 값을 iterator.set() 메서드를 사용하여 현재 요소에 대입하여 수정
}
```

코드가 복잡해졌다. 그리고 이처럼 컬렉션 객체와 Iterator 객체를 혼용하면 반복과 변경이 동시에 이루어져 쉽게 문제를 일으킨다.

자바 8의 replaceAll 메서드를 사용하면 간단하게 구현할 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));

```

다음 8.3절에서는 Map 인터페이스에 추가된 새 기능을 설명한다.
