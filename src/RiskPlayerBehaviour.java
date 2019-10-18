import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public abstract class RiskPlayerBehaviour extends Behaviour {
    private AID riskGameAgentAID;

    public RiskPlayerBehaviour(AID riskGameAgentAID) {
        this.riskGameAgentAID = riskGameAgentAID;
    }

    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("I want to play Risk.");
        msg.addReceiver(riskGameAgentAID);
        myAgent.send(msg);
    }

    public boolean done() {
        return true;
    }

    public int onEnd() {
        myAgent.addBehaviour(new RiskPlayerListener());

        return 0;
    }

}