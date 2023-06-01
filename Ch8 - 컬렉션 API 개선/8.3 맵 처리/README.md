# 8.3. 맵 처리

자바 8에서는 Map 인터페이스에 디폴트 메소드를 추가했다.

### 8.3.1. forEach 메소드

맵에서 키와 값을 반복하는 작업을 위해 자바 8에서부터 Map 인터페이스는 BiConsumer(키와 값을 인수로 받음)를 인수로 받는 `forEach` 메소드를 제공한다.

```java
// forEach 메소드 미사용
for(Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
    String friend = entry.getKey();
    Integer age = entry.getValue();
    System.out.println(friend + " is " + age + " years old");
}

// forEach 메소드 사용
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```

### 8.3.2 정렬 메소드

`Entry.comparingByValue`, `Entry.comparingByKey`를 이용하면 맵의 항목을 값 또는 키를 기준으로 정려할 수 있다.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
        entry("Cristina", "Matrix"),
        entry("Olivia", "James Bond"));

favouriteMovies.entrySet()
        .stream()
        .sorted(Entry.comparingByKey()) // 사람의 이름을 알파벳 순으로 스트림 요소 처리
        .forEachOrdered(System.out::println);
```

> #### HashMap 성능
> 자바 8에서는 HashMap의 내부 구조를 바꿔 성능을 개선했다. 
> 
> 기존의 맵의 항목은 많은 키가 같은 해시코드를 반환하는 상황이 되면 O(n)의 시간이 걸리는 LinkedList로 버킷을 반환해야 하므로 성능이 저하되었다.
> 
> 최근에는 버킷이 너무 커지면 O(log(n))의 시간이 소요되는 정렬된 트리를 이용해 동적으로 치환해 충돌이 일어나는 요소 반환 성능을 개선했다. (키가 Comparable 형태여야 정렬된 트리를 지원)

### 8.3.3 getOrDefault 메서드

키가 존재하지 않으면 결과가 null이 반환되므로 NullPointerException을 방지하기 위해 요청 결과의 null 여부를 확인해야 했다.

기본값을 반환하는 `getOrDefault` 메서드는 첫 번째 인수로 키를, 두 번째 인수로 기본값을 받아 맵에 키가 존재하지 않으면 두 번째 인수로 받은 기본값을 반환한다.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"), entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));   // James Bond 출력
System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix"));  // Matrix 출력
```

키가 존재하더라도 값이 null인 상황에서는 `getOrDefault`가 null을 반환할 수 있다.

### 8.3.4 계산 패턴

키 존재 여부에 따라 어떤 동작을 실행하고 결과를 저장해야하는 상황이 필요한 때가 있다.

다음의 3가지 연산이 이런 상황에 도움이 된다.

- `computeIfAbsent` : 제공된 키에 해당하는 값이 없으면(혹은 null) 키를 이용해 새 값을 계산하고 맵에 추가한다.
- `computeIfPresent` : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
- `compute` : 제공된 키로 새 값을 계산하고 맵에 저장한다.

| Map을 이용한 캐시 구현

```java
// 캐시
Map<String, byte[]> dataToHash = new HashMap<>();

// 각 라인을 SHA-256의 해시 값으로 계산해서 저장하기 위한 계산 객체
MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

// 키가 없다면 line과 계산된 해시 값이 key,value로 들어감
lines.forEach(line -> dataToHash.computeIfAbsent(line, this::calculateDigest));

// 키의 해시를 계산해서 반환
private byte[] calculateDigest(String key) {
    return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
}
```

| 키가 존재하지 않으면 값을 반환

아래 코드는 키가 존재하면 기존 값에, 존재하지 않으면 새로운 리스트에 'Star Wars'가 추가 된다.

```java
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
        .add("Star Wars");
```

### 8.3.5 삭제 패턴

자바 8에서는 키가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메서드를 제공한다.

| 자바 8 이전

```java
String key = "Raphael";
String value = "Jack Reacher 2";
if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
   favouriteMovies.remove(key);
   return true;
} else {
   return false;
}
```

| 자바 8 이후

```java
favouriteMovies.remove(key, value);
```

### 8.3.6 교체 패턴

맵의 항목을 바꾸는데 사용하는 메서드

- `replaceAll` : BiFunction을 적용한 결과로 각 항목의 값을 교체한다.
- `replace` : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있다.

```java
Map<String, String> favouriteMovies = new HashMap<>();
favouriteMovies.put("Raphael", "Star Wars"); 
favouriteMovies.put("Olivia", "james bond"); 

favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase()); 
```

### 8.3.7 합침

`putAll` 메서드를 이용하여 두 맵을 합칠 수 있다.

그러나 `putAll`은 중복된 키가 있다면 제대로 동작하지 않는다.

이때는 중복된 키를 어떻게 합칠지 결정하는 `BiFunction`을 인수로 받는 `merge` 메서드를 사용한다.

| putAll 사용

```java
Map<String, String> family = Map.ofEntries(entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(entry("Raphael", "Star Wars"));

Map<String, String> everyone = new HashMap<>(family);
everyone.putAll(friends);
```

| merge 사용

중복된 키가 있으면 두 값을 연결하고 없으면 (key, value) 그대로 저장한다.

```java
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((key, value) ->  everyone.merge(key, value, (movie1, movie2) -> movie1 + " & " + movie2));
System.out.println(everyone);
```

| merge를 이용한 초기화 검사

원하는 값이 초기화되어 있으면 +1, 초기화 되어있지 않아 null이면 (movieName, 1) 저장한다.

```java
moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
```