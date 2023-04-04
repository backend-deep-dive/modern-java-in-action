package ch2.sec2.prettyprint;

import ch2.sec2.Apple;

public class AppleFancyFormatter implements AppleFormatter {

    @Override
    public String accept(Apple a) {
        return "FancyFormatter: ##### " + a.toString() + " #####";
    }
}
