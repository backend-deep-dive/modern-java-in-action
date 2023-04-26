# 5.3. 매핑

스트림 API의 map과 flatMap 메소드는 특정 데이터를 선택하는 기능을 제공한다.

## 5.3.1. 스트림의 각 요소에 함수 적용하기

스트림은 함수를 인수로 받은 map 메소드를 지원한다.

인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.

```java
List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(Collectors.toList());
```

## 5.3.2. 스트림 평면화

영단어가 담긴 리스트에서 각 단어의 알파벳을 포함하는 리스트를 반환한다고 가정하자.

예를 들어, ["Hello", "World"]에서 ["H", "e", "l", "o", "W", "r", "d"]를 포함하는 리스트가 반환되어야 한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .distinct()                     // Stream<String[]>
        .collect(Collectors.toList());  // List<String[]>
```

위 코드에서 map으로 전달한 람다는 각 단어의 String[]을 반환해서 원하는 반환값을 얻을 수 없다.

문자열 배열을 받아 문자열 스트림을 만드는 `Arrays.stream()` 메소드를 사용해보자.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .map(Arrays::stream)            // Stream<Stream<String>>
        .distinct()                     // Stream<Stream<String>>
        .collect(Collectors.toList());  // List<Stream<String>>
```

위 방법은 원하던 결과인 List<String>이 아닌 List<Stream<String>>을 반환해준다.

### flapMap 사용

flatMap 메소드는 스트림의 각 값을 다른 스트림으로 만든 다음 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

```java
words.stream()                          // Stream<String>
        .map(word -> word.split(""))    // Stream<String[]>
        .flatMap(Arrays::stream)        // Stream<String>
        .distinct()                     // Stream<String>
        .collect(Collectors.toList());  // List<String>
```