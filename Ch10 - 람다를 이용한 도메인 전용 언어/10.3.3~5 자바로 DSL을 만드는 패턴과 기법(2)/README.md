# 10.3 자바로 DSL 만드는 패턴과 기법

## 10.3.1 메서드 체인

생략. 이전 페이지 참고…

## 10.3.2 중첩된 함수 이용

생략. 이전 페이지 참고…

## 10.3.3 람다 표현식을 이용한 함수 시퀀싱

```java
Order order = order(o -> {
	o.forCustomer("BigBank");
	o.buy(t -> {
		t.quantity(80);
		t.price(125.00);
		t.stock(s -> {
			s.symbol("IBM");
			s.market("NYSE");
		});
	});
	o.sell( t -> {
	...
	});
})
```

이런 식으로 람다 표현식으로 사용자가 인수를 구현할 수 있게 해서 자유도를 높인다.

여기서 잠깐! new Order 도 아니고 order 함수는 무엇일까…? 알고보니 Static import 였다. 가독성이 매우 안 좋다.

```java
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.at;
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.buy;
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.on;
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.order;
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.sell;
import static modernjavainaction.chap10.dsl.NestedFunctionOrderBuilder.stock;
```

아…

### 구현

```java
  public static Order order(Consumer<LambdaOrderBuilder> consumer) {
    LambdaOrderBuilder builder = new LambdaOrderBuilder();
    consumer.accept(builder);
    return builder.order;
  }
```

즉 빌더의 private member 인 order 를 돌려준다.

```java
public class Order {

  private String customer;
  private List<Trade> trades = new ArrayList<>();

  public void addTrade( Trade trade ) {
    trades.add( trade );
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer( String customer ) {
    this.customer = customer;
  }

  public double getValue() {
    return trades.stream().mapToDouble( Trade::getValue ).sum();
  }

  @Override
  public String toString() {
    String strTrades = trades.stream().map(t -> "  " + t).collect(Collectors.joining("\n", "[\n", "\n]"));
    return String.format("Order[customer=%s, trades=%s]", customer, strTrades);
  }

}

```

오더는 이렇게 생긴 친구이고,

static order 함수에 인자로 들어가는 친구는 LambdaOrderBuilder 를 인자로 받는 Consumer 이다. 즉,

```java
o -> {
...
}
```

해당 람다가

```java
Consumer<LambdaOrderBuilder> consumer
```

로 변환이 된다.

그리고 `o` 는 LambdaOrderBuilder 이다. 때문에 setCustomer 함수가 아니라 forCustomer 함수를 호출하는 것이다.

`o` 가 Order 인줄 알았는데 헷갈리면 안된다.

그런데 왜 이름을 Builder 라고 지은 것일까? 빌더 패턴으로서의 면모가 보이질 않는다. 이 점이 의문이다.

팩토리라고 한다면 모를까…

순차적으로 forCustomer, buy, sell 함수등을 통해서 order 의 상태를 정해주는 모습이 보인다.

그 와중에 buy 와 sell 함수는 또 TradeBuilder 를 인자로 받아서 Trade 의 상태를 따로 정해준다.
이것도 왜 TradeBuilder 인지 의문이다.

최종적으로 static order 함수는 builder 가 consumer 를 통해 만든 private Order order 를 반환한다.

### 장점

- 메서드 체인 패턴처럼 플루언트 방식으로 거래 주문 정의 가능.
- 중첩 함수 형식처럼 도메인 객체의 계층 구조를 유지.

## 10.3.4 조합하기

지금까지 살펴본 세가지 DSL 의 장단점을 조합한다.

- 메서드 체인
- 중첩된 함수
- 람다 함수 시퀀싱

중첩 함수 -> 람다 표현식 -> 메서드 체인

혼용하기

```java
    public void mixed() {
        Order order =
                forCustomer("BigBank",
                        buy(t -> t.quantity(80)
                                .stock("IBM")
                                .on("NYSE")
                                .at(125.00)),
                        sell(t -> t.quantity(50)
                                .stock("GOOGLE")
                                .on("NASDAQ")
                                .at(375.00)));

        System.out.println("Mixed:");
        System.out.println(order);
    }
```

비교적 깔끔하다.

중첩함수 형식을 통해 Order order 를 만들되, Order order 의 trades 목록을 더해 나가는 buy, sell 함수는 여전히 람다표현식을 사용한다. 다만, 트레이드 객체 자체는 객체의 계층 구조가 갈릴 게 없이 단순 빌더로도 충분히 아름답게 만들 수 있으므로 빌더로 만들어졌다.

## 10.3.5 DSL 에서 메서드 참조 사용하기

```java
public class Tax {
	public static double regional(double value) {
		return value * 1.1;
	}

	public static double general(double value) {
		return value * 1.3;
	}

	public static double surcharge(double value) {
		return value * 1.05;
	}
}
```

```java
public static double calculate(Order order, boolean useRegional, boolean useGeneral, boolean useSurcharge) {
    double value = order.getValue();
    if (useRegional) {
      value = Tax.regional(value);
    }
    if (useGeneral) {
      value = Tax.general(value);
    }
    if (useSurcharge) {
      value = Tax.surcharge(value);
    }
    return value;
}
```

```java
double value = calculate(order, true, false, true);
```

끔찍한 방식이다.

### 개선안

```java
double value = new TaxCalculator()
.withTaxRegional()
.withTaxSurcharge()
.calculate(order);
```

훨씬 더 가독성이 있다.

하지만 책에서는 확장성이 제한적이라고 비판한다.

함수형 기능을 이용하면 더 간결하고 유연하게 같은 가독성을 달성할 수 있다고 한다.

### 함수형 개선안

```java
public class TaxCalculator {
    public DoubleUnaryOperator taxFunction = d -> d;

    public TaxCalculator with(DoubleUnaryOperator f) {
	    taxFunction = taxFunction.andThen(f);
	    return this;
    }

    public double calculateF(Order order) {
	    return taxFunction.applyAsDouble(order.getValue());
    }
}
```

```java
    value = new TaxCalculator().with(Tax::regional)
        .with(Tax::surcharge)
        .calculateF(order);
```

메서드 레퍼런스를 재사용해서 유용하고 가독성 높은 코드를 달성했다.

`andThen` 의 적절한 사용법이라고 생각한다.

![](attachments/Pasted%20image%2020230622172754.png)
