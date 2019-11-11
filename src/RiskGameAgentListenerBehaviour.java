import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

class RiskGameAgentListenerBehaviour extends Behaviour {
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null)
            interpretMessage(msg);
    }

    private void interpretMessage(ACLMessage msg) {
        String header = msg.getContent().split("\n")[0];
        String arguments = msg.getContent().split("\n")[1];
        boolean valid;

        switch (header) {
        case "[PLACEMENT]":
            if (!msg.getSender().equals(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).placedArmies % ((RiskGameAgent)myAgent).numberPlayers))) // Not his turn
                return;

            valid = ((RiskGameAgent)myAgent).riskMap.placeIfValid(msg.getSender(), arguments.split(" ")[1]);

            if (!valid) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("[PLACE]\n");
                myAgent.send(reply);
                return;
            }

            if (valid) {
                ((RiskGameAgent)myAgent).placedArmies++;
                ACLMessage notify = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                notify.setContent(msg.getContent());
                for (int i = 0; i < ((RiskGameAgent)myAgent).numberPlayers; i++)
                    notify.addReceiver(((RiskGameAgent)myAgent).players.get(i));
                myAgent.send(notify);

                System.out.println(msg.getSender() + " PLACED " + arguments.split(" ")[1]);
            }

            break;
        case "[GAME_PLACEMENT]": 
            if (!msg.getSender().equals(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).playing)) || ((RiskGameAgent)myAgent).phase != GamePhase.PLACE) // Not his turn or not correct phase
                return;

            valid = ((RiskGameAgent)myAgent).riskMap.placeIfValidList(msg.getSender(), arguments.split(" ")[1]);

            if (!valid) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                msg.setContent("[GAME_PLACE]\n"+ ((RiskGameAgent)myAgent).riskMap.calculateArmiesToPlace(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).playing)));
                myAgent.send(reply);
                return;
            }

            if (valid) {
                ACLMessage notify = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                notify.setContent(msg.getContent());
                for (int i = 0; i < ((RiskGameAgent)myAgent).numberPlayers; i++)
                    notify.addReceiver(((RiskGameAgent)myAgent).players.get(i));
                myAgent.send(notify);

                System.out.println(msg.getSender() + " GAME_PLACED " + arguments.split(" ")[1]);

                //Only testing placements
                ((RiskGameAgent)myAgent).phase = GamePhase.ATTACK;
            }
            break;
        case "[END_ATTACK]":
            System.out.println(msg.getSender() + " END_ATTACK " + arguments);
            ((RiskGameAgent)myAgent).phase = GamePhase.FORTIFY;
            break;
        default:
            break;
        }
    }

    public boolean done() {
        return false;
    }
}