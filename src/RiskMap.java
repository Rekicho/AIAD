import src.Country;
import src.Continent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

import jade.core.AID;

public class RiskMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private final HashMap<String, Country> countries;
    private final HashMap<String, Continent> continents;

    public RiskMap() {
        continents = new HashMap<String, Continent>();
        countries = new HashMap<String, Country>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/countries.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");

                if (!continents.containsKey(parts[0]))
                    continents.put(parts[0], new Continent(parts[0]));

                countries.put(parts[1], new Country(parts[1]));
                continents.get(parts[0]).addCountry(countries.get(parts[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load countries");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/borders.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");
                countries.get(parts[0]).addBorder(countries.get(parts[1]));
                countries.get(parts[1]).addBorder(countries.get(parts[0]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load borders");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/bonus.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");
                continents.get(parts[0]).setBonus(Integer.parseInt(parts[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load bonus");
        }
    }

    public HashMap<String, Continent> getContinents() {
        return continents;
    }

    public HashMap<String, Country> getCountries() {
        return countries;
    }

    public String toString() {
        String res = "";

        Iterator it = continents.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += ((Continent) pair.getValue()) + "\n";
            it.remove();
        }

        return res;
    }

    public ArrayList<Country> unoccupiedCountries() {
        ArrayList<Country> unoccupiedCountries = new ArrayList();

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).isEmpty())
                unoccupiedCountries.add((Country) pair.getValue());
        }

        return unoccupiedCountries;
    }

    public boolean placeIfValid(AID player, String country) {
        if (unoccupiedCountries().size() != 0) {
            if (countries.get(country).getOwner() != null)
                return false;

            countries.get(country).setOwner(player);
            countries.get(country).setArmies(1);

            return true;
        }

        if (countries.get(country).getOwner().equals(player)) {
            countries.get(country).setArmies(countries.get(country).getArmies() + 1);
            return true;
        }

        return false;
    }

    public boolean checkGameOver() {

        Iterator it = countries.entrySet().iterator();

        AID player = null;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            AID owner = ((Country) pair.getValue()).getOwner();

            if (player == null)
                player = owner;
            if (!player.equals(owner))
                return false;
        }

        return true;
    }
}