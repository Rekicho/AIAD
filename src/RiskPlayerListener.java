import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RiskPlayerListener extends Behaviour {
    public void action() {
        myAgent.receive();
    }

    public boolean done() {
        return false;
    }

}