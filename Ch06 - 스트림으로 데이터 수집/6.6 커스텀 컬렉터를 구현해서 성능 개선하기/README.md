# 6.6 커스텀 컬렉터를 구현해서 성능 개선하기
앞서 작성한 소수, 비소수 분할 코드를 `커스텀 컬렉터`를 이용해서 성능을 개선해 보자
````java
// n이하의 자연수를 소수와 비소수로 분류하는 메서드
public Map<Boolean, List<Integer>> partitionPrimes(int n){
    return IntStream.rangeClosed(2,n).boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
}

//제곱근 이하로 대상 caindiditc의 숫자 범위를 제한한 isPrime 메서드
/**
 * 2에서 대상의 제곱근(candidateRoot)까지의 수 중
 * 대상이 나누어떨어지는 수가 없는 경우(noneMatch)는 true 소수이다,
 * 있는 경우는 false 소수가 아니다를 반환하는 프레디케이트
 */
public boolean isPrime(int candidate){
    int candidateRoot = (int) Math.sqrt((double)candidate);
    return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
}

````
커스텀 컬렉터를 이용하면 성능을 더 개선할 수 있다.
## 6.6.1 소수로만 나누기
우선 소수로 나누어 떨어지는지 확인해서 대상의 범위를 좁힐 수 있다.<br>
제수(나눗셈에서 나누는 수)가 소수가 아니면 소용없으므로 제수를 `현재 숫자 이하에서 발견한 소수`로 제한할 수 있으며, 주어진 숫자가 소수인지 확인하기 위해 지금까지 발견한 소수 리스트에 접근해야 한다.<br>
우리가 살펴본 컬렉터로는 컬렉터 수집 과정에서 부분 결과에 접근할 수 없지만, 커스텀 컬렉터 클래스를 사용해서 해결할 수 있다.

-> 중간 결과 리스트가 있다면 isPrime 메서드로 중간 결과 리스트를 전달하도록 구현한다.
```java
public static boolean isPrime(List<Integer> primes, Integer candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate); // 먼저 candidate의 제곱근을 구한다.
    return primes.stream() 
                 .takeWhile(i -> i <= candidateRoot) // candidate 값보다 작은 소수 목록(primes)을 가져온다.
                 .noneMatch(i -> candidate % i == 0); // candidate 수를 각 소수로 나눈 나머지가 0인지 확인 -> 어떤 소수로도 나누어 떨어지면 candidate는 소수가 X
}
```
(위 코드에 대하여)
- 대상 숫자의 제곱근보다 작은 소수만 사용하도록 코드를 최적화 해야한다.<br>
- 다음 소수가 대상의 루트보다 크면 소수로 나누는 **검사를 중단**하는 방식으로 성능을 개선한다.
  - -> 리스트와 프레디케이트를 인수로 받아 프레디케이트를 만족하는 긴 요소로 이루어진 리스트를 반환하는 `takeWhile`이라는 메소드를 구현한다. (`takeWhile`은 java9버전에서 지원하므로 직접 구현해야한다.)
- 위의 숫자형 스트림에서 `rangeClosed`를 이용해 대상의 제곱근 이하의 값까지로만 범위를 제한한 것처럼,
  - `takeWhile`의 쇼트서킷을 이용해 제곱근 이하의 값까지만 체크한다.

새로운 `isPrime` 메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자. Collector 클래스를 선언하고 Collector 인터페이스에서 요구하는 메서드 다섯 개를 구현해야한다. 
### 1단계 : Collector 클래스 시그니처 정의
```java
public interface Collector<T, A, R>
```
- T는 스트림 요소의 형식
- A는 중간 결과를 누적하는 객체의 형식
- R은 collect 연산의 최종 결과 형식을 의미

소수와 비소수를 참과 거짓으로 나누기 위해 정수로 이루어진 스트림에서 누적자와 최종 결과 형식이 `Map<Boolean, List<Integer>>`인 컬렉터를 구현해야 한다.
```java
public class PrimeNumbersCollector
    implements Collect<Integer, 
        Map<Boolean, List<Integer>>,
        Map<Boolean, List<Integer>>>
```
### 2단계 : 리듀싱 연산 구현
먼저 supplier 메서드로 누적자를 만드는 함수를 반환해야 한다.
```java
public Supplier<Map<Boolean, List<Integer>>> supplier() {
  return () -> new HashMap<Boolean, List<Integer>>() {{
    put(true, new ArrayList<Integer>());
    put(false, new ArrayList<Integer>());
  }};
}
```
위 코드에서 누적자로 만들 맵을 만들면서 true, false 키와 빈 리스트로 초기화 했다.

다음으로 스트림 요소를 어떻게 수집할지 결정하는 `accumulator` 메서드를 만든다.
이제 언제든지 원할 때 수집 과정의 중간 결과, 즉 지금까지 발견한 소수를 포함하는 누적자에 접근할 수 있다.
```java
public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
  return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
    acc.get( isPrime(acc.get(true), candidate) ) //isPrime 결과에 따라 소수/비소수 리스트를 만든다.
      .add(candidate); //candidate를 알맞은 리스트에 추가한다.
  };
}
```
지금까지 발견한 소수 리스트 `acc.get(true)`와 소수 여부를 확인할 수 있는 candidate를 인수로 isPrime 메서드를 호출했다. 그리고 호출 결과를 알맞은 리스트에 추가한다.
### 3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)
이번에는 병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다. 예제에서는 두 번째 맵의 소수 리스트와 비소수 리스트의 모든 수를 첫 번째 맵에 추가하는 연산이면 충분하다.
```java
public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
  return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
    map1.get(true).addAll(map2.get(true));
    map1.get(false).addAll(map2.get(false));
  };
}
```
알고리즘 자체가 순차적이어서 컬렉터를 실제로 병렬로 사용할 순 없으므로 combiner 메서드는 빈 구현으로 남겨두거나 exception을 던지도록 구현하면 된다.
### 4단계 : finisher 메서드와 컬렉터의 characteristics 메서드
accumulator의 형식은 컬렉터 결과 형식과 같으므로 변환 과정은 필요없다. 따라서 항등 함수 identity를 반환하도록 finisher 메서드를 구현한다.
```java
public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
  return Function.identity();
}
```
### 최종 Custom Collector 코드
````java
public class PrimNumberCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    /**
     * 수집과정의 중간결과, 즉 지금까지 발견한 소수를 포함하는 누적자로 사용할 맵을 만들고,
     * 소수와 비소수를 수집하는 두 개의 리스트를 각각 true, false 키와 빈 리스트로 초기화 한다. (p226)
     */
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<>() {{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    /**
     * 스트림의 요소를 수집한다. (p226)
     * 누적 리스트 acc에서 isPrime 메서드 결과에 따라 소수(true), 비소수(false) 리스트에 candidate를 추가 한다.
     */
    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
           acc.get( isPrime( acc.get(true), //지금까지 발견한 소수 리스트(supplier true 키 분할 리스트)를 isPrime으로 전달
               candidate) )
              .add(candidate);  //isPrime 메서드의 결과에 따라 맵에서 알맞은 리스트를 받아 현재 candidate를 추가
        };
    }

    private static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return primes.stream()
                     .takeWhile(i -> i <= candidateRoot)
                     .noneMatch(i -> candidate % i == 0);
    }

    /**
     * 알고리즘이 순차적이기에 실제 병렬로 사용할 수는 없지만, 학습용으로 만든 메서드. 여기선 실제 사용되지 않는다.
     * 각 스레드에서 만든 누적 결과를 병합하여 반환한다. (p228)
     */
    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, 
            Map<Boolean, List<Integer>> map2) -> {
                map1.get(true).addAll(map2.get(true)); //두 번째 맵의 소수 리스트를 첫 번째 맵의 소수 리스트에 추가
                map1.get(false).addAll(map2.get(false)); //두 번째 맵의 비소수 리스트를 첫 번째 맵의 비소수 리스트에 추가
            return map1;
        };
    }

    /**
     * 본래 누적자를 최종결과로 변환하는 역할을 하지만, (p227)
     * 여기선 누적자인 accumulator가 이미 최종 결과와 같으므로 변환 과정이 필요 없어 항등 함수를 반환하도록 구현한다.
     */
    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    /**
     * 발견한 소수의 순서에 의미가 있으므로 CONCURRENT, UNORDERED는 해당되지 않고, IDENTITY_FINISH만 설정한다.
     * 추후 최적화를 위한 힌트 제공 (p229)
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
}
````
### Collector 인터페이스를 별도 구현 없이 사용
코드는 간결하지만, 가독성 및 재사용성은 떨어짐
```java
static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollectorV2(int n) {
    return IntStream.rangeClosed(2, n).boxed()
            .collect(
                    () -> new HashMap<Boolean, List<Integer>>() {{    //발행
                        put(true, new ArrayList<>());
                        put(false, new ArrayList<>());
                    }},
                    (acc, candidate) -> {    //누적
                        acc.get(isPrime(acc.get(true), candidate)).add(candidate);
                    },
                    (map1, map2) -> {   //병합
                        map1.get(true).addAll(map2.get(true));
                        map1.get(false).addAll(map2.get(false));
                    }
            );
}
```