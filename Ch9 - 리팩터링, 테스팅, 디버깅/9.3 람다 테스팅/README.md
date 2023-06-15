# 9.3 람다 테스팅

프로그램이 의도대로 동작하는지 확인할 수 있는 단위 테스팅을 진행한다.

## 9.3.1 보이는 람다 표현식의 동작 테스팅
일반적인 메서드는 이름이 존재하기 때문에 단위 테스트를 문제없이 진행할 수 있지만, 람다는 익명이므로 테스트 코드 이름을 호출할 수 없다.<br>
따라서 필요하다면 람다를 필드에 저장해 테스트 할 수 있다. 람다 표현식은
함수형 인터페이스의 인스턴스를 생성한다.

````java
public class OrderProduct {
    private String name;
    private int count;
    private int price;
	....
}

public static class Order {
    public static final ToIntFunction<Order> getTotalPrice =
            (Order o) -> o.getProducts().stream().mapToInt(p -> p.getPrice() * p.getCount()).sum();

    private List<OrderProduct> products;
    ...
}

    @Test
    void test() {
        Order order = new Order(List.of(
                new OrderProduct("TV", 1, 300_000),
                new OrderProduct("공책", 3, 1_000),
                new OrderProduct("컴퓨터", 1, 1_000_000)
        ));
        int totalPrice = Order.getTotalPrice.applyAsInt(order);
        Assertions.assertEquals(totalPrice, 1_303_000);
    }

````

## 9.3.2 람다를 사용하는 메서드의 동작에 집중하라

람다의 목표는 정해진 동작을 다른 메서드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것이다.<br>
그러려면 세부 구현을 포함하는 람다 표현식을 공개하지 말아야 한다.<br>
람다 표현식을 사용하는 메서드의 동작을 테스트함으로써 람다를 공개하지 않으면서도 람다 표현식을 검증할 수 있다.<br>

````java
public static class Order {
...
    public static Order limitProductPrice(Order order, int maxPrice) {
        List<OrderProduct> newProducts = order.getProducts().stream()
                .filter(p -> p.getPrice() <= maxPrice).collect(Collectors.toList());
        return new Order(newProducts);
    }
}

@Test
void test() {
    Order order = new Order(List.of(
            new OrderProduct("TV", 1, 300_000),
            new OrderProduct("공책", 3, 1_000),
            new OrderProduct("컴퓨터", 1, 1_000_000)
    ));
    Order newOrder = Order.limitProductPrice(order, 990_000);
    int totalPrice = Order.getTotalPrice.applyAsInt(newOrder);
    Assertions.assertEquals(totalPrice, 303_000);
}
````

## 9.3.4 고차원 함수 테스팅
함수를 인수로 받거나 다른 함수를 반환하는 메서드(고차원 함수)는 좀 더 사용하기 어렵다.<br>
메서드가 람다를 인수로 받는다면 다른 람다로 메서드의 동작을 테스트 할 수 있다.<br>
```java
@Test
public void testFilter() throws Exception {
List<Integer> numbers = Arrays.asList(1,2,3,4);
List<Integer> even = filter(numbers, i -> i%2 == 0);
List<Integer> smallerThanThree = filter(numbers, i->i<3);
assertEquals(Arrays.asList(2,4), even);
assertEquals(Arrays.asList(1,2), smallerThanThree);
```
