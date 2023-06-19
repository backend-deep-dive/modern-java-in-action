# 9.2 람다로 객체지향 디자인 패턴 리팩터링하기

## 9.2.1 전략(strategy)

전략 패턴은 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법

다양한 기준을 갖는 입력값 검증, 다양한 파싱 방법 사용, 입력 형식 설정 등에 활용 가능

전략 패턴 구성

1. 알고리즘을 나타내는 인터페이스
2. 다양한 알고리즘을 나타내는 인터페이스 구현
3. 전략 객체를 사용하는 클라이언트

| ValidationStrategy.java

```java
public interface ValidationStrategy {
    boolean execute(String s);
}
```

| IsAllLowerCase.java

```java
public class IsAllLowerCase implements ValidationStrategy {
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}
```

| IsNumeric.java

```java
public class IsNumeric implements ValidationStrategy {
    public boolean execute(String s) {
        return s.matches("[0-9]+");
    }
}
```

| Validator.java

```java
public class Validator {
    private final ValidationStrategy validationStrategy;

    public Validator(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    public boolean validate(String s) {
        return validationStrategy.execute(s);
    }
}
```

| Main.java

```java
public class Main {

    public static void main(String[] args) {
        Validator numericValidator = new Validator(new IsNumeric());
        System.out.println(numericValidator.validate("aaaa"));    // false
        Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
        System.out.println(numericValidator.validate("bbbb"));    // true
    }
}
```

### 람다 표현식 사용

| Main.java

```java
public class Main {
    public static void main(String[] args) {
        Validator lowerCaseValidator = new Validator(s -> s.matches("[0-9]+"));
        System.out.println(numericValidator.validate("aaaa"));    // false
        Validator numericValidator = new Validator(s -> s.matches("[a-z]+"));
        System.out.println(numericValidator.validate("bbbb"));    // true
    }
}
```

## 9.2.2 템플릿 메서드(template method)

알고리즘의 개요를 제시한 다음 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 사용

| OnlineBanking.java

```java
abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer customer = Database.getCustomerWithId(id);
        makeHappy(customer);
    }

    abstract void makeHappy(Customer customer);
}
```

### 람다 표현식 사용

| OnlineBanking.java

```java
abstract class OnlineBanking {
    public void processCustomer(int id, Consumer<<Customer>makeHappy) {
        Customer customer = Database.getCustomerWithId(id);
        makeHappy.accept(customer);
    }
}
```

```java
new OnlineBanking().processCustomer(1337, customer -> System.out.println("Hello " + customer.getName()));
```

## 9.2.3 옵저버(observer)

어떤 이벤트가 발생했을 때 한 객체(**주제, subject**)가 다른 객체 리스트(**옵저버, observer**)에 자동으로 알림을 보내야 하는 상황에서 사용

### 옵저버
| Observer.java

```java
interface Observer {
    void notify(String tweet);
}
```

| NewYorkTimes.java

```java
class NewYorkTimes implements Observer {
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("money")) {
            System.out.println("Breaking news in NewYork!" + tweet);
        }
    }
}
```

| Guardian.java

```java
class Guardian implements Observer {
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("queen")) {
            System.out.println("Yet more news from London... " + tweet);
        }
    }
}
```

| LeMonde.java

```java
class LeMonde implements Observer {
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("wine")) {
            System.out.println("Today cheese, wine and news! " + tweet);
        }
    }
}
```

### 주제

| Subject.java

```java
interface Subject {
    void register(Observer observer);
    void notifyObservers(String tweet);
}
```

| Feed.java

```java
class Feed implements Subject {
    private static final List<Observer> OBSERVERS = new ArrayList<>();
    
    public void register(Observer observer) {
        OBSERVERS.add(observer);
    }
    
    public void notifyObservers(String tweet) {
        OBSERVERS.forEach(observer -> observer.notify(tweet));
    }
}
```

| Main.java

```java
public class Main {
    public static void main(String[] args) {
        Feed feed = new Feed();
        feed.register(new NewYorkTimes());
        feed.register(new Guardian());
        feed.register(new LeMonde());
        feed.notifyObservers("The queen said her favourite book is Modern Java in Action!");
    }
}
```

### 람다 표현식 사용하기

| Main.java

```java
public class Main {
    public static void main(String[] args) {
        Feed feed = new Feed();
        feed.register(twwet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NewYork!" + tweet);
            }
        });
        feed.register(tweet -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more news from London... " + tweet);
            }
        });
    }
}
```

> 옵저버가 상태를 가지며, 여러 메소드를 정의하는 등 복잡하다면 람다 표현식보다 기존의 클래스 구현방식이 바람직

## 9.2.4 의무 체인(chain of responsibility)

작업 처리 객체의 체인(동작 체인 등)을 만들 때 사용

한 객체가 어떤 작업을 처리한 다음 다른 객체로 결과를 전달하고, 다른 객체도 해야 할 작업을 처리한 다음 또 다른 객체로 전달

| Processing.java

```java
public abstract class Processing<T> {
    protected Processing<T> successor;
    
    public void setSuccessor(Processing<T> successor) {
        this.successor = successor;
    }
    
    public T handle(T input) {
        T work = handleWork(input);
        if (successor != null) {
            return successor.handle(work);
        }
        return work;
    }
    
    abstract protected T handleWork(T input);
}
```

| HeaderTextProcessing.java

```java
public class HeaderTextProcessing extends Processing<String> {
    public String handleWork(String text) {
        return "From Raoul, Mario and Alan: " + text;
    }
}
```

| SpellCheckerProcessing.java

```java
public class SpellCheckerProcessing extends Processing<String> {
    public String handleWork(String text) {
        return text.replaceAll("labda", "lambda");
    }
}
```

| Main.java

```java
public class Main {
    public static void main(String[] args) {
        Processing<String> processing1 = new HeaderTextProcessing();
        Processing<String> processing2 = new SpellCheckerProcessing();
        processing1.setSuccessor(processing2);
        System.out.println(processing1.handle("Aren't labdas really sexy?"));   // From Raoul, Mario and Alan: Aren't lambdas really sexy?
    }
}
```

### 람다 표현식 사용

```java
UnaryOperator<String> headerProcessing = text -> "From Raoul, Mario and Alan: " + text; // 첫 번째 작업 처리 객체
UnaryOperator<String> spellCheckerProcessing = text -> text.replaceAll("labda", "lambda");  // 두 번째 작업 처리 객체
Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);   // 동작 체인으로 두 함수 조합
String result = pipeline.apply("Aren't labdas really sexy?!!");
```

## 9.2.5 팩토리(factory)

인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용

| ProductFactory.java

```java
public class ProductFactory {
    public static Product createProduct(String name) {
        swtich(name) {
            case "loan":
                return new Loan();
            case "stock":
                return new Stock();
            case "bond":
                return new Bond();
            default:
                throw new RuntimeException("No such product " + name);
        }
    }
}
```

```java
Product product = ProductFactory.createProduct("loan");
```

### 람다 표현식 사용

```java
Supplier<Product> loanSupplier = Loan::new; // 생성자 참조
Loan loan = loanSupplier.get();
```

```java
final static Map<String, Supplier<Product>> map = new HashMap<>();  // 상품명을 생성자로 연결하는 Map 생성
static {
    map.put("loan", Loan::new);
    map.put("stock", Stock::new);
    map.put("bond", Bond::new);
}
```

```java
public static Product createProduct(String name) {  // Map 을 이용해 팩토리 디자인 패턴처럼 다양한 상품 인스턴스화
    Supplier<Product> product = map.get(name);
    if (product != null) {
        return product.get();
    }
    throw new IllegalArgumentException("No such product " + name)
}
```

생성자로 여러 인수가 필요할 때 TriFunction 등의 새로운 함수형 인터페이스를 만들어야 한다.

```java
// 세 인수가 필요할 때 아래와 같이 구현 가능
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}

Map<String, TriFunction<Integer, Integer, String, Product>> map = new HashMap<>();
```