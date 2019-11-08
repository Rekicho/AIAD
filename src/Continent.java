package src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;

import jade.core.AID;

public class Continent implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    HashMap<String, Country> countries;
    int bonus;

    public Continent(String name) {
        this.name = name;
        countries = new HashMap<String, Country>();
        bonus = 0;
    }

    public void addCountry(Country country) {
        countries.put(country.name, country);
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public String toString() {
        String res = name + "[" + bonus + "]:";

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += "\n   - " + ((Country) pair.getValue());
            it.remove();
        }

        return res;
    }

    public int getBonusIfControlled(AID player)
    {
        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(!((Country) pair.getValue()).getOwner().equals(player))
                return 0;
        }

        return bonus;
    }
}