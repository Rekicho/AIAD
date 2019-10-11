package src;

import java.util.HashMap;

public class Country {
    String name;
    HashMap<String, Country> borders;

    public Country(String name) {
        this.name = name;
        borders = new HashMap<String, Country>();
    }

    public String getName() {
        return name;
    }

    public void addBorder(Country border) {
        borders.put(border.getName(), border);
    }

    public int borderCount() {
        return borders.size();
    }
}