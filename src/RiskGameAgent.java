import jade.core.Agent;
import java.util.HashMap;
import java.util.HashSet;
import src.Country;

public class RiskGameAgent extends Agent {
    private int numberPlayers;
    private static final HashMap<Integer,Integer> startingArmies;
    private static final HashSet<Country> countries;
    static {
        HashMap<Integer, Integer> hashmap = new HashMap<Integer,Integer>();
        hashmap.put(2,40);
        hashmap.put(3,35);
        hashmap.put(4,30);
        hashmap.put(5,25);
        hashmap.put(6,20);
        startingArmies = hashmap;

        HashSet<Country> hashset = new HashSet<Country>();
        hashset.add(new Country("Alaska"));
        hashset.add(new Country("Alberta"));
        hashset.add(new Country("Central America"));
        hashset.add(new Country("Eastern United States"));
        hashset.add(new Country("Greenland"));
        hashset.add(new Country("Northwest Territory"));
        hashset.add(new Country("Ontario"));
        hashset.add(new Country("Quebec"));
        hashset.add(new Country("Western United States"));

        countries = hashset;
    }

    public void setup(){
        Object[] args = getArguments();

        if(args == null)
            return;

        int players = Integer.parseInt(args[0].toString());

        System.out.println(startingArmies.get(players));
    }
}