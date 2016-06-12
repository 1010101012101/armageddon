package com.barclaycardus.armageddon.controller;

import com.barclaycardus.armageddon.BarclayBotParser;
import com.barclaycardus.armageddon.QueryResponse;
import com.barclaycardus.armageddon.RuleEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Junaid on 11-Jun-16.
 */
@RestController
@EnableAutoConfiguration
public class SentenceParserController {

    BarclayBotParser barclayBotParser=new BarclayBotParser();
    WorkFlowService workFlowService = new WorkFlowService();
    BarclayBotRepository barclayBotRepository = new BarclayBotRepository();

    @RequestMapping("/createSession/{userId}")
    @ResponseBody
    String createSession(@PathVariable String userId) {
        return barclayBotRepository.createSessionForUser(userId);
    }

    @RequestMapping("/getGreetingMessage/{userId}")
    @ResponseBody
    String getGreetingMessage(@PathVariable String userId) {
        return "Welcome " + barclayBotRepository.getUserName(userId) + ". How can I help you";
    }

    @RequestMapping("/getAction/{sessionId}")
    @ResponseBody
    String getAction(@PathVariable String sessionId, @RequestBody String content) {
        try {
            content = content.toLowerCase();
            Communication lastCommunication = barclayBotRepository.getLastCommunication(sessionId);
            if(lastCommunication ==null){
                String action = barclayBotParser.predictAction(content).get(0);
                Communication communication = new Communication(action,content);
                communication.setUserId(barclayBotRepository.getUserId(sessionId));
                communication.setSessionId(sessionId);
                communication.setPreviousCommunication(null);
                RuleEngine.populateRespose(communication);
                workFlowService.invokeWorkFlow(communication);
                barclayBotRepository.addCommunicationDetailsForSession(sessionId, communication);
                QueryResponse.reset();
                return communication.getReply();
            }else{
                String action = barclayBotParser.predictAction(content).get(0);
                Communication communication = new Communication(action,content);
                communication.setUserId(barclayBotRepository.getUserId(sessionId));
                communication.setSessionId(sessionId);
                communication.setPreviousCommunication(lastCommunication);
                RuleEngine.populateRespose(communication);
                workFlowService.invokeWorkFlow(communication);
                barclayBotRepository.addCommunicationDetailsForSession(sessionId, communication);
                QueryResponse.reset();
                return communication.getReply();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Please try again";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SentenceParserController.class, args);
        RuleEngine.initiateEngine();
        //SentenceDetectorTrainer("hi, good morning. change phone");
    }
}

