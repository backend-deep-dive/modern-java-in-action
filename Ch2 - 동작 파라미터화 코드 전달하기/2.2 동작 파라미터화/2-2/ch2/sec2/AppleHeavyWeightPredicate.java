package ch2.sec2;

public class AppleHeavyWeightPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
        boolean isHeavy = apple.getWeight() > 200;
        // if (isHeavy) {
        // System.out.printf("Weight Predicate: this apple of weight %d is heavy
        // enough\n", apple.getWeight());
        // }
        return isHeavy;
    }
}
