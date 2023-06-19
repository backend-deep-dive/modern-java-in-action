package ch2.sec2.prettyprint;

import ch2.sec2.Apple;
import ch2.sec2.Color;

public class AppleFancyFormatter implements AppleFormatter {

    @Override
    public String accept(Apple a) {
        String redApple = "\uD83C\uDF4E";
        String greenApple = "\uD83C\uDF4F";

        return "FancyFormatter: "
                + (a.getColor().equals(Color.GREEN) ? greenApple : redApple)
                + " (" + a.getWeight() + "g)";
    }
}
