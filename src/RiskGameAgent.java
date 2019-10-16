import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import src.Country;
import src.Continent;

public class RiskGameAgent extends Agent {
    private int numberPlayers;
    private int joinedPlayers = 0;
    private static final HashMap<Integer, Integer> startingArmies;
    private static final HashMap<String, Country> countries;
    private static final HashMap<String, Continent> continents;

    static {
        HashMap<Integer, Integer> startingArmiesMap = new HashMap<Integer, Integer>();

        startingArmiesMap.put(2, 40);
        startingArmiesMap.put(3, 35);
        startingArmiesMap.put(4, 30);
        startingArmiesMap.put(5, 25);
        startingArmiesMap.put(6, 20);
        startingArmies = startingArmiesMap;

        HashMap<String, Continent> continentsMap = new HashMap<String, Continent>();
        HashMap<String, Country> countriesMap = new HashMap<String, Country>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/countries.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");

                if (!continentsMap.containsKey(parts[0]))
                    continentsMap.put(parts[0], new Continent(parts[0]));

                countriesMap.put(parts[1], new Country(parts[1]));
                continentsMap.get(parts[0]).addCountry(countriesMap.get(parts[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load countries");
        }

        continents = continentsMap;
        countries = countriesMap;

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
    }

    public void setup() {
        Object[] args = getArguments();

        if (args == null || continents.isEmpty() || countries.isEmpty()) {
            doDelete();
            return;
        }

        numberPlayers = Integer.parseInt(args[0].toString());

        addBehaviour(new WaitForPlayers());
    }

    class WaitForPlayers extends Behaviour {
        
        public void action() {
            System.out.println("waiting for " + (((RiskGameAgent) myAgent).numberPlayers - ((RiskGameAgent) myAgent).joinedPlayers) + " more players.");
            ACLMessage msg = receive();

            if(msg != null) {
                joinedPlayers++;
                System.out.println(msg);
                // ACLMessage reply = msg.createReply();
                // reply.setPerformative(ACLMessage.AGREE);
                // reply.setContent("Added you to the player list.");
                // send(reply);
            } else {
                block();
            }               
        }

        public boolean done() {
            return ((RiskGameAgent) myAgent).joinedPlayers == ((RiskGameAgent) myAgent).numberPlayers;
        }

        public int onEnd() {
            System.out.println("Got all players, starting game.");
            //myAgent.addBehaviour(new MapDisplayBehaviour());

            return 0;
        }
    }

    class MapDisplayBehaviour extends Behaviour {
        public void action() {
            System.out.println(((RiskGameAgent) myAgent).continents);
        }

        public boolean done() {
            return false;
        }
    }
}