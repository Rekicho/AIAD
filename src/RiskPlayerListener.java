import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import src.Utils;

public class RiskPlayerListener extends Behaviour {
    public void readMap(String map) {
        ((BasicRiskPlayerAgent)myAgent).riskMap = (RiskMap) Utils.fromString(map);
        System.out.println("[" + myAgent.getLocalName() + "] Got Map");
    }

    public void sendGameAction(ACLMessage msg, String response) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        reply.setContent(response);
        myAgent.send(reply);
        System.out.println(reply.getContent());

    }

    public void placeArmies(ACLMessage msg) {
        String response = ((BasicRiskPlayerAgent)myAgent).placeArmies();
        sendGameAction(msg, response);
    }

    public void doPlacement(AID player, String country) {
        ((BasicRiskPlayerAgent)myAgent).riskMap.placeIfValid(player,country);
    }

    public void doPlacementList(AID player, String placementList) {
        ((BasicRiskPlayerAgent)myAgent).riskMap.placeIfValidList(player,placementList);
    }

    public void placeNewArmies(ACLMessage msg, int armies) {
        String response = ((BasicRiskPlayerAgent)myAgent).placeNewArmies(armies);
        sendGameAction(msg, response);
    }

    public void attack(ACLMessage msg) {
        String response = ((BasicRiskPlayerAgent)myAgent).attack();
        sendGameAction(msg, response);
    }

    public void defend(ACLMessage msg) {
        String[] message = msg.getContent().split("\n")[1].split(" ");
        String response = ((BasicRiskPlayerAgent)myAgent).defend(message[1], message[2], Integer.parseInt(message[3]));
        sendGameAction(msg, response);
    }

    public void doFightResolve(String msg) {
        String[] args = msg.split(" ");
        ((BasicRiskPlayerAgent)myAgent).riskMap.fight(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3],Integer.parseInt(args[4]));
    }

    public void interpretMessage(ACLMessage msg)
    {
        System.out.println(msg.getContent());
        String[] args = msg.getContent().split("\n");
        switch(args[0]) {
            case "[MAP]": readMap(args[1]);
                break;
            case "[PLACE]": placeArmies(msg);
                break;
            case "[GAME_PLACE]": placeNewArmies(msg,Integer.parseInt(args[1]));
                break;
            case "[PLACEMENT]": doPlacement(new AID(args[1].split(" ")[0],AID.ISLOCALNAME),args[1].split(" ")[1]);
                break;
            case "[GAME_PLACEMENT]": doPlacementList(new AID(args[1].split(" ")[0],AID.ISLOCALNAME),args[1].split(" ")[1]);
                break;
            case "[REQUEST_ATTACK]": attack(msg);
                break;
            case "[DEFEND]": defend(msg);
                break;
            case "[FIGHT]": doFightResolve(args[1]);
                break;
            default: break;
        }
    } 
    
    public void action() {
        ACLMessage msg = myAgent.receive();
        if(msg != null)
            interpretMessage(msg);
    }

    public boolean done() {
        return false;
    }

}