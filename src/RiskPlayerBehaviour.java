import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public abstract class RiskPlayerBehaviour extends Behaviour {
    private AID riskGameAgentAID;
    private boolean msgSent;
    private boolean replyReceived;

    public RiskPlayerBehaviour(AID riskGameAgentAID) {
        this.riskGameAgentAID = riskGameAgentAID;
        msgSent = false;
        replyReceived = false;
    }

    public void action() {
        if(!msgSent) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent("[JOIN]\nI want to play Risk.");
            msg.addReceiver(riskGameAgentAID);
            myAgent.send(msg);
            msgSent = true;
        } else {
            ACLMessage msg = myAgent.receive();
            if(msg != null)
            {
                System.out.println("[" + myAgent.getLocalName() + "] Joined " + riskGameAgentAID.getLocalName() + "game.");
                replyReceived = true;
            }
        }
    }

    public boolean done() {
        return replyReceived;
    }

    public int onEnd() {
        myAgent.addBehaviour(new RiskPlayerListener());

        return 0;
    }

}