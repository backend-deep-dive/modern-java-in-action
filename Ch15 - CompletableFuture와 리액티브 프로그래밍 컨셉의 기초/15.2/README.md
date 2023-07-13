# 15.2 동기 API 와 비동기 API

```java
int y = f(x);
int z = g(x);
System.out.println(y + z);
```

f, g 가 실행에 매우 오래 걸린다면, 어떻게 병렬성에서 이득을 취할 수 있을까?

```java
public static void main(String[] args) throws InterruptedExcepution {
 int x = 1337;
 Result result = new Result();

 Thread t1 = new Thread(() -> {result.left = f(x);});
 
 Thread t2 = new Thread(() -> {result.right = g(x);});
...
}
```

코드가 너무 복잡해진다. (책에서 지적)

Runnable 대신 Future 를 사용하면 더 단순하다.

```java
int x = 1337;

ExecutorService executorService = Executors.newFixedThreadPool(2);
Future<Integer> y = executorService.submit(() -> f(x));
executorService.submit(() -> g(x));

executorService.shutdown();
```

여전히 이 코드도 submit, shutdown 과 같은 불필요한 코드가 많다.

## 15.2.1 Future 형식

이러한 문제를 비동기 API 를 통해 해결할 수 있다!

```java
Future<Integer> f(int x);
Future<Integer> g(int x);
```

```java
Future<Integer> y = f(x);
Future<Integer> z = g(x);
System.out.println(y.get() + z.get());
```

### 단골 질문

z 에도 굳이 Future 를 써야 할까? 어차피 y 만 비동기로 구해도 되는 게 아닐까?

보기 깔끔 (가독성), 추후 확장을 위해 통일하는 것이 바람직하다!

## 15.2 리액티브 형식

```
void f(int x, IntConsumer dealWithResult);
```

자바스크립트에서 정말 많이 보던 형식이다!

``` java
function A(int x, function B ) {

 jobA(x) ...

 if job(A) finishes successfully:
  B()
}
```

```
전쟁(int 3년, () => 결혼);
```

```java
Result result = new Result();

f(x, (int y) -> {
 result.left = y;
})

f(x, (int z) -> {
 result.right = z;
})
```

## 15.2.3 잠자기는 해로운 것

블로킹 동작은 해로운 것으로 간주

A

```java
work1();
Thread.sleep(1000);
work2();
```

B

```java
ScheduledExecutorService.schedule(
 work2, 10, TimeUnit.SECONDS); 
)
```

> 이것 역시 자바스크립트에서 정말 많이 보던 setTimeout 함수 비슷하다다.

A 는 work1 10 초 뒤에 2 를 실행한다.

B 는 work1 진행하고 10 초 뒤에 work2 가 실행되도록 스케쥴링한다.

A 와 B 의 차이점은?

B 는 그 사이 10 초동안 다른 작업을 허용한다.

### 질문

어차피 운영체제 상에서는 Sleep 하면 다른 작업을 하지 않나?

책 답변: 스레드의 제한이 없고 저렴하면 코드 **A 와 B 는 사실상 같다!** 하지만 대개 그렇지 않으므로 B 의 형식을 따르자.

## 15.2.4 현실성 확인

과연 비동기가 항상 좋은 것일까? 꼭 그렇지는 않다.
항상 유익을 얻을 수 있는 상황을 따져보고 결정하자!

## 15.2.5 비동기 API 예외 처리는?

다른 스레드에서 호출되기 때문에 이때 발생하는 에러는 호출자의 실행 범위와는 상관이 없는 상황이 된다.

예상치 못한 일이 벌어지면 다른 동작을 실행시켜야 한다. 어떻게?

CompletableFuture 에서는 get 메서드의 예외를 처리할 수 있는 기능을 제공한다. 예외에서 회복할 수 있도록 exceptionally 라는 메서드도 제공한다.

> 이것 역시 자바스크립트에서 정말 자주 보던 (resolve, reject) => {} 패턴이다!

```java
void f(int x, Consumer<Integer> dealWithResult,
Consumer<Throwable> dealWithException)
```

예외가 발생하면 dealWithException(e); 로 처리하면 된다!

점점 함수 인자값이 늘어나는 것이 보인다… 인자값이 더 늘어난다면 확장성을 위해 이러한 콜백 객체들을 하나의 객체로 감싸는 것이 좋다.

```java
class Subscriber<Integer> {
 void onComplete()
 void onError()
 void onNext()
}
```
