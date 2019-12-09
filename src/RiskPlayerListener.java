import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RiskPlayerListener extends Behaviour {
    private boolean gameStarted = false;

    public void readMap(String map) {
        ((BasicRiskPlayerAgent) myAgent).riskMap = (RiskMap) Utils.fromString(map);
        // System.out.println("[" + myAgent.getLocalName() + "] Got Map");
    }

    public void sendGameAction(ACLMessage msg, String response) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        reply.setContent(response);
        myAgent.send(reply);
    }

    public void placeArmies(ACLMessage msg) {
        String response = ((BasicRiskPlayerAgent) myAgent).placeArmies();
        sendGameAction(msg, response);
    }

    public void doPlacement(AID player, String country) {
        ((BasicRiskPlayerAgent) myAgent).riskMap.placeIfValid(player, country);
    }

    public void doPlacementList(AID player, String placementList) {
        ((BasicRiskPlayerAgent) myAgent).riskMap.placeIfValidList(player, placementList);
    }

    public void placeNewArmies(ACLMessage msg, int armies) {
        String response = ((BasicRiskPlayerAgent) myAgent).placeNewArmies(armies);
        sendGameAction(msg, response);
    }

    public void attack(ACLMessage msg) {
        gameStarted = true;
        String response = ((BasicRiskPlayerAgent) myAgent).attack();
        sendGameAction(msg, response);
    }

    public void defend(ACLMessage msg) {
        String[] message = msg.getContent().split("\n")[1].split(" ");
        String response = ((BasicRiskPlayerAgent) myAgent).defend(message[1], message[2], Integer.parseInt(message[3]));
        sendGameAction(msg, response);
    }

    public void doFightResolve(String msg) {
        String[] args = msg.split(" ");
        ((BasicRiskPlayerAgent) myAgent).riskMap.fight(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                args[3], Integer.parseInt(args[4]));
    }

    public void fortify(ACLMessage msg) {
        String response = ((BasicRiskPlayerAgent) myAgent).fortify();
        sendGameAction(msg, response);
    }

    public void doFortify(String args) {
        ((BasicRiskPlayerAgent) myAgent).riskMap.doFortify(args);
    }

    public void requestAlliance(ACLMessage msg) {
        ACLMessage reply = msg.createReply();

        if(((BasicRiskPlayerAgent) myAgent).analyseAlliance(msg.getSender())) {
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            reply.setContent("[ACCEPT_ALLIANCE]\n");
        }

        else {
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent("[REJECT_ALLIANCE]\n");
        }

        myAgent.send(reply);
    }

    public void rejectAlliance() {
        ((BasicRiskPlayerAgent) myAgent).rejectAlliance();
    }

    public void interpretMessage(ACLMessage msg) {
        String[] args = msg.getContent().split("\n");
        switch (args[0]) {
        case "[MAP]":
            readMap(args[1]);
            break;
        case "[PLACE]":
            placeArmies(msg);
            break;
        case "[GAME_PLACE]":
            placeNewArmies(msg, Integer.parseInt(args[1]));
            break;
        case "[PLACEMENT]":
            doPlacement(new AID(args[1].split(" ")[0], AID.ISLOCALNAME), args[1].split(" ")[1]);
            break;
        case "[GAME_PLACEMENT]":
            doPlacementList(new AID(args[1].split(" ")[0], AID.ISLOCALNAME), args[1].split(" ")[1]);
            break;
        case "[REQUEST_ATTACK]":
            attack(msg);
            break;
        case "[DEFEND]":
            defend(msg);
            break;
        case "[FIGHT]":
            doFightResolve(args[1]);
            break;
        case "[REQUEST_FORTIFY]":
            fortify(msg);
            break;
        case "[FORTIFY]":
            doFortify(args[1]);
            break;
        case "[GAME_OVER]":
            myAgent.doDelete();
            break; 
        case "[REQUEST_ALLIANCE]":
            requestAlliance(msg);
            break;
        case "[ACCEPT_ALLIANCE]":
            break;
        case "[REJECT_ALLIANCE]":
            rejectAlliance();
            break;
        case "[TERMINATE_ALLIANCE]":
            rejectAlliance();
            break;
        default:
            break;
        }
    }

    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (gameStarted && !((BasicRiskPlayerAgent) myAgent).riskMap.stillPlaying(myAgent.getAID()))
                myAgent.doDelete();
            interpretMessage(msg);
        }
    }

    public boolean done() {
        return false;
    }

}