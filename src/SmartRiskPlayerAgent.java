import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import src.Country;

public class SmartRiskPlayerAgent extends BasicRiskPlayerAgent {
    //public String placeNewArmies(int numberOfArmies) {
    //     String armiesPlacement = "";
    //     Random rng = new Random();
    //     ArrayList<Country> myCountries = myCountries();

    //     Country bestPlacement = null;
    //     int maxBorders = 0;

    //     for(Country country : myCountries())
    //         if(country.enemyBordersCount() > maxBorders)
    //             bestPlacement = country;


    //     for (int i = 0; i < numberOfArmies; i++) {
    //         // Choose randomnly
    //         if(bestPlacement != null)
    //             armiesPlacement += bestPlacement.getName() + (i == numberOfArmies - 1 ? "" : ',');

    //         else armiesPlacement += myCountries.get(rng.nextInt(myCountries.size())).getName()
    //                     + (i == numberOfArmies - 1 ? "" : ',');
    //     }

    //     return "[GAME_PLACEMENT]\n" + getLocalName() + " " + armiesPlacement;
    // }

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
}