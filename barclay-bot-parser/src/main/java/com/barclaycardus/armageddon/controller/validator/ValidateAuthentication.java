package com.barclaycardus.armageddon.controller.validator;

import com.barclaycardus.armageddon.controller.BarclayBotRepository;
import com.barclaycardus.armageddon.controller.Communication;

import java.util.*;

/**
 * Created by jyotik on 6/12/2016.
 */
public class ValidateAuthentication {
    ServiceInvoker serviceInvoker = new ServiceInvoker();
    Map<String, String> usersAuthenticated = new HashMap<String, String>();
    Set<String> validEvents = new HashSet<String>(Arrays.asList("CONFIRMED_PHONE", "CONFIRMED_EMAIL", "CONFIRMED_ADDRESS"));

    Map<String, List<String>> authentaicationDetails = new HashMap<String, List<String>>();
    public ValidateAuthentication(){
        usersAuthenticated.put("User1", "Basic");
        usersAuthenticated.put("User2", "Secure");
        authentaicationDetails.put("Basic", Arrays.asList("CONFIRMED_PHONE", "CONFIRMED_EMAIL"));
        authentaicationDetails.put("Secure", Arrays.asList("CONFIRMED_PHONE", "CONFIRMED_EMAIL", "CONFIRMED_ADDRESS"));
    }

    public Communication authenticate(Communication communication){
        if(validEvents.contains(communication.getAction())) {
            String action = communication.getAction();
            List<String> validActionsForUser = authentaicationDetails.get(usersAuthenticated.get(communication.getUserId()));
            if (!validActionsForUser.contains(action)) {
                communication.setReply("Could not proceed with changes as current authentication level is not enough. please contact customer care");
                communication.setAction("PLEASE_AUTHENTICATE");
            }
        }
        return communication;
    }
}
