import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class SmartRiskPlayerAgent extends BasicRiskPlayerAgent {
    public String attack() {
        ArrayList<Country> possibleCountriesToAttack = new ArrayList<Country>();

        for (Country country : myCountries()) {
            Iterator it = country.getBorders().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Country actual = (Country) pair.getValue();
                if (!actual.getOwner().equals(getAID()))
                    possibleCountriesToAttack.add(actual);
            }
        }

        if (possibleCountriesToAttack.size() == 0)
            return "[END_ATTACK]\n";

        // Country to attack
        Random rng = new Random();
        Country countryToAttack = possibleCountriesToAttack.get(rng.nextInt(possibleCountriesToAttack.size()));

        // My Country
        ArrayList<Country> myCountriesToAttack = new ArrayList<Country>();
        for (Country country : myCountries())
            if (country.getBorders().containsKey(countryToAttack.getName()) && country.getArmies() >= 2 && country.getArmies() > countryToAttack.getArmies())
                myCountriesToAttack.add(country);

        if (myCountriesToAttack.size() == 0)
            return "[END_ATTACK]\n";

        Country attacker = myCountriesToAttack.get(rng.nextInt(myCountriesToAttack.size()));
        int armiesToAttack = 0;
        if (attacker.getArmies() > 3)
            armiesToAttack = 3;
        else
            armiesToAttack = attacker.getArmies() - 1;

        return "[ATTACK]\n" + getLocalName() + " " + attacker.getName() + " " + countryToAttack.getName() + " "
                + armiesToAttack;
    }

    public String placeArmies() {
        ArrayList<Country> countriesToPlace = null;
        countriesToPlace = riskMap.unoccupiedCountries(); // places one army onto any unoccupied territory

        if (countriesToPlace.size() == 0)
            countriesToPlace = myCountries(); // places one additional army onto any territory this player already
                                              // occupies

        // Player doesn't have any country, he lost the game
        // This point needs to be unreachable
        if (countriesToPlace.size() == 0)
            return "[ERROR]\n";

        Country selectedCountry = null;
        int maxBorders = 0;

        for(Country c : countriesToPlace) {

            int counter = 0;
            Iterator it = c.getBorders().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                if(((Country) pair.getValue()).getOwner() != null && ((Country) pair.getValue()).getOwner().equals(getAID()))
                    counter++;
            }

            if(counter > maxBorders) {
                maxBorders = counter;
                selectedCountry = c;
            }
        }

        // Chooses random country
        if(selectedCountry == null) {
            Random rng = new Random();
            selectedCountry = countriesToPlace.get(rng.nextInt(countriesToPlace.size()));
        }

        return "[PLACEMENT]\n" + getLocalName() + " " + selectedCountry.getName();
    }

    public Country getBestCountryToPlace() {
        Country res = null;
        float max = 1000; // never reached

        for(Country c : myCountries()) {
            int count = c.getArmies();
            if(count < max) {
                max = count;
                res = c;
            }
        }
        return res;
    }

    public String placeNewArmies(int numberOfArmies) {
        String armiesPlacement = "";

        for (int i = 0; i < numberOfArmies; i++) {
            armiesPlacement += getBestCountryToPlace().getName()
                    + (i == numberOfArmies - 1 ? "" : ',');
        }

        return "[GAME_PLACEMENT]\n" + getLocalName() + " " + armiesPlacement;
    }
}