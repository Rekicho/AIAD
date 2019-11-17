import jade.core.*;
import jade.wrapper.*;

public class Risk {
    private static jade.core.Runtime rt;
    private static Profile profile;
    private static ContainerController mainContainer;

    static {
        rt = jade.core.Runtime.instance();
        profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        mainContainer = rt.createMainContainer(profile);
    }

    public static void main(String[] args) throws StaleProxyException {
        Object[] riskAgentArgs = new Object[1];
        riskAgentArgs[0] = 4;
        AgentController riskGameAC = mainContainer.createNewAgent("RiskGame", "RiskGameAgent", riskAgentArgs);
        riskGameAC.start();

        Object[] basicRiskPlayerArgs = new Object[2];
        basicRiskPlayerArgs[0] = "RiskGame";
        basicRiskPlayerArgs[1] = AID.ISLOCALNAME;

        AgentController basicRiskPlayer1AC = mainContainer.createNewAgent("BasicRiskPlayer1", "BasicRiskPlayerAgent",
                basicRiskPlayerArgs);
        basicRiskPlayer1AC.start();

        AgentController basicRiskPlayer2AC = mainContainer.createNewAgent("BasicRiskPlayer2", "BasicRiskPlayerAgent",
                basicRiskPlayerArgs);
        basicRiskPlayer2AC.start();

        AgentController basicRiskPlayer3AC = mainContainer.createNewAgent("BasicRiskPlayer3", "BasicRiskPlayerAgent",
                basicRiskPlayerArgs);
        basicRiskPlayer3AC.start();

        AgentController basicRiskPlayer4AC = mainContainer.createNewAgent("BasicRiskPlayer4", "BasicRiskPlayerAgent",
                basicRiskPlayerArgs);
        basicRiskPlayer4AC.start();
    }
}