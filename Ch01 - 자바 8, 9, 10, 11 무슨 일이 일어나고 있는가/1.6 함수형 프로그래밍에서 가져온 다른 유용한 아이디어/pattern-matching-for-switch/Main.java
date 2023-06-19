public class Main {
    public static void main(String[] args) {

        System.out.println("hello world!");

        Double d = 1234.1234;
        Long l = 12341234L;

        System.out.println(formatter(d));
        System.out.println(formatter(l));

        System.out.println(formatterPatternSwitch(d));
        System.out.println(formatterPatternSwitch(l));

    }

    static String formatter(Object obj) {
        String formatted = "unknown";
        if (obj instanceof Integer i) {
            formatted = String.format("int %d", i);
        } else if (obj instanceof Long l) {
            formatted = String.format("long %d", l);
        } else if (obj instanceof Double d) {
            formatted = String.format("double %f", d);
        } else if (obj instanceof String s) {
            formatted = String.format("String %s", s);
        }
        return formatted;
    }

    static String formatterPatternSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l -> String.format("long %d", l);
            case Double d -> String.format("double %f", d);
            case String s -> String.format("String %s", s);
            default -> obj.toString();
        };
    }

}