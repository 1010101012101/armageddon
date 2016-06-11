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

    @RequestMapping("/getAction/{sentence}")
    @ResponseBody
    String getAction(@PathVariable String sentence) {
        try {
            String sentenceDetectorTrainer = BarclayBotParser.SentenceDetectorTrainer(sentence);
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

