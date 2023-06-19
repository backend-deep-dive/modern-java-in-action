package ch2.sec2;

public class AppleGreenColorPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
        boolean isGreen = Color.GREEN.equals(apple.getColor());

        // if (isGreen) {
        // System.out.printf("Color Predicate: this apple's color is %s\n",
        // apple.getColor());
        // }
        return isGreen;
    }

}
