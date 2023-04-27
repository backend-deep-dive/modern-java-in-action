import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Practice {

    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950));

        // 연습 1. 2011년에 일어난 모든 트랜잭션을 찾아 오름차순으로 정렬하시오.
        List<Transaction> tr2011 = transactions.stream()
                .filter(transaction -> transaction.getYear() == 2011) // 2011년에 발생한 트랜잭션을 필터링하도록 프레디케이트를 넘겨줌
                .sorted(comparing(Transaction::getValue)) // 트랜잭션 값으로 요소 정렬
                .collect(toList()); // 결과 리스트의 모든 요소를 리스트로 반환
        System.out.println(tr2011);
        System.out.println();

        // 연습 2. 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.
        List<String> cities = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity()) // 트랜잭션과 관련한 각 거래자의 도시 추출
                .distinct()
                .collect(toList());
        System.out.println(cities);
        System.out.println();

        Set<String> cities2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getCity())
                .collect(toSet());
        System.out.println(cities2);
        System.out.println();

        // 연습 3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.
        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader) // 트랜잭션의 모든 거래자 추출
                .filter(trader -> trader.getCity().equals("Cambridge")) // Cambridge의 거래자만 선택
                .distinct() // 중복 제거
                .sorted(comparing(Trader::getName)) // 결과 스트림의 거래자를 이름으로 정렬
                .collect(toList());
        System.out.println(traders);
        System.out.println();

        // 연습 4. 모든 거래자의 이름을 알파벳 순으로 정렬해서 반환하시오.
        String traderStr = transactions.stream()
                .map(transaction -> transaction.getTrader().getName()) // 모든 거래자의 이름을 문자열 스트림으로 추출
                .distinct() // 중복된 이름 제거
                .sorted() // 이름을 알파벳 순으로 정렬
                .reduce("", (n1, n2) -> n1 + n2); // 각각의 이름을 하나로 연결하여 결국 모든 이름을 연결
        System.out.println(traderStr);
        System.out.println();

        String traderStr2 = transactions.stream()
                .map(transaction -> transaction.getTrader().getName())
                .distinct()
                .sorted()
                .collect(joining());
        System.out.println(traderStr2);
        System.out.println();

        // 연습 5. 밀라노에 거래자가 있는가?
        boolean milanBased = transactions.stream()
                .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan")); // anyMatch에 boolean값을 전달해서 밀라노에 거래자가 있는지 확인
        System.out.println(milanBased);
        System.out.println();

        // 연습 6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.
        transactions.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity())) // Cambridge에 거주하는 거래자의 트랜잭션을 선택
                .map(Transaction::getValue) // 이 거래자들의 값 추출
                .forEach(System.out::println); // 각 값을 출력
        System.out.println();

        // 연습 7. 전체 트랜잭션 중 최댓값은 얼마인가?
        int highestValue = transactions.stream()
                .map(Transaction::getValue) // 각 트랜잭션의 값 추출
                .reduce(0, Integer::max); // 결과 스트림의 최댓값 계산
        System.out.println(highestValue);
        System.out.println();

        // 연습 8. 전체 트랜잭션 중 최솟값은 얼마인가?
        Optional<Transaction> smallestTransaction = transactions.stream()
                .reduce((t1, t2) ->
                    t1.getValue() < t2.getValue() ? t1: t2); // 각 트랜잭션 값을 반복 비교해서 가장 작은 트랜잭션 검색
        System.out.println(smallestTransaction);
        System.out.println();

        Optional<Transaction> smallestTransaction2 = transactions.stream()
                .min(comparing(Transaction::getValue));
        System.out.println(smallestTransaction2);
        // Here I cheat a bit by converting the found Transaction (if any) to a String
        // so that I can use a default String if no transactions are found (i.e. the
        // Stream is empty).
        System.out.println(smallestTransaction2.map(String::valueOf).orElse("No transactions found"));

    }

}
