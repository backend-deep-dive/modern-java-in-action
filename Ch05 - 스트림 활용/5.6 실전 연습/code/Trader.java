
public class Trader {

    private String name;
    private String city;

    public Trader(String n, String c) {
        name = n;
        city = c;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

  @Override
  public String toString() {
    return String.format("Trader: "+this.name + " in " + this.city);
  }

}