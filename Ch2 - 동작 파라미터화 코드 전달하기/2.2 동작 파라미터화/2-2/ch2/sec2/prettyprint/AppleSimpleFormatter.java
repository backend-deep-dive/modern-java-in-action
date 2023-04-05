package ch2.sec2.prettyprint;

import ch2.sec2.Apple;

public class AppleSimpleFormatter implements AppleFormatter {

    @Override
    public String accept(Apple a) {
        return "SimpleFormatter: an apple of " + a.getWeight() + "g";
    }

}
