import jade.core.*;
import jade.wrapper.*;

public class Risk {
    public static void main(String[] args) throws StaleProxyException {
        jade.core.Runtime rt = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        ContainerController mainContainer = rt.createMainContainer(profile);
        Object[] agentArgs = new Object[1];
        agentArgs[0] = 1;
        AgentController ac1 = mainContainer.createNewAgent("RiskGame", "RiskGameAgent", agentArgs);
        ac1.start();
    }
}