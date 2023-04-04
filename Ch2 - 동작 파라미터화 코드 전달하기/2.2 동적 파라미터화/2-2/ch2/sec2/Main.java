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
        inventory.add(new Apple(Color.GREEN, 1));
        inventory.add(new Apple(Color.RED, 2));
        inventory.add(new Apple(Color.RED, 3));
        inventory.add(new Apple(Color.GREEN, 4));

        AppleFormatter fancyFormatter = new AppleFancyFormatter();
        AppleFormatter simpleFormatter = new AppleSimpleFormatter();

        List<Apple> filteredInventory = filterGreenApples(inventory);
        for (Apple apple : filteredInventory) {
            System.out.println(apple);
            System.out.println(fancyFormatter.accept(apple));
            System.out.println(simpleFormatter.accept(apple));
        }
        System.out.println("---");

        filteredInventory = filterApples(inventory, new AppleGreenColorPredicate());
        for (Apple apple : filteredInventory) {
            System.out.println(apple);
        }
        System.out.println("---");

        filteredInventory = filterApples(inventory, new AppleHeavyWeightPredicate());
        for (Apple apple : filteredInventory) {
            System.out.println(apple);
        }
        System.out.println("---");
    }

}
