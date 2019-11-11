import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import src.Country;



public class BasicRiskPlayerAgent extends Agent {
    private AID riskGameAgentAID;
    protected RiskMap riskMap;

    public void setup() {
        Object[] args = getArguments();

        riskGameAgentAID = new AID(args[0].toString(), Boolean.parseBoolean(args[1].toString()));

        addBehaviour(new BasicRiskPlayerBehaviour(riskGameAgentAID));
    }

    class BasicRiskPlayerBehaviour extends RiskPlayerBehaviour {
        public BasicRiskPlayerBehaviour(AID riskGameAgentAID) {
            super(riskGameAgentAID);
        }
    }

    public ArrayList<Country> myCountries() {
        ArrayList<Country> countriesOwned = new ArrayList<Country>();

        Iterator it = riskMap.getCountries().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).getOwner().equals(getAID()))
                countriesOwned.add((Country) pair.getValue());
        }

        return countriesOwned;
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

        // Noob player - Chooses random country
        Random rng = new Random();
        Country selectedCountry = countriesToPlace.get(rng.nextInt(countriesToPlace.size()));

        return "[PLACEMENT]\n" + getLocalName() + " " + selectedCountry.getName();
    }

    // TODO
    // - Game Play
    // 1. Getting and placing new armies
    // 2. Attacking
    // 3. Fortifying your position
    public String placeNewArmies(int numberOfArmies) {
        String armiesPlacement = "";
        Random rng = new Random();
        ArrayList<Country> myCountries = myCountries();

        for (int i = 0; i < numberOfArmies; i++) {
            // Choose randomnly
            armiesPlacement += myCountries.get(rng.nextInt(myCountries.size())).getName() + (i == numberOfArmies - 1 ? "" : ',');
        }

        return "[GAME_PLACEMENT]\n" + getLocalName() + " " + armiesPlacement;
    }

    public String attack() {
        ArrayList<Country> possibleCountriesToAttack = new ArrayList<Country>();

        for(Country country : myCountries()) {
            Iterator it = country.getBorders().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Country actual = (Country) pair.getValue();
                if (!actual.getOwner().equals(getAID()) && actual.getArmies() >= 2)
                    possibleCountriesToAttack.add(actual);
            }
        }

        // Country to attack
        Random rng = new Random();
        Country countryToAttack = possibleCountriesToAttack.get(rng.nextInt(possibleCountriesToAttack.size()));

        // My Country
        ArrayList<Country> myCountriesToAttack = new ArrayList<Country>();
        for(Country country : myCountries())
            if(country.getBorders().containsKey(countryToAttack.getName()))
                myCountriesToAttack.add(country);

        Country attacker = myCountriesToAttack.get(rng.nextInt(myCountriesToAttack.size()));
        int armiesToAttack = 0;
        if(attacker.getArmies() > 3) armiesToAttack = 3;
        else armiesToAttack = attacker.getArmies() - 1;

        return "[ATTACK]\n" + getLocalName() + " " + attacker.getName() + " " + countryToAttack.getName() + " " +  armiesToAttack;
    }
}