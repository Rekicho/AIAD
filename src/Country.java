import jade.core.AID;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Country implements Serializable {
    private static final long serialVersionUID = 1L;
    
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

    public void setArmies(int armies) {
        this.armies = armies;
    }

    public int getArmies() {
        return this.armies;
    }

    public boolean isEmpty() {
        return armies == 0;
    }

    public int borderCount() {
        return borders.size();
    }

    public HashMap<String, Country> getBorders() {
        return borders;
    }

    @Override
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
            if (it.hasNext())
                res += ", ";
        }

        return res + "])";
    }

    public boolean hasFortifiableBorder() {
        Iterator it = borders.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Country value = (Country) pair.getValue();
            if(value.getOwner().equals(this.owner) && value.getArmies() > 1)
                return true;
        }

        return false;
    }

    public int enemyBordersCount() {
        int res = 0;

        Iterator it = borders.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(!((Country) pair.getValue()).getOwner().equals(this.owner))
                res++;
        }

        return res;
    }

    public ArrayList<Country> getAlliedBorders() {
        ArrayList<Country> res = new ArrayList<Country>();

        Iterator it = borders.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(((Country) pair.getValue()).getOwner().equals(this.owner))
                res.add((Country) pair.getValue());
        }

        return res;
    }

}