package com.barclaycardus.armageddon.controller.validator;

import com.barclaycardus.armageddon.controller.Communication;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jyotik on 6/12/2016.
 */
public class ServiceInvoker {

    List<String> validServiceActions = Arrays.asList("CONFIRMED_PHONE", "CONFIRMED_EMAIL", "CONFIRMED_ADDRESS");
    public void invokeService(Communication communication){
        if(validServiceActions.contains(communication.getAction())) {
            System.out.println("Service Invoked for " + communication.getUserId() + " with action " + communication.getAction());
            communication.setReply((null!=communication.getReply()?communication.getReply():"") + " Do you want any other assistance");
            communication.setAction("CONFIRM_EXIT");
        }
    }
}
