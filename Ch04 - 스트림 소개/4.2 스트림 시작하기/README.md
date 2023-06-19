# 4.2 스트림 시작하기

## 연속된 요소

콜렉션과의 차점
콜렉션은 데이터 위주, 스트림은 계산, 연산 위주

## 소스

데이터 제공 소스로부터 데이터를 순차적으로 소비한다. 스트림 요소의 순서는 소스 순서와 같다.

## 데이터 처리 연산

데이터베이스와 비슷한 연산을 제공한다.

- map (update)
- filter (= select where)
- reduce (= group by)
- find (select where like %x%)
- match (select where regexp)
- sort (order by)

등으로 데이터를 조작할 수 있다.


## 파이프라이닝

스트림 연산끼리 연결하여 파이프라인을 구성한다.

즉, 
```
stream().
AAA().
BBB().
CCC().
.collect()
```

위와 같은 코드가 있을 때, AAA()가 모두 실행되고 난 다음에야 BBB()가 실행되는 것이 아니라,

**AAA, BBB, CCC로 이루어진 스트림 파이프라인이 구성**되고 난 다음에 스트림이 소스를 소비한다.

이 덕분에 lazy evaluation, short circuit같은 최적화도 쉽게 얻을 수 있다. (5장에서 설명)

## 내부 반복

반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리, 스트림은 내부 반복을 지원한다. 4.3.2절에서 설명

```java
  public static final List<Dish> menu = Arrays.asList(
      new Dish("pork", false, 800, Dish.Type.MEAT),
      new Dish("beef", false, 700, Dish.Type.MEAT),
      new Dish("chicken", false, 400, Dish.Type.MEAT),
      new Dish("french fries", true, 530, Dish.Type.OTHER),
      new Dish("rice", true, 350, Dish.Type.OTHER),
      new Dish("season fruit", true, 120, Dish.Type.OTHER),
      new Dish("pizza", true, 550, Dish.Type.OTHER),
      new Dish("prawns", false, 400, Dish.Type.FISH),
      new Dish("salmon", false, 450, Dish.Type.FISH)
  );
```

```java
  public static void main(String[] args) {
    List<String> names = menu.stream()
        .filter(dish -> {
          System.out.println("filtering " + dish.getName());
          return dish.getCalories() > 300;
        })
        .map(dish -> {
          System.out.println("mapping " + dish.getName());
          return dish.getName();
        })
        .limit(3)
        .collect(toList());
    System.out.println(names);
  }
```

![](Pasted%20image%2020230423163643.png)

내부 반복이 적용되어 pork 먼저 스트림을 전부 탄 다음에 beef가 stream에 소비되는 것을 알 수 있다 (**내부 반복**).

limit3의 제한에 걸려서 french fries나 rice같은 객체들이 소비되기 전에 스트림 연산이 종료되었다 (**short circuit**).

5장에서 이 예제를 더 자세히 설명한다.

**4.3에서 컬랙션 API와 스트림 API의 차이점에 대해 설명한다.**