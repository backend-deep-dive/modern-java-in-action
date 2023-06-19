# 4.3. 스트림과 컬렉션

공통점

- **연속**된 요소 형식의 값을 저장하는 자료구조 인터페이스 제공

차이점

| |스트림|컬렉션|
|:---:|:---:|:---:|
|데이터 계산 시점|요청할 때만 요소 계산|컬렉션 추가 전 계산|
|데이터 추가 가능|불가능|가능|
|제조 방식|요청 중심 제조/즉석 제조|생상자 중심 제조|
|반복 방식|내부 반복|외부 반복|

## 4.3.1. 딱 한 번만 탐색할 수 있다

반복자와 마찬가지로 스트림도 **한 번만** 탐색할 수 있다. (탐색된 스트림의 요소는 소비됨)

## 4.3.2. 외부 반복과 내부 반복

컬렉션 인터페이스는 사용자가 직접 요소를 반복해야 한다. (외부 반복, external iteration)

스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가 저장해준다. (내부 반복, internal iteration)

| 컬렉션: for-each 루프 이요한 외부 반복

```java
List<String> names = new ArrayList<>();
for (Dish dish : menu) {
    names.add(dish.getName());
}
```

for-each 구문을 이용하면 Iterator 객체 이용할 때보다 더 쉽게 컬렉션을 반복할 수 있다.

| 컬렉션: 내부적으로 숨겨진 반복자를 사용한 외부 반복

```java
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while (iterator.hasNext()) {    // 명시적 반복
    Dish dish = iterator.next();
    names.add(dish.getName());
}
```

| 스트림: 내부 반복

```java
List<String> names = menu.stream()
        .map(Dish::getName) // 요리명 추출
        .collect(Collectors.toList());  // 파이프라인 실행(반복자 필요 없음)
```

내부 반복이 더 좋은 이유

1. 작업을 투명하게 병렬로 처리할 수 있다.
   
2. 더 최적화된 다양한 순서로 처리할 수 있다.

외부 반복에서는 병렬성을 <U>**스스로 관리**</U>해야 한다.

병렬성 스스로 관리란

- 병렬성 포기

- `synchronized`로 관리

스트림은 내부 반복을 사용하므로 반복 과정을 우리가 신경 쓰지 않아도 된다.

하지만 이와 같은 이점을 누리려면 filter나 map 같이 반복을 숨겨주는 연산 리스트가 미리 정의되어 있어야 한다. (동작 파라미터화 활용 가능)