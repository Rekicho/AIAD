package src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

public class Continent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    String name;
    HashMap<String, Country> countries;

    public Continent(String name) {
        this.name = name;
        countries = new HashMap<String, Country>();
    }

    public void addCountry(Country country) {
        countries.put(country.name, country);
    }

    public String toString() {
        String res = name + ":";

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += "\n   - " + ((Country) pair.getValue());
            it.remove();
        }

        return res;
    }
}