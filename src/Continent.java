package src;

import java.util.HashMap;

public class Continent {
    String name;
    HashMap<String,Country> countries;

    public Country(String name) {
        this.name = name;
        countries = new HashMap<String,Country>();
    }
}