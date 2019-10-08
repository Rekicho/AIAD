import jade.core.Agent;
import java.util.HashMap;
import src.Country;
import src.Continent;

public class RiskGameAgent extends Agent {
    private int numberPlayers;
    private static final HashMap<Integer,Integer> startingArmies;
    private static final HashMap<String,Country> countries;
    private static final HashMap<String,Continent> continents;
    static {
        HashMap<Integer,Integer> startingArmiesMap = new HashMap<Integer,Integer>();

        startingArmiesMap.put(2,40);
        startingArmiesMap.put(3,35);
        startingArmiesMap.put(4,30);
        startingArmiesMap.put(5,25);
        startingArmiesMap.put(6,20);
        startingArmies = startingArmiesMap;


        HashMap<String,Continent> continentsMap = new HashMap<String,Continent>();

        continentsMap.put("North America", new Continent("North America"));
        continentsMap.put("Europe", new Continent("Europe"));
        continentsMap.put("Asia", new Continent("Asia"));
        continentsMap.put("South America", new Continent("South America"));
        continentsMap.put("Africa", new Continent("Africa"));
        continentsMap.put("Australia", new Continent("Australia"));

        continents = continentsMap;


        HashMap<String,Country> mapsMap = new HashMap<String,Country>();

        mapsMap.put("Alaska", new Country("Alaska"));
        mapsMap.put("Alberta", new Country("Alberta"));
        mapsMap.put("Central America", new Country("Central America"));
        mapsMap.put("Eastern United States", new Country("Eastern United States"));
        mapsMap.put("Greenland", new Country("Greenland"));
        mapsMap.put("Northwest Territory", new Country("Northwest Territory"));
        mapsMap.put("Ontario", new Country("Ontario"));
        mapsMap.put("Quebec", new Country("Quebec"));
        mapsMap.put("Western United States", new Country("Western United States"));

        mapsMap.put("Great Britain", new Country("Great Britain"));
        mapsMap.put("Iceland", new Country("Iceland"));
        mapsMap.put("Northern Europe", new Country("Northern Europe"));
        mapsMap.put("Scandinavia", new Country("Scandinavia"));
        mapsMap.put("Southern Europe", new Country("Southern Europe"));
        mapsMap.put("Ukraine", new Country("Ukraine"));
        mapsMap.put("Western Europe", new Country("Western Europe"));

        mapsMap.put("Afghanistan", new Country("Afghanistan"));
        mapsMap.put("China", new Country("China"));
        mapsMap.put("India", new Country("India"));
        mapsMap.put("Irkutsk", new Country("Irkutsk"));
        mapsMap.put("Japan", new Country("Japan"));
        mapsMap.put("Kamchatka", new Country("Kamchatka"));
        mapsMap.put("Middle East", new Country("Middle East"));
        mapsMap.put("Mongolia", new Country("Mongolia"));
        mapsMap.put("Siam", new Country("Siam"));
        mapsMap.put("Siberia", new Country("Siberia"));
        mapsMap.put("Ural", new Country("Ural"));
        mapsMap.put("Yakutsk", new Country("Yakutsk"));

        mapsMap.put("Argentina", new Country("Argentina"));
        mapsMap.put("Brazil", new Country("Brazil"));
        mapsMap.put("Peru", new Country("Peru"));
        mapsMap.put("Venezuela", new Country("Venezuela"));

        mapsMap.put("Congo", new Country("Congo"));
        mapsMap.put("East Africa", new Country("East Africa"));
        mapsMap.put("Egypt", new Country("Egypt"));
        mapsMap.put("Madagascar", new Country("Madagascar"));
        mapsMap.put("North Africa", new Country("North Africa"));
        mapsMap.put("South Africa", new Country("South Africa"));

        mapsMap.put("Eastern Australia", new Country("Eastern Australia"));
        mapsMap.put("Indonesia", new Country("Indonesia"));
        mapsMap.put("New Guinea", new Country("New Guinea"));
        mapsMap.put("Western Australia", new Country("Western Australia"));

        countries = mapsMap;
    }

    public void setup(){
        Object[] args = getArguments();

        if(args == null)
            return;

        int players = Integer.parseInt(args[0].toString());

        System.out.println(startingArmies.get(players));
    }
}