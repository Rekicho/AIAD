package src;

import java.util.*;

public class Country {
    String name;
    HashSet<Country> neighbours;

    public Country(String name) {
        this.name = name;
        neighbours = new HashSet<Country>();
    }
}