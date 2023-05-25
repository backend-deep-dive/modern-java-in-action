# 7.3 Spliterator 인터페이스

자바 8은 `Spliterator(Splitable Iterator, 분할할 수 있는 반복자)`라는 새로운 인터페이스를 제공한다.

Iterator처럼 소스의 요소 탐색 기능을 제공하지만, 병렬 작업에 특화되어 있다.

자바 8은 컬렉션 프레임워크에 포함된 모든 자료구조에 사용할 수 있는 디폴트 Spliterator 구현을 제공한다.

컬렉션은 `spliterator`라는 메서드를 제공하는 Spliterator 인터페이스를 구현한다.

```java
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action); // Spliterator의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 참을 반환
    
    Spliterator<T> trySplit(); // Spliterator의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 Spliterator를 생성
    
    long estimateSize(); // 탐색해야 할 요소 수 정보 제공
    
    int characteristics(); // Spliterato 자체의 특성 집합을 포함하는 int를 반환
}
```

## 7.3.1 분할 과정

스트림을 여러 스트림으로 분할하는 과정은 재귀적으로 일어난다.

<img width="650" alt="재귀 분할 과정" src="https://user-images.githubusercontent.com/86337233/218249512-63a68c03-1571-4fdd-b61a-9028f75fe869.png">

1. 첫 번째 Spliterator에 trySplit을 호출 시 두 번째 Spliterator 생성
2. 두 개의 Spliterator에 trySplit를 다시 호출 시 네 개의 Spliterator 생성(trySplit의 결과가 null이 될 때까지 이 과정 반복)
3. trySplit이 null 반환 시 더 이상 자료구조 분할할 수 없음
4. Spliterator에 호출한 모든 trySplit의 결과가 null이면 재귀 분할 과정 종료

이 분할 과정은 `characteristics` 메서드로 정의하는 **Spliterator의 특성**에 영향을 받는다.

### Spliterator 특성

<img width="570" alt="Spliterator 특성" src="https://user-images.githubusercontent.com/86337233/218249520-7ac9828e-f4e2-409c-b21d-2ea027047b14.png">

## 7.3.2 커스텀 Spliterator 구현하기

문자열의 단어 수를 계산하는 메서드를 구현해보자.

| 반복형으로 단어 수를 세는 메소드

```java
public int countWordsIteratively(String s) {
    int counter = 0;
    boolean lastSpace = true;
    for (char c : s.toCharArray()) {
        if (Character.isWhitespace(c)) {
            lastSpace = true;
        } else {
            if (lastSpace) {
                counter++;
            } else {
                lastSpace = false;
            }
        }
    }
    return counter;
}
```

단어 사이에 공백이 여러 개일 때도 반복 구현이 제대로 작동된다.

### 함수형으로 단어 수 세는 메소드 재구현하기

```java
class WordCounter {
    private final int counter;
    private final boolean lastSpace;
    
    public WordCounter(int counter, boolean lastSpace) {
        this.counter = counter;
        this.lastSpace = lastSpace;
    }

    /**
     * 반복 알고리즘처럼 accumulate 메소드는 문자열의 문자를 하나씩 탐색한다.
     * 
     * @param c
     * @return
     */
    public WordCounter accumulate(Character c) {
        if (Character.isWhitespace(c)) {
            return lastSpace ? this : new WordCounter(counter, true);
        } else {
            return lastSpace ? new WordCounter(counter + 1, false) : this;  // 문자를 하나씩 탐색하다 공백 문자 만나면 지금까지 탐색한 문자를 단어로 간주하여(공백 문자 제외) 단어 수를 증가시킨다.
        }
    }

    /**
     * 두 WordCounter의 counter 값을 더하고, counter 값만 더할 것이므로 마지막 공백은 신경 쓰지 않고 새로운 WordCounter를 반환한다.
     * 
     * @param wordCounter
     * @return
     */
    public WordCounter combine(WordCounter wordCounter) {
        return new WordCounter(counter + wordCounter.getCounter(), wordCounter.lastSpace);
    }
    
    public int getCounter() {
        return counter;
    }
}
```

새로운 문자 c를 탐색했을 때 WordCounter의 상태 변화

<img width="500" alt="WordCounter" src="https://user-images.githubusercontent.com/86337233/218249523-20c10e2a-c441-43b9-90fc-76b91e4368e5.png">

### WordCounter 병렬로 수행하기

단어 수를 계산하는 연산을 병렬 스트림으로 처리하면 원하는 결과가 나오지 않는다.

원래 문자열을 **임의의 위치에서 둘로 나누다보니** 하나의 단어를 둘로 계산하는 상황이 발생할 수 있기 때문이다.

즉, 순차 스트림을 병렬 스트림으로 바꿀 때 스트림 분할 위치에 따라 잘못된 결과가 나올 수 있다.

```java
public class WordCounterSpliterator implements Spliterator<Character> {

    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    /**
     * 문자열에서 현재 인덱스에 해당하는 문자를 Consumer에 제공한 다음, 인데스를 증가시킨다.
     *
     * @param action 소비한 문자를 전달
     * @return 소비할 문자가 남아있으면 true를 반환 (반복해야 할 문자가 남아있음을 의미)
     */
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        action.accept(string.charAt(currentChar++));    // 현재 문자를 소비
        return currentChar < string.length();
    }

    /**
     * 반복될 자료구조를 분할하는 로직을 포함한다.
     * 분할이 필요한 상황에서는 파싱해야 할 문자열 청크의 중간 위치를 기준으로 분할하도록 지시한다.
     *
     * @return 남은 문자 수가 한계값 이하면 null 반환 -> 분할을 중지하도록 지시
     */
    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;
        if (currentSize < 10) {
            return null;    // 파싱할 문자열이 순차 처리할 수 있을 만큼 충분히 작아졌음을 알림
        }

        // 1. 파싱할 문자열의 중간을 분할 위치로 설정
        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            // 2. 다음 공백이 나올 때까지 분할 위치를 뒤로 이동시킴
            if (Character.isWhitespace(string.charAt(splitPos))) {
                // 3. 처음부터 분할위치까지 문자열을 파싱할 새로운 WordCounterSpliterator를 생성
                Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));

                // 4. 이 WordCounterSpliterator의 시작 위치를 분할 위치로 설정
                currentChar = splitPos;

                // 5. 공백을 찾았고 문자열을 분리했으므로 루프를 종료
                return spliterator;
            }
        }
        return null;
    }

    /**
     * @return 탐색해야 할 요소의 개수
     */
    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    /**
     * @return 특성들
     */
    @Override
    public int characteristics() {
        return ORDERED // 문자열의 문자 등장 순서가 유의미함
                + SIZED // estimatedSize의 메서드의 반환값이 정확함
                + SUBSIZED // trySplit으로 생성된 Spliterator도 정확한 크기를 가짐
                + NONNULL // 문자열에는 null 문자가 존재하지 않음
                + IMMUTABLE // 문자열 자체가 불변 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음
                ;
    }
}
```

### WordCounterSpliterator 활용

문자열을 단어가 끝나는 위치에서만 분할하는 방법으로 위의 문제를 해결할 수 있다.

Spliterator는 첫 번째 탐색 시점, 첫 번째 분할 시점, 또는 첫 번째 예상 크기(estimatedSize) **요청 시점에 요소의 소스를 바인딩할 수 있다.**

이와 같은 동작을 `늦은 바인딩 Spliterator`라고 부른다.