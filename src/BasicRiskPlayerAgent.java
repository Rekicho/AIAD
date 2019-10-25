import jade.core.Agent;
import jade.core.AID;

import src.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    // First Stage
    public ArrayList<Country> unoccupiedCountries() {
        HashMap<String, Country> countries = null;
        ArrayList<Country> unoccupiedcountries = null;

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).isEmpty())
                unoccupiedcountries.add((Country) pair.getValue());
            it.remove();
        }

        return unoccupiedcountries;
    }

    // Second Stage
    public ArrayList<Country> thisAgentCountries() {
        HashMap<String, Country> countries = null;
        ArrayList<Country> countriesOwned = null;

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).getOwner().equals(riskGameAgentAID))
                countriesOwned.add((Country) pair.getValue());
            it.remove();
        }

        return countriesOwned;
    }

    public String placeArmies(int stage) {

        ArrayList<Country> countriesToPlace = null;

        switch (stage) {
        case 1: // places one army onto any unoccupied territory
            countriesToPlace = unoccupiedCountries();
            break;
        case 2: // places one additional army onto any territory this player already occupies
            countriesToPlace = thisAgentCountries();
        default:
            break;
        }

        // Player doesn't have any country, he lost the game
        // This point needs to be unreachable
        if (countriesToPlace == null)
            return "[ERROR]";

        // Noob player - Shuffles the list and chooses the first possible country
        Collections.shuffle(countriesToPlace);
        Country selectedCountry = countriesToPlace.get(0);

        // Return message
        // [Placement]
        // AID_ID CountryName
        return "[Placement]\n" + getLocalName() + " " + selectedCountry.getName();
    }

    // TODO
    // - Game Play
    // 1. Getting and placing new armies
    // 2. Attacking
    // 3. Fortifying your position

}