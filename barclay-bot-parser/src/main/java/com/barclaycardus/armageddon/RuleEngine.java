package com.barclaycardus.armageddon;

import com.barclaycardus.armageddon.controller.Communication;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created by Junaid on 11-Jun-16.
 */
public class RuleEngine {
    static KieSession kSession;

    public static void initiateEngine(){
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        kSession = kContainer.newKieSession("ksession-rules");
    }

    public static void populateRespose(Communication communication){
        QueryRequest queryRequest=new QueryRequest();
        kSession.insert(communication);
        kSession.fireAllRules();
    }
}
