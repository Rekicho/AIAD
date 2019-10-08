package src;

import java.util.HashMap;

public class Country {
    String name;
    HashMap<String,Country> neighbours;

    public Country(String name) {
        this.name = name;
        neighbours = new HashMap<String,Country>();
    }
}