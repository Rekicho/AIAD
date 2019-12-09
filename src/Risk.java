import jade.core.*;
import jade.core.AID;
import jade.wrapper.*;
import java.util.Random;

public class Risk {
    private static jade.core.Runtime rt;
    private static Profile profile;
    private static ContainerController mainContainer;
    private static Random rng;

    static {
        rt = jade.core.Runtime.instance();
        profile = new ProfileImpl();
        //profile.setParameter(Profile.GUI, "true");
        mainContainer = rt.createMainContainer(profile);
        rng = new Random();
    }

    public static void main(String[] args) throws StaleProxyException {
        int numberPlayers = rng.nextInt(5)+2;

        Object[] riskAgentArgs = new Object[1];
        riskAgentArgs[0] = numberPlayers;
        AgentController riskGameAC = mainContainer.createNewAgent("RiskGame", "RiskGameAgent", riskAgentArgs);
        riskGameAC.start();

        Object[] basicRiskPlayerArgs = new Object[2];
        basicRiskPlayerArgs[0] = "RiskGame";
        basicRiskPlayerArgs[1] = AID.ISLOCALNAME;

        for (int i = 0; i < numberPlayers; i++) {
            boolean smart = rng.nextBoolean();

            AgentController RiskPlayerAC;
            if(smart) {
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