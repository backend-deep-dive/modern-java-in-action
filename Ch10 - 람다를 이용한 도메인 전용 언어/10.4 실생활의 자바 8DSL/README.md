# 10.4. 실생활의 자바 8 DSL

### DSL 패턴의 장점과 단점

![image](https://github.com/deingvelop/modern-java-in-action/assets/100582309/e0708dd4-e354-4fd2-95ec-685e936e492d)


<br><br>

# 10.4.1. jOOQ
: SQL을 구현하는 내부적 DSL
- 자바에 직접 내장된 형식 안전 언어

> SQL : DSL이 가장 흔히, 광범위하게 사용되는 분야

### 원리
- 소스코드 생성기 : DB 스키마를 역공학함
  - 자바 컴파일러가 복잡한 SQL 구문의 형식을 확인할 수 있도록 도움<br>(역공학 : 주어진 실물로부터 공학적 개념이나 형상모델을 추출해내는 과정)
  - 역공학 프로세스 제품이 생성한 정보를 기반으로 DB 스키마를 탐색할 수 있음

<br>

### 활용 예시
- SQL Query
  ```sql
  SELECT * FROM BOOK 
  WHERE BOOK.PUBLISHED_IN = 2016 
  ORDER BY BOOK.TITLE
  ```

- jOOQ DSL
  ```java
  create.selectFrom(BOOK) 
		.where(BOOK.PUBLISHED_IN_eq(2016)) 
		.orderBy(BOOK.TITLE)
  ```

<br>

### 특징
- 스트림 API와 조합하여 사용할 수 있음 → SQL 질의 실행으로 나온 결과를 한 개의 플루언트 구문으로 해서 데이터를 메모리에서 조작할 수 있음
  ```java
  Class.forName( "org.h2.Driver"); 
  try (Connection c = getConnection("jdbc:h2:~/sql-goodies-with-mapping", "sa", "")) {    // SQL DB 연결 만들기
	  DSL.using(c) 
		 .select(BOOK.AUTHOR, BOOK.TITLE)    // 만들어진 DB 연결을 이용하여 jOOQ SQL문 시작
		 .where(BOOK.PUBLISHED_IN.eq (2016))
		 .orderBy(BOOK.TITLE)
	 .fetch()    // jOOQ DSL로 SQL문 정의
	 .stream()    // DB에서 데이터 가져오기!  ------ jOOQ문은 여기서 종료!!
	 .collect(groupingBy(    // stream API로 DB에서 가져온 데이터 처리 시작
		 r -> r.getValue(BOOK.AUTHOR),
		 LinkedHashMap::new, 
		 mapping(r -> r.getValue(BOOK.TITLE), toList())))
		 .forEach((author, titles) -> System.out.println(author + " is author of " + titles));    // 저자 이름 목록, 각 저자가 집필한 책들 출력
  }
  ```
  - jOOQ DSL을 구현하는 데 메서드 체인 패턴을 사용함
  - 잘 만들어진 SQL 질의 문법을 흉내내려면 메서드 체인 패턴의 여러 특성(선택적 파라미터를 허용하고 미리 정해진 순서로 특정 메서드가 호출될 수 있게 강제)이 반드시 필요하기 때문!

<br><br>

# 10.4.2. Cucumber

: 개발자가 비즈니스 시나리오를 평문 영어로 구현할 수 있도록 도와주는 BDD 도구
- DSL 명령문을 실행할 수 있는 테스트케이스로 변환함

> **동작 주도 개발(Behavior-Driven Development, BDD)**
> - 테스트 주도 개발의 확장
> - 다양한 비즈니스 시나리오를 구조적으로 서술하는 간단한 도메인 전용 스크립팅 언어 사용 → 명령문을 실행할 수 있는 테스트 케이스로 변환함
> - BDD를 통한 스크립트 결과물 : 실행할 수 있는 테스트이자, 비즈니스 기능의 수용 기준이 됨<br>
> **장점**
> - 우선 순위에 따른, 확인할 수 있는 비즈니스 가치를 전달하는 개발 노력에 집중함
> - 비즈니스 어휘를 공유함 → 도메인 전문가와 프로그래머 사이의 간격을 줄임

<br>

### 활용 예시

```cucumber
Feature: Buy stock
	Scenario: Buy 10 IBM stocks
		Given the price of a "IBM" stock is 125$ 
		When I buy 10 "IBM" 
		Then the order value should be 1250$
```

- 큐컴버는 세 가지로 구분되는 개념을 사용함
  - `Given` : 전제 조건 정의
  - `When` : 시험하려는 도메인 객체의 실질 호출
  - `Then` : 테스트 케이스의 결과를 확인하는 assertion

<br>

### 특징
- 테스트 시나리오를 정의하는 스크립트 : 제한된 수의 키워드를 제공하며 자유로운 형식으로 문장을 구현할 수 있는 외부 DSL을 활용함
- 이들 문장은 테스트 케이스의 변수를 캡쳐하는 정규 표현식으로 매칭되며, 테스트 자체를 구현하는 메서드로 이를 전달함
- 이전에 나왔던 주식 거래 도메인 모델을 이용하여, Cucumber로 주식 거래 주문의 값이 제대로 계산되었는지 확인하는 테스트 케이스를 개발할 수 있다.
  ```java
  public class BuyStocksSteps { 
	  private Map stockllnitPrices = new HashMap<>(); 
	  private Order order = new Order();
	  
	  @Given("^the price of a \"(.*?)\" stock is (\\d+)\\$$")  // 시나리오 전제 조건인 주식 단가 정의
	  public void setUnitPrice(String stockName, int unitPrice) { 
		  stockUnitValues.put(stockName, unitPrice);    // 주식단가 저장 
	  
	  @When("^I buy (\\d+) \"(.*?)\"$")    // 테스트 대상인 도메인 모델에 행할 액션 정의
	  public void buyStocks(int quantity, String stockName) { 
		  Trade trade = new Trade();    // 적절하게 도메인 모델 도출 
		  trade.setType(Trade.Type.BUY); 
		  
		  Stock stock = new Stock(); 
		  stock.setSymbol(stockName); 
		  
		  trade.setStock(stock); 
		  trade.setPrice(stockUnitPrices.get(stockName)); 
		  trade.setQuantity(quantity); 
		  order.addTrade(trade); 
		  
	  @Then("^the order value should be (\\d+)\\$$") 
	  public void checkOrderValue(int expectedValue) {    // 예상되는 시나리오 결과 정의
		  assertEquals(expectedValue, order.getValue());    // 테스트 어설션 확인
  ```

- Java 8 이후로는 람다 표현식을 활용하여, 두 개의 인수 메서드(기존에 어노테이션 값을 포함한 정규 표현식과 테스트 메서드를 구현하는 람다)를 이용해 어노테이션을 제거하는 다른 문법을 큐컴버로 개발할 수 있다. 
- `When`의 테스트 시나리오를 다음처럼 다시 구현할 수 있다.
  ```java
  public class BuyStocksSteps implements cucumber.api.java8.En { 
	  private Map<String, Integer> stockUnitPrices = new HashMap<>(); 
	  private Order order = new Order(); 
	  public BuyStocksSteps() { 
		  Given("^the price of a \"(.*?)\" stock is (\\d+)\\$$", 
				  (String stockName, int unitPrice) -> { 
					  stockUnitValues.put(stockName, unitPrice);
					  });
					  // ... When과 Then 람다는 편의상 생략 
	  } 
  }
  ```
  -  코드가 더 단순해짐
  - 특히, 테스트 메서드가 무명 람다로 바뀌면서, 의미를 가진 메서드 이름을 찾는 부담이 사라짐

### 의의
- 큐컴버의 DSL은 아주 간단하지만 외부적 DSL과 내부적 DSL이 어떻게 효과적으로 합쳐질 수 있으며 람다와 함께 가독성 있는 함축된 코드를 구현할 수 있는지를 잘 보여줌

<br><br>

# 10.4.3. Spring Integration (스프링 통합)

:  유명한 Enterprise Integration Patterns에 나오는 통합 패턴들을 스프링 프레임워크에 구현해놓은 것

### 핵심 목표
- 복잡한 엔터프라이즈 통합 솔루션을 구현하는 단순한 모델을 제공하고 비동기, 메시지 주도 아키텍처를 쉼게 적용할 수 있게 돕는 것

### 특징
- 의존성 주입에 기반하여 스프링 프로그래밍 모델을 확장한다.
- 스프링 기반 애플리케이션 내의 경량의 원격, 메시징, 스케줄링을 지원함
- 풍부하고 유창한 DSL을 통해, 기존의 스프링 XML 설정 파일 기반에도 이들 기능을 지원함

### 내부 구현
- 메시지 기반의 애플리케이션에 필요한 공통 패턴을 모두 구현함 (ex: channel, endpoints(엔드포인트), Pollers(폴러), channel interceptors(채널 인터셉터), ...)
- 가독성이 높아지도록 엔드포인 트는 DSL에서 동사로 구현하며 여러 엔드포인트를 한 개 이상의 메시지 흐름으로 조합해서 통합 과정이 구성됨
- Spring Integration DSL을 이용하여, 스프링 통합 흐름 설정하기
  ```java
  @Configuration 
  @EnableIntegration 
  public class MyConfiguration {
  
	  @Bean 
	  public MessageSource<?> integerMessageSource() { 
		  MethodlnvokingMessageSource source = new MethodInvokingMessageSource();
		  source.setObject(new Atomiclnteger());
		  source.setMethodName("getAndIncrement");   // 호출시 Atomiclnteger를 증가시키는 새 MessageSource를 생성 
		  return source; 
	  } 
	  
	  @Bean 
	  Public DirectChannel inputChannel() {
		  return new DirectChannel();    // MessageSource에서 도착하는 데이터를 나르는 채널 
	  } 
	  
	  @Bean 
	  public IntegrationFlow myFlow() { 
		  return IntegrationFlows
				  .from(this.integerMessageSource(),   // 기존에 정의한 MessageSource를 IntegrationFlow의 입력으로 사용
						  c -> c.poller(Pollers.fixedRate(10)))   // MessageSource를 폴링하면서 MessageSource가 나르는 데이터를 가져옴
				  .channel(this.inputChannel())
				  .filter((Integer p) -> p % 2 == 0)    // 짝수만 거름
				  .transform(Object::toString)          // Messagesource에서 가져온 정수를 문자열로 변환
				  .channel(MessageChannels.queue("queueChannel"))    // queueChannel을 IntegrationFlow의 결과로 설정
				  .get();    // IntegrationFlow 만들기를 끝나고 반환
	  } 
  }
  ```
  - `myFlow()`가 Spring Integration DSL를 활용하여 `IntegrationFlow`를 만드는 코드
    - 메서드 체인 패턴을 구현하는 `IntegrationFlows` 클래스가 제공하는 유연한 빌더를 사용함
    - 결과 flow는 고정된 속도로 `MessageSource`를 폴링하면서 일련의 정수를 제공하고 → 짝수만 거른 다음 → 문자열로 변환해 → 최종적으로 결과를 자바 8 스트림 API와 비슷한 방법으로 출력 채널에 전달함
    - `inputchannel` 이름만 알고 있다면 이 API를 이용해 flow 내의 모든 컴포넌트로 메시지를 전달할 수 있음

  - 예시 2
  ```java
  @Bean 
  public IntegrationFlow myFlow() { 
	  return flow -> flow.filter((Integer p) -> p % 2 == 0) 
	  				     .transform(Object::toString)
	  				     .handle(System.out::printin); 
  }
  ```

- Spring Integration에서 가장 널리 사용하는 패턴 = 메서드 체인
  - 이 패턴은 IntegrationFlow 빌더의 주요 목표인 전달되는 메시지 흐름을 만들고 데이터를 변환하 는 기능에 적합함
  - 하지만 마지막 예제에서 확인할 수 있듯이 최상위 수준의 객체를 만들 때 (그리고 내부의 복잡한 메서드 인수에도)는 함수 시퀀싱과 람다 표현식을 사용함
