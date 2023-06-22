# 10.3 자바로 DSL을 만드는 패턴과 기법

간단한 도메인 모델을 정의해보면서 DSL을 만드는 패턴을 알아보자.

예제 도메인 모델은 3가지로 구성된다.

1. 주어진 시장에 주식 가격을 모델링 하는 순수 자바 빈즈 `Stock.java`

```java
public class Stock {

  private String symbol; // 주식
  private String market; // 시장

  //getter, setter

}
```

2. 주어진 가격에서 주어진 양의 주식을 사거나 파는 거래 `Trade.java`

```java
public class Trade {

  public enum Type {
    BUY,
    SELL
  }

  private Type type;
  private Stock stock;
  private int quantity;
  private double price;

  //getter, setter

}
```

3. 고객이 요청한 한 개 이상의 거래의 주문 `Order.java`

```java
public class Order {

  private String customer;
  private List<Trade> trades = new ArrayList<>();

  //getter, setter

  public void addTrade( Trade trade ) {
    trades.add( trade );
  }

  public double getValue() {
    return trades.stream().mapToDouble( Trade::getValue ).sum();
  }
}
```

예제 10-4. 도메인 객체의 API를 직접 이용해 주식 거래 주문 만들기

```java

    Order order = new Order();
    order.setCustomer("BigBank");

    Trade trade1 = new Trade();
    trade1.setType(Trade.Type.BUY);

    Stock stock1 = new Stock();
    stock1.setSymbol("IBM");
    stock1.setMarket("NYSE");

    trade1.setStock(stock1);
    trade1.setPrice(125.00);
    trade1.setQuantity(80);
    order.addTrade(trade1);

    Trade trade2 = new Trade();
    trade2.setType(Trade.Type.BUY);

    Stock stock2 = new Stock();
    stock2.setSymbol("GOOGLE");
    stock2.setMarket("NASDAQ");

    trade2.setStock(stock2);
    trade2.setPrice(375.00);
    trade2.setQuantity(50);
    order.addTrade(trade2);
```

```java
// 결과
Order[customer=BigBank, trades=[
  Trade[type=BUY, stock=Stock[symbol=IBM, market=NYSE], quantity=80, price=125.00]
  Trade[type=BUY, stock=Stock[symbol=GOOGLE, market=NASDAQ], quantity=50, price=375.00]
]]
```

이러한 코드는 장황하고 비개발자인 도메인 전문가가 이해하고 검증하기 어렵다.

직관적으로 도메인 모델을 반영할 수 있는 DSL이 필요하다. 이 책에서는 다음과 같은 DSL 패턴들을 소개하고 있다.


> **DSL 패턴 세 가지**
> 
- 메서드 체인 (10.3.1)
- 중첩된 함수 (10.3.2)
- 람다 표현식을 이용한 함수 시퀀싱 (10.3.3)

## 10.3.1 메서드 체인

메서드 체인은 DSL 에서 가장 흔한 방식 중 하나이다.

한 개의 메서드 호출 체인으로 거래 주문을 정의할 수 있다.

예제 10-5. 메서드 체인으로 주식 거래 주문 만들기

```java
Order order = forCustomer("BigBank")
        .buy(80)
				.stock("IBM")
				.on("NYSE")
				.at(125.00)
        .sell(50)
				.stock("GOOGLE")
				.on("NASDAQ")
				.at(375.00)
        .end();
```

이러한 코드를 만들기 위해서 아래와 같이 DSL을 구현해야 한다.

- 플루언트 API로 도메인 객체를 만드는 빌더 구현
- 최상위 수준 빌더를 만들고 주문을 감싸서 거래를 추가할 수 있도록 구현

>  Fluent API란? (fluent interface)
> 
>    - 메서드 체이닝에 상당 부분 기반한 객체 지향 API 설계 메서드
>    - 내부 DSL 라인을 따라 작업을 수행하는 것
>    - 소스 코드의 가독성을 산문과 유사하게 만드는 것이 목적
>    - 인터페이스 안에 **도메인 특화 언어**(**DSL**)를 작성


예제 10-6. 메서드 체인 DSL을 제공하는 주문 빌더 `MethodChainingOrderBuilder.java`

```java
public class MethodChainingOrderBuilder {

  public final Order order = new Order(); // 빌더로 감싼 주문

  private MethodChainingOrderBuilder(String customer) {
    order.setCustomer(customer); // Order 개체 초기화
  }

  public static MethodChainingOrderBuilder forCustomer(String customer) {
    return new MethodChainingOrderBuilder(customer); // 고객의 주문을 만드는 정적 팩토리 메서드
// MethodChainingOrderBuilder의 새 인스턴스를 생성하고 메서드 체인을 시작하는데 사용 - 고객 이름을 매개변수로 사용하고 빌더 객체 반환
  }

  public TradeBuilder buy(int quantity) { // 주식을 사는 TradeBuilder 생성
    return new TradeBuilder(this, Trade.Type.BUY, quantity);
  }

  public TradeBuilder sell(int quantity) { // 주식을 파는 TradeBuilder 생성
    return new TradeBuilder(this, Trade.Type.SELL, quantity);
  }

  private MethodChainingOrderBuilder addTrade(Trade trade) {
    order.addTrade(trade); // 주문에 주식 거래 추가
    return this; // 유연하게 추가 주문을 만들어 추가할 수 있도록 주문 빌더 자체를 반환
  }

  public Order end() {
    return order; // 주문 만들기를 종료하고 반환
  }

  public static class TradeBuilder { // Stock 클래스의 인스턴스를 만드는 TradeBuilder의 공개 메서드

    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    private TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) { // 상위 빌더, 거래 유형(매수/매도), 주식 수량을 매개변수로 사용해 TradeBuilder 객체 초기화하는 생성자
      this.builder = builder;
      trade.setType(type);
      trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
      return new StockBuilder(builder, trade, symbol);
    }

  }

  public static class StockBuilder { // 주식 관련 구성 처리

    private final MethodChainingOrderBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    private StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
      this.builder = builder;
      this.trade = trade;
      stock.setSymbol(symbol);
    }

    public TradeBuilderWithStock on(String market) {
      stock.setMarket(market); // 주식 시장 설정
      trade.setStock(stock); // 거래에 주식 추가
      return new TradeBuilderWithStock(builder, trade); // 빌더 반환
    }

  }

  public static class TradeBuilderWithStock { // 거래되는 주식의 단위 가격을 설정한 후 원래 주문 빌더를 반환
// 메서드 체인의 마지막 단계

    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
      this.builder = builder;
      this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) { // 거래된 주식 단가 설정하고 원래 MethodChainingBuilder 객체를 반환
      trade.setPrice(price);
      return builder.addTrade(trade);
    }
  }
}
```


이렇게 메서드 체인 DSL을 이용하면 이처럼 MethodChainingOrderBuilder가 끝날 때까지 다른 거래를 플루언트 방식으로 추가할 수 있다.

- 메서드 체인 DSL의 장점
    - 사용자가 지정된 절차에 따라 플루언트 API의 메서드를 호출하도록 강제
    - 주문에 사용한 파라미터가 빌더 내부로 국한됨
    - 정적 메서드 사용을 최소화하거나 없앨 수 있음
    - 메서드 이름이 인수의 이름을 대신하여 DSL의 가독성 개선
    - 문법적 잡음이 최소화

- 메서드 체인 DSL의 단점
    - 전체 빌더를 구현해야 함
    - 상위 수준의 빌더를 하위 수준의 빌더와 연결할 많은 접착 코드가 필요
    - 도멘인 객체의 중첩 구조와 일치하게 들여쓰기를 강제할 수 없음

## 10.3.2 중첩된 함수 이용

다른 함수 안에 함수를 이용해 도메인 모델을 만든다.

예제 10-7. 중첩된 함수로 주식 거래 만들기

```java
Order order = order("BigBank",
        buy(80,
            stock("IBM", on("NYSE")),
            at(125.00)),
        sell(50,
            stock("GOOGLE", on("NASDAQ")),
            at(375.00))
    );
```

메서드 체인 DSL보다 비교적 간단하다.

아래와 같은 DSL 형식으로 사용자에게 API를 제공할 수 있다.

예제 10-8. 중첩된 함수 DSL을 제공하는 주문 빌더 `NestedFunctionOrderBuilder.java`

```java
import java.util.stream.Stream;

public class NestedFunctionOrderBuilder {

  public static Order order(String customer, Trade... trades) {
    Order order = new Order(); // 해당 고객의 주문 만들기
    order.setCustomer(customer);
    Stream.of(trades).forEach(order::addTrade); // 주문에 모든 거래 추가
    return order;
  }

  public static Trade buy(int quantity, Stock stock, double price) {
    return buildTrade(quantity, stock, price, Trade.Type.BUY); // 주식 매수 거래 만들기
  }

  public static Trade sell(int quantity, Stock stock, double price) {
    return buildTrade(quantity, stock, price, Trade.Type.SELL); // 주식 매도 거래 만들기
  }

  private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type buy) {
    Trade trade = new Trade();
    trade.setQuantity(quantity);
    trade.setType(buy);
    trade.setStock(stock);
    trade.setPrice(price);
    return trade;
  }

  public static double at(double price) { // 거래된 주식의 단가를 정의하는 더미 메서드
    return price;
  }

  public static Stock stock(String symbol, String market) {
    Stock stock = new Stock(); // 거래된 주식 만들기
    stock.setSymbol(symbol);
    stock.setMarket(market);
    return stock;
  }

  public static String on(String market) {
    return market; // 주식이 거래된 시장을 정의하는 더미 메서드 정의
  }

}
```

- 중첩 함수 DSL 장점
    - 중첩 방식이 도메인 객체 계층 구조에 그대로 반영

- 중첩 함수 DSL 단점
    - 결과 DSL 에 더 많은 괄호를 사용
    - 정적 메서드 사용이 빈번
    - 인수 목록을 정적 메서드에 넘겨줘야 함
    - 도메인에 선택 사항 필드가 있으면 인수를 생략할 수 있으므로 메서드 오버라이드 구현 필요
    - 인수의 의미가 이름이 아니라 위치에 의해 정의됨
        → 인수의 역할을 확실하게 만드는 여러 더미메서드( at(), on() )를 이용해 해결책 제시
        

여기까지 살펴본 두 가지 DSL 패턴에는 람다 표현식을 사용하지 않았다. 

10.3.3 절에서는 자바 8에서 추가된 함수형 기능을 활용하는 세 번째 기법을 설명한다.
