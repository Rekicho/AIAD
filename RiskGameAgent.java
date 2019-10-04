import jade.core.Agent;
import java.util.HashMap;

public class RiskGameAgent extends Agent {
    private int numberPlayers;
    private static final HashMap<Integer,Integer> startingArmies;
    static {
        HashMap<Integer, Integer> hashmap = new HashMap<Integer,Integer>();
        hashmap.put(2,40);
        hashmap.put(3,35);
        hashmap.put(4,30);
        hashmap.put(5,25);
        hashmap.put(6,20);
        startingArmies = hashmap;
    }

    public void setup(){
        
    }
}