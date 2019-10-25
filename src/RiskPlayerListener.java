import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import src.Utils;

public class RiskPlayerListener extends Behaviour {
    public void readMap(String map) {
        ((BasicRiskPlayerAgent)myAgent).riskMap = (RiskMap) Utils.fromString(map);
        System.out.println("[" + myAgent.getLocalName() + "] Got Map");
    }

    public void interpretMessage(ACLMessage msg)
    {
        String[] args = msg.getContent().split("\n");
        switch(args[0]) {
            case "[MAP]": readMap(args[1]);
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