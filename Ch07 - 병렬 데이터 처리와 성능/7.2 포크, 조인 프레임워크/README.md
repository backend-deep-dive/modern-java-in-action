# 포크 조인 프레임워크

병렬화 할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음, 서브 태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되었음. 


기본적인 구조: 
작업자(Worker)들을 작업장(Thread pool)에 등록한다.


이 프레임워크에서는 서브태스크를 스레드 풀 ForkJoinPool의 작업자 스레드에 분산 할당하는 ExecutorService 인터페이스를 구현한다.

### 책내용 x 질문: ExecuterService 인터페이스는 무엇인가? 

https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html

ForkJoinPool 은 AbstractExecutorService를 상속하고, AES는 ES를 상속한다.
ES는 Executor를 상속한다. 

> 무언가 쓰레드 단위의 작업을 실행해주는 역할을 하는 듯 하다.

ForkJoinPool은 invoke를 통해 ForkJoinTask를 실행할 수 있다.

RecursiveTask는 ForkJoinTask를 상속한다.

즉, ForkJoinPool은 RecursiveTask를 invoke 할 수 있다.

```java
return FORK_JOIN_POOL.invoke(task);
```

이런 형식이다.


## 7.2.1 RecursiveTask 활용

```
protected abstract R compute();
```

```
if task is not divisible {
	순차적 태스크 계산
} else {
	divide into two
	rA = compute(A)
	rB = compute(B)
	merge(rA, rB)
}
```

```java
@Override
protected Long compute() {
	int length = end - start;
	if (length <= THRESHOLD) {
		return computeSequentially();
	}
	ForkJoinSumCalculator leftTask = 
	new ForkjoinSumCalculator(numbers, start, start + length / 2);

	// 중요!!!
	leftTask.fork(); // 병렬 실행

	ForkJoinSumCalculator rightTask =
	new ForkJoinSumCalculator(numbers, start + length / 2, end);

	Long rightResult = rightTask.compute();
	Long leftResult = leftTask.join();
	return leftResult + rightResult;
}


```

![](Pasted%20image%2020230525164724.png)

```java
public static long forkJoinSum(long n){
	long[] numbers = LongStream.rangeClosed(1, n).toArray();
	ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
	return new ForkJoinPool().invoke(task);
}
```

---

질문: 그냥 fork fork join join 해도 되나요?

답: 밑에서 책에서 설명해줍니다.

---

질문: ForkJoinPool().invoke(task) 하지 말고 그냥 task.compute() 하면 안될까요?

되긴 하네요? 차이가 뭘지...

답:
https://stackoverflow.com/questions/34132326/forkjoinpool-invoke-and-forkjointask-invoke-or-compute

풀에 등록하고 쓰면 이미 만들어져 있는 풀을 재사용할 수 있지만, compute로 하면 프레임워크가 알아서 새로운 풀을 만듬. pool.invoke(task) 가 정석.

---

일반적으로 ForkJoinPool은 애플리케이션에서 단 한 번만 인스턴스화 해서 정적 필드에 싱글턴으로 저장한다.

---



```powershell
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Iterative Sum done in: 7 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Sequential Sum done in: 205 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Parallel forkJoinSum done in: 405 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Range forkJoinSum done in: 6 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Parallel range forkJoinSum done in: 1 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
ForkJoin sum done in: 20 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
ForkJoin sum done in: 22 msecs
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
Result: 1
ForkJoin sum done in: 16 msecs
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
Result: 50000005000000
SideEffect sum done in: 56 msecs
Result: 10305945166823
Result: 6607953176395
Result: 7791133171914
Result: 8657678393018
Result: 13548949593583
Result: 8075795039826
Result: 13872708668531
Result: 16710882018292
Result: 11759395261769
Result: 13221808214408
SideEffect parallel sum done in: 93 msecs

Process finished with exit code 0

```

ForkJoin sum의 성능은 책에서는 41, 여기서는 20msec 인데, 이는 stream을 long[]으로 변환했기 때문에 발생하는 오버헤드일 뿐이지, ForkJoin의 성능이 느린 것은 아니다. 

> 실제로 ForkJoin을 사용하지 않고 무조건 1을 반환하도록 코드를 수정한 결과, 16ms의 결과가 나온다. ForkJoin sum은 4msec + alpha 정도만 사용하는 것이다.

## 7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법

### join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때가지 호출자를 블록시킨다.

### RecursiveTask 내에서는 ForkJoinPool의 invoke를 사용하면 안된다. 대신 compute나 fork를 호출할 수 있다. 계산을 시작할 때만 invoke를 사용한다.

### 서브태스크에 fork를 불러서 ForkJoinPool의 일정을 조절할 수 있다.

왼쪽 작업과 오른쪽 작업에 모두 fork를 호출하는 것이 자연스러울 것 같지만, 한쪽 작업에는 compute를 하는 것이 더 효율적이다. (질문에 대한 답변!)

같은 스레드를 재사용할 수 있다.

책 내용 x 질문: 이때, fork를 먼저 부르고 사용해야 하는가?

### 병렬 계산은 디버깅하기 어렵다.

fork라 불리는 다른 스레드에서 compute를 호출하므로 스택 트레이스가 의미 없다.

### 순차 처리보다 무조건 빠를 거라는 생각을 버려야 한다.

- 태스크를 여러 독립 서브태스크로 분할할 수 있어야 한다.
- 서브태스크의 실행시간이 새로운 태스크를 포킹하는 시간보다 길어야 한다.
	- 예를 들면 I/O와 계산을 병렬로 실행
- 준비 과정을 거쳐야 할 수 있다. 따라서 여러 번 프로그램을 실행해 봐야 한다.
	- branch prediction
- 컴파일러 최적화는 순차 버전에 집중될 수도 있다.

---

문제: 서브 태스크를 어디까지 분할할 것인지, 결정은 어떻게 내리는가???

다음 절에서 계속

---

## 7.2.3 작업 훔치기

**Work Stealing**

ForkJoinCalculator에서 숫자가 만 개 이하면 서브태스크 분할을 중단했다.

1000만개의 숫자라면 1000+개의 서브태스크를 포크할 것이다. 

**하지만 코어는 4개밖에 안되는데? 천 개로 나눠봤자...?**

하지만 적절한 크기로 많은 태스크를 포킹하는 것이 더 낫다.

질문: 코어가 4개인 CPU에서 쓰레드를 4개 이상 나누는 것이 의미가 있는가? 1000개씩 쓰레드를 만드는 게 어떤 의미가 있을까?

답: 잘게 자르는 것이 의미가 있다. 현실에서는 각각의 태스크가 다른 시간에 종료될 수 있다. 노는 코어는 큐의 헤드에서 다른 작업을 가져와서 처리한다. 모든 작업을 끝낸 스레드는 다른 스레드의 작업 큐의 꼬리에서 작업을 훔쳐와서 계속 일한다. 때문에 각각의 스레드 간의 작업 부하를 비슷한 수준으로 유지할 수 있다.

![](Pasted%20image%2020230525170448.png)


이번 절에서는 숫자 배열을 여러 태스크로 분할하는 로직을 직접 개발했다.

다음 절에서는 자동으로 분할해주는 Spliterator를 사용해 더 편하게 병렬 작업을 수행한다.

