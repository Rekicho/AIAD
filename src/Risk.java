import jade.core.*;
import jade.core.AID;
import jade.wrapper.*;

public class Risk {
    private static jade.core.Runtime rt;
    private static Profile profile;
    private static ContainerController mainContainer;

    static {
        rt = jade.core.Runtime.instance();
        profile = new ProfileImpl();
        // profile.setParameter(Profile.GUI, "true");
        mainContainer = rt.createMainContainer(profile);
    }

    public static void main(String[] args) throws StaleProxyException {

        if (args.length < 2 || args.length > 6) {
            System.out.println("Usage: java Risk <basic||smart>(2,6)");
            System.exit(-1);
        } else {
            for(String s : args) {
                if (!(s.equals("smart") || s.equals("basic"))) {
                    System.out.println("Invalid " + s + "\nUsage: java Risk <basic||smart>(2,6)");
                    System.exit(-1);
                }
            }
        }

        Object[] riskAgentArgs = new Object[1];
        riskAgentArgs[0] = args.length;
        AgentController riskGameAC = mainContainer.createNewAgent("RiskGame", "RiskGameAgent", riskAgentArgs);
        riskGameAC.start();

        Object[] basicRiskPlayerArgs = new Object[2];
        basicRiskPlayerArgs[0] = "RiskGame";
        basicRiskPlayerArgs[1] = AID.ISLOCALNAME;

        int i = 0;
        String agentName;

        for (String player : args) {
            i++;
            AgentController RiskPlayerAC;
            if(player.equals("smart")) {
                RiskPlayerAC = mainContainer.createNewAgent("SmartRiskPlayer" + i, "SmartRiskPlayerAgent",
                        basicRiskPlayerArgs);
                RiskPlayerAC.start();
            } else {
                RiskPlayerAC = mainContainer.createNewAgent("BasicRiskPlayer" + i, "BasicRiskPlayerAgent",
                        basicRiskPlayerArgs);
                RiskPlayerAC.start();
            }
        }
    }
}