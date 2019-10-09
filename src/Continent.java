package src;

import java.util.HashMap;

public class Continent {
    String name;
    HashMap<String,Country> countries;

    public Continent(String name) {
        this.name = name;
        countries = new HashMap<String,Country>();
    }

    public void addCountry(Country country) {
        countries.put(country.name,country);
    }
}