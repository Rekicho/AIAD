import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

enum GamePhase {
    PLACE, ATTACK, FORTIFY
}

enum LoggerInformation {
    SETUP_MAP, SETUP_PLAYERS, INITIAL_PLACE, ROUND_PLACE, ROUND_ATTACK, ROUND_DEFEND, ROUND_FIGHT, ROUND_FORTIFY,
    ROUND_MAP
}

public class RiskGameAgent extends Agent {
    protected ArrayList<AID> players;
    protected int numberPlayers;
    protected static final HashMap<Integer, Integer> startingArmies;
    protected RiskMap riskMap;
    protected int armiesToPlace;
    protected int placedArmies;
    protected int playing;
    protected int round;
    protected AID defending = null;
    protected AID attacking = null;
    protected String attackingCountry = null;
    protected String defendingCountry = null;
    protected GamePhase phase;
    protected Logger logger;

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
        logger = new Logger();

        if (args == null || riskMap.getContinents().isEmpty() || riskMap.getCountries().isEmpty()) {
            doDelete();
            return;
        }

        numberPlayers = Integer.parseInt(args[0].toString());
        players = new ArrayList<AID>(numberPlayers);
        armiesToPlace = numberPlayers * startingArmies.get(numberPlayers);
        round = 0;

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

            // Add riskMap to logger
            addLoggerInformation(LoggerInformation.SETUP_MAP, ((RiskGameAgent) myAgent).players.get(0), null);

            addLoggerInformation(LoggerInformation.SETUP_PLAYERS, ((RiskGameAgent) myAgent).players.get(0), null);

            System.out.println("[RiskGameAgent] Got all players, starting game.");
            Collections.shuffle(players);

            myAgent.addBehaviour(new RiskGameAgentListenerBehaviour());
            myAgent.addBehaviour(new SendMapBehaviour());

            return 0;
        }
    }

    class MapDisplayBehaviour extends Behaviour {
        public void action() {
            System.out.println(((RiskGameAgent) myAgent).players);
            System.out.println(((RiskGameAgent) myAgent).riskMap);
            System.out.println("GAME FINISHED!");
        }

        public boolean done() {
            return true;
        }

        public int onEnd() {
            doDelete();
            return 0;
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

        public int onEnd() {
            myAgent.addBehaviour(new PlayerDeploymentBehaviour());
            return 0;
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
            return placedArmies == armiesToPlace;
        }

        public int onEnd() {
            addInitialMapInformation(riskMap.getInitialInformation());
            myAgent.addBehaviour(new GameLoopBehaviour());
            return 0;
        }
    }

    class GameLoopBehaviour extends Behaviour {
        int last_playing;
        GamePhase last_phase;

        GameLoopBehaviour() {
            addRound();
            playing = 0;
            phase = GamePhase.PLACE;
            last_playing = 0;
            last_phase = GamePhase.PLACE;
            requestPlayerAction(playing, phase);
        }

        public void requestPlayerAction(int player, GamePhase action) {
            ACLMessage msg;
            switch (action) {
            case PLACE:
                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("[GAME_PLACE]\n" + riskMap.calculateArmiesToPlace(players.get(player)));
                msg.addReceiver(players.get(player));
                send(msg);

                break;

            case ATTACK:
                msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("[REQUEST_ATTACK]\n");
                msg.addReceiver(players.get(player));
                send(msg);

                break;

            default:
                break;

            }
        }

        public void action() {
            if (last_playing != playing || last_phase != phase) {
                if (playing == 0)
                    addRound();

                while (!riskMap.stillPlaying(players.get(playing))) {
                    playing++;
                    playing %= numberPlayers;
                    phase = GamePhase.PLACE;
                }

                addLoggerInformation(LoggerInformation.ROUND_MAP, ((RiskGameAgent) myAgent).players.get(playing), null);

                last_playing = playing;
                last_phase = phase;
                requestPlayerAction(playing, phase);
            }
        }

        public boolean done() {
            return riskMap.checkGameOver();
        }

        public int onEnd() {
            ACLMessage notify = new ACLMessage(ACLMessage.INFORM);
            notify.setContent("[GAME_OVER]\n");
            for (int i = 0; i < numberPlayers; i++)
                notify.addReceiver(players.get(i));
            myAgent.send(notify);
            myAgent.addBehaviour(new MapDisplayBehaviour());
            
            Iterator it = riskMap.getCountries().entrySet().iterator();
            Map.Entry pair = (Map.Entry) it.next();
            
            logger.saveGame(((Country) pair.getValue()).getOwner().getLocalName());
            return 0;
        }
    }

    public void addLoggerInformation(LoggerInformation type, AID player, String[] arguments) {

        String name = player.getName();
        String localName = player.getLocalName();

        switch (type) {
        case SETUP_MAP:
            logger.addMap(riskMap.getContinents(), riskMap.getCountries());
            break;
        case SETUP_PLAYERS:
            for (int i = 0; i < numberPlayers; i++) {
                logger.addPlayer(((AID) players.get(i)).getName(), ((AID) players.get(i)).getLocalName());
            }
            break;
        case INITIAL_PLACE:
            // Arguments: playerLocalName SelectedCountryName
            logger.addRoundMove(round, name, localName, Logger.LoggerPhase.INITIAL_PLACE, arguments[1]);
            break;
        case ROUND_PLACE:
            for (int i = 0; i < arguments.length; i++) {
                logger.addRoundMove(round, name, localName, Logger.LoggerPhase.ROUND_PLACE, arguments[i]);
            }
            break;
        case ROUND_ATTACK:
            // Arguments: playerLocalName playerCountryName EnemyCountryName
            // playerCountryArmies
            logger.addRoundMove(round, name, localName, Logger.LoggerPhase.ROUND_ATTACK,
                    (arguments[1] + ' ' + arguments[2] + ' ' + arguments[3]));
            break;
        case ROUND_FORTIFY:
            // Arguments: playerLocalName CountryName1 CountryName2 ArmiesToFortify
            logger.addRoundMove(round, name, localName, Logger.LoggerPhase.ROUND_FORTIFY,
                    arguments[1] + ' ' + arguments[2] + ' ' + arguments[3]);
            break;
        case ROUND_MAP:
            logger.addRoundMap(round, riskMap.getContinents(), riskMap.getCountries());
        default:
            break;
        }
    }

    public void addLoggerInformation(LoggerInformation type, AID player, AID player2, String[] arguments) {

        String name = player2.getName();
        String localName = player2.getLocalName();

        switch (type) {
        case ROUND_DEFEND:
            // Arguments: playerLocalName AttackerCountryName AttackerCountryArmies
            // DefenderCountryName DefenderCountryArmies
            logger.addRoundMove(round, name, localName, Logger.LoggerPhase.ROUND_DEFEND,
                    player.getName() + " " + player.getLocalName() + " " + arguments[4]);
            break;
        case ROUND_FIGHT:
            // Arguments: AttackerCountryName AttackerInitialArmies AttackerLosses
            // DefenderCountryName DefenderLosses
            logger.addRoundMove(round, name, localName, Logger.LoggerPhase.ROUND_FIGHT,
                    arguments[2] + ' ' + arguments[4]);
            break;
        default:
            break;
        }
    }

    public void addInitialMapInformation(HashMap<String, HashMap<String, Integer>> info) {
        logger.addInitialMapInformation(info);
    }

    public void addRound() {
        this.round++;
    }
}
