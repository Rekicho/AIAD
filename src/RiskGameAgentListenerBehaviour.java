import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

class RiskGameAgentListenerBehaviour extends Behaviour {
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null)
            interpretMessage(msg);
    }

    private void interpretMessage(ACLMessage msg) {
        String header = msg.getContent().split("\n")[0];

        String arguments = "";
        
        if(msg.getContent().split("\n").length > 1)
            arguments = msg.getContent().split("\n")[1];

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
                reply.setContent("[GAME_PLACE]\n"+ ((RiskGameAgent)myAgent).riskMap.calculateArmiesToPlace(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).playing)));
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

                ((RiskGameAgent)myAgent).phase = GamePhase.ATTACK;
            }
            break;
        case "[ATTACK]":
            if (!msg.getSender().equals(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).playing)) || ((RiskGameAgent)myAgent).phase != GamePhase.ATTACK) // Not his turn or not correct phase
            return;    

            valid = ((RiskGameAgent)myAgent).riskMap.checkValidAttack(msg.getSender(), arguments);

            if (!valid) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("[REQUEST_ATTACK]\n");
                myAgent.send(reply);
                return;
            }

            if (valid) {
                //Ask defending Agent for armies
                System.out.println(msg.getSender() + " ATTACK " + arguments);
                
                ACLMessage msgToDefender = new ACLMessage(ACLMessage.REQUEST);
                msgToDefender.setContent("[DEFEND]\n"+ arguments);
                ((RiskGameAgent)myAgent).defending = ((RiskGameAgent)myAgent).riskMap.getCountries().get(arguments.split(" ")[2]).getOwner();
                ((RiskGameAgent)myAgent).attackingCountry = arguments.split(" ")[1];
                ((RiskGameAgent)myAgent).defendingCountry = arguments.split(" ")[2];
                msgToDefender.addReceiver(((RiskGameAgent)myAgent).defending);
                myAgent.send(msgToDefender);
            }
            break;
        case "[DEFEND]":
            if (((RiskGameAgent)myAgent).defending == null || !msg.getSender().equals(((RiskGameAgent)myAgent).defending))
                return;    

            String[] args = arguments.split(" ");
            valid = ((RiskGameAgent)myAgent).attackingCountry.equals(arguments.split(" ")[1]) &&
                    ((RiskGameAgent)myAgent).defendingCountry.equals(arguments.split(" ")[3]);

            if (!valid) return;

            if (valid) {
                System.out.println(msg.getSender() + " DEFEND " + arguments);

                String response = ((RiskGameAgent)myAgent).riskMap.resolveAttack(arguments);

                System.out.println("FIGHT " + response);
                
                ((RiskGameAgent)myAgent).defending = null;
                ((RiskGameAgent)myAgent).defendingCountry = null;
                ((RiskGameAgent)myAgent).attackingCountry = null;

                ACLMessage notify = new ACLMessage(ACLMessage.INFORM);
                notify.setContent("[FIGHT]\n" + response);
                for (int i = 0; i < ((RiskGameAgent)myAgent).numberPlayers; i++)
                    notify.addReceiver(((RiskGameAgent)myAgent).players.get(i));
                myAgent.send(notify);

                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setContent("[REQUEST_ATTACK]\n");
                request.addReceiver(((RiskGameAgent)myAgent).players.get(((RiskGameAgent)myAgent).playing));
                myAgent.send(request);
            }
            break;
        case "[END_ATTACK]":
            System.out.println(msg.getSender() + " END_ATTACK " + arguments);
            //((RiskGameAgent)myAgent).phase = GamePhase.FORTIFY;
            //Send fortify
            ((RiskGameAgent)myAgent).phase = GamePhase.PLACE;
            ((RiskGameAgent)myAgent).playing++;
            ((RiskGameAgent)myAgent).playing %= ((RiskGameAgent)myAgent).numberPlayers;
            System.out.println(((RiskGameAgent)myAgent).riskMap);
            break;
        default:
            break;
        }
    }

    public boolean done() {
        return false;
    }
}