package ch2.sec2;

import java.util.ArrayList;
import java.util.List;

import ch2.sec2.prettyprint.AppleFancyFormatter;
import ch2.sec2.prettyprint.AppleFormatter;
import ch2.sec2.prettyprint.AppleSimpleFormatter;

class Main {

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (Color.GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }

        return result;
    }

    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : result) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }

        return result;
    }

    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        List<Apple> inventory = new ArrayList<>();
        inventory.add(new Apple(Color.RED, 0));
        inventory.add(new Apple(Color.GREEN, 100));
        inventory.add(new Apple(Color.RED, 200));
        inventory.add(new Apple(Color.RED, 300));
        inventory.add(new Apple(Color.GREEN, 400));

        // testing formatters
        AppleFormatter simpleFormatter = new AppleSimpleFormatter();
        AppleFormatter fancyFormatter = new AppleFancyFormatter();

        for (Apple apple : inventory) {
            System.out.println(simpleFormatter.accept(apple));
        }
        System.out.println("---");

        for (Apple apple : inventory) {
            System.out.println(fancyFormatter.accept(apple));
        }
        System.out.println("---");

        // filter green apples
        System.out.println("filter green");
        List<Apple> filteredInventory = filterGreenApples(inventory);
        for (Apple apple : filteredInventory) {
            System.out.println(fancyFormatter.accept(apple));
        }
        System.out.println("---");

        // filter green apples with predicate
        System.out.println("filter green with predicate");
        filteredInventory = filterApples(inventory, new AppleGreenColorPredicate());
        for (Apple apple : filteredInventory) {
            System.out.println(fancyFormatter.accept(apple));
        }
        System.out.println("---");

        // filter heavy apples with predicate
        System.out.println("filter heavy apples");
        filteredInventory = filterApples(inventory, new AppleHeavyWeightPredicate());
        for (Apple apple : filteredInventory) {
            System.out.println(fancyFormatter.accept(apple));
        }
        System.out.println("---");
    }

}
