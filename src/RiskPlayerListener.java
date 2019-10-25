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

    public void placeArmies(ACLMessage msg) {
        String response = ((BasicRiskPlayerAgent)myAgent).placeArmies();
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        reply.setContent(response);
        myAgent.send(reply);
    }

    public void doPlacement(AID player, String country) {
        ((BasicRiskPlayerAgent)myAgent).riskMap.placeIfValid(player,country);
    }

    public void interpretMessage(ACLMessage msg)
    {
        String[] args = msg.getContent().split("\n");
        switch(args[0]) {
            case "[MAP]": readMap(args[1]);
                break;
            case "[PLACE]": placeArmies(msg);
                break;
            case "[PLACEMENT]": doPlacement(new AID(args[1].split(" ")[0],AID.ISLOCALNAME),args[1].split(" ")[1]);
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