package ch2.sec2;

public class Apple {
    private Color color;
    private int weight;

    public int getWeight() {
        return weight;
    }

    public Apple(Color color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public Color getColor() {
        return this.color;
    }

    public String toString() {
        return "Apple: " + this.color.name() + ", " + String.valueOf(this.weight);
    }

}