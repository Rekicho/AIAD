import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import src.*;

public class RiskGameAgent extends Agent {
    private ArrayList<AID> players;
    private int numberPlayers;
    private static final HashMap<Integer, Integer> startingArmies;
    private RiskMap riskMap;
    private int armiesToPlace;
    private int placedArmies;

    static {
        HashMap<Integer, Integer> startingArmiesMap = new HashMap<Integer, Integer>();

        startingArmiesMap.put(2, 40);
        startingArmiesMap.put(3, 35);
        startingArmiesMap.put(4, 30);
        startingArmiesMap.put(5, 25);
        startingArmiesMap.put(6, 20);
        startingArmies = startingArmiesMap;
    }

    public void setup() {
        Object[] args = getArguments();

        riskMap = new RiskMap();

        if (args == null || riskMap.getContinents().isEmpty() || riskMap.getCountries().isEmpty()) {
            doDelete();
            return;
        }

        numberPlayers = Integer.parseInt(args[0].toString());
        players = new ArrayList<AID>(numberPlayers);
        armiesToPlace = numberPlayers * startingArmies.get(numberPlayers);

        addBehaviour(new WaitForPlayers());
    }

    class WaitForPlayers extends Behaviour {
        public void action() {
            int playersLeft = (((RiskGameAgent) myAgent).numberPlayers - ((RiskGameAgent) myAgent).players.size());
            System.out.println(
                    "[RiskGameAgent] Waiting for " + playersLeft + " more player" + (playersLeft != 1 ? "s." : '.'));
            ACLMessage msg = receive();

            if (msg != null) {
                AID newPlayer = msg.getSender();
                ACLMessage reply = msg.createReply();

                if (players.contains(newPlayer)) {
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("[ALREADY_JOINED]\nYou are already on the player list.");
                } else {
                    players.add(newPlayer);
                    System.out.println("[RiskGameAgent] Added " + newPlayer.getLocalName() + "to the player list.");
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("[ADDED]\nAdded you to the player list.");
                }
                send(reply);
            } else {
                block();
            }
        }

        public boolean done() {
            return ((RiskGameAgent) myAgent).players.size() == ((RiskGameAgent) myAgent).numberPlayers;
        }

        public int onEnd() {
            System.out.println("[RiskGameAgent] Got all players, starting game.");
            Collections.shuffle(players);

            myAgent.addBehaviour(new ListenerBehaviour());
            myAgent.addBehaviour(new SendMapBehaviour());
            myAgent.addBehaviour(new PlayerDeploymentBehaviour());

            return 0;
        }
    }

    class ListenerBehaviour extends Behaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null)
                interpretMessage(msg);
        }

        private void interpretMessage(ACLMessage msg) {
            String header = msg.getContent().split("\n")[0];
            String arguments = msg.getContent().split("\n")[1];

            switch (header) {
            case "[PLACEMENT]":
                if (!msg.getSender().equals(players.get(placedArmies % numberPlayers))) // Not his turn
                    return;

                boolean valid = riskMap.placeIfValid(msg.getSender(), arguments.split(" ")[1]);

                if (!valid) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("[PLACE]\n");
                    send(reply);
                    return;
                }

                if (valid) {
                    placedArmies++;
                    ACLMessage notify = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    notify.setContent(msg.getContent());
                    for (int i = 0; i < numberPlayers; i++)
                        notify.addReceiver(players.get(i));
                    send(notify);
                }

                System.out.println(msg.getSender() + " PLACED " + arguments.split(" ")[1]);

                break;
            default:
                break;
            }
        }

        public boolean done() {
            return false;
        }
    }

    class SendMapBehaviour extends Behaviour {
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("[MAP]\n" + Utils.toString(riskMap));
            for (int i = 0; i < numberPlayers; i++)
                msg.addReceiver(players.get(i));
            send(msg);
        }

        public boolean done() {
            return true;
        }
    }

    class MapDisplayBehaviour extends Behaviour {
        public void action() {
            System.out.println(((RiskGameAgent) myAgent).players);
            System.out.println(((RiskGameAgent) myAgent).riskMap);
        }

        public boolean done() {
            return true;
        }
    }

    class PlayerDeploymentBehaviour extends Behaviour {
        int sentMessages = 0;

        public void action() {
            if (sentMessages == placedArmies) {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("[PLACE]\n");
                msg.addReceiver(players.get(sentMessages % numberPlayers));
                send(msg);
                sentMessages++;
            }
        }

        public boolean done() {
            return sentMessages == armiesToPlace;
        }

        public int onEnd() {
            // myAgent.addBehaviour(new MapDisplayBehaviour());
            myAgent.addBehaviour(new GameLoopBehaviour());
            return 0;
        }
    }

    class GameLoopBehaviour extends Behaviour {

        public void action() {

        }

        public boolean done() {
            return riskMap.checkGameOver();

        }

        public int onEnd() {
            myAgent.addBehaviour(new MapDisplayBehaviour());
            return 0;
        }
    }
}