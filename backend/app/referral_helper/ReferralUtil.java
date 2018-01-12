package referral_helper;

import java.util.Date;

import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import drools.Exchanges;

public class ReferralUtil {



	public ReferralUtil() {
		
	}

	public static double innerProduct(double weight, double[] a, double[] b) {
		double result = 0;

		for (int i = 0; i < a.length; i++) {
			result += (a[i] * b[i]);
		}

		result = result * weight;

		return result;
	}

	public static double getFitnessScore(double weight, double[] query, double[] sociability, double[] expertise) {

		return (innerProduct(weight, query, sociability) * innerProduct(1 - weight, query, expertise));
	}

	public static void logDroolsMessage(String actorName, boolean send, String type, boolean tell) {

		KieServices kieServices = new KieServicesImpl();
		KieContainer kc = kieServices.getKieClasspathContainer(ReferralUtil.class.getClassLoader());
		KieSession kieSession = kc.newKieSession("ReferraldKS");

		Exchanges message = new Exchanges(new Date(), actorName, send, type, tell);

		kieSession.insert(message);

		kieSession.fireAllRules();

		kieSession.dispose();
	}
}
