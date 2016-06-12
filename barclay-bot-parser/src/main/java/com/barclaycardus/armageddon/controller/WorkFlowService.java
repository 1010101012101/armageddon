package com.barclaycardus.armageddon.controller;

import com.barclaycardus.armageddon.controller.validator.ServiceInvoker;
import com.barclaycardus.armageddon.controller.validator.ValidateAuthentication;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Created by jyotik on 6/12/2016.
 */
public class WorkFlowService {
    CamelContext context = new DefaultCamelContext();
    ValidateAuthentication validateAuthentication = new ValidateAuthentication();
    ServiceInvoker serviceInvoker = new ServiceInvoker();

    public WorkFlowService(){
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:process")
                            .bean(validateAuthentication, "authenticate(${body})")
                            .bean(serviceInvoker, "invokeService(${body})");
                }
            });
            context.start();
        }catch (Exception e){
            throw new RuntimeException("Please try later.");
        }

    }

    public void invokeWorkFlow(Communication communication){
        context.createProducerTemplate().sendBody("direct:process", communication);
    }

}
