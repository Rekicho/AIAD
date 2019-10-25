package src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jade.core.AID;

public class Country {
    String name;
    HashMap<String, Country> borders;
    AID owner;
    int armies;

    public Country(String name) {
        this.name = name;
        borders = new HashMap<String, Country>();

        owner = null;
        armies = 0;
    }

    public String getName() {
        return name;
    }

    public void addBorder(Country border) {
        borders.put(border.getName(), border);
    }

    public void setOwner(AID newOwner) {
        this.owner = newOwner;
    }

    public AID getOwner() {
        return this.owner;
    }

    public boolean isEmpty() {
        return armies == 0;
    }

    public int borderCount() {
        return borders.size();
    }

    public String toString() {
        String res = name + " (";

        if (owner == null)
            res += "___";
        else
            res += owner.getName();

        res += ", " + armies + ", [";

        Iterator it = borders.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += ((Country) pair.getValue()).getName();
            it.remove();

            if (it.hasNext())
                res += ", ";
        }

        return res + "])";
    }

}