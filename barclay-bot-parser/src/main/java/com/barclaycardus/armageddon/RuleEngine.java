package com.barclaycardus.armageddon;

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

    public static String getResponse(String action){
        QueryRequest queryRequest=new QueryRequest();
        queryRequest.setQuery(action);
        kSession.insert(queryRequest);
        System.out.println("************* Fire Rules **************");
        kSession.fireAllRules();
        System.out.println("************************************");
        String response = QueryResponse.getResponse();
        System.out.println("response " + response);
        return response;
    }
}
