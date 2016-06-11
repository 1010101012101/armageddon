package com.barclaycardus.armageddon.controller;

import com.barclaycardus.armageddon.BarclayBotParser;
import com.barclaycardus.armageddon.QueryResponse;
import com.barclaycardus.armageddon.RuleEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Junaid on 11-Jun-16.
 */
@RestController
@EnableAutoConfiguration
public class SentenceParserController {

    BarclayBotParser barclayBotParser=new BarclayBotParser();


    @RequestMapping("/getAction/{sentence}")
    @ResponseBody
    String getAction(@PathVariable String sentence) {
        try {
            String sentenceDetectorTrainer = barclayBotParser.predictAction(sentence).get(0);
            String response = RuleEngine.getResponse(sentenceDetectorTrainer);
            QueryResponse.reset();
            return response;
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

