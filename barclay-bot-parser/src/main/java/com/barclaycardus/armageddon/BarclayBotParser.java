package com.barclaycardus.armageddon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class BarclayBotParser {

	public static String URL = "C:\\Users\\jyotik\\workspace\\barclay-bot-parser\\src\\main\\resources\\";
	DocumentCategorizerME classificationME;
	JazzySpellChecker jazzySpellChecker;

	public BarclayBotParser() throws Exception {
		BarclaysBotTrainer barclaysBotTrainer = new BarclaysBotTrainer();
		String classificationModelFilePath = URL + "en-barclay_bot.bin";
		classificationME = new DocumentCategorizerME(new DoccatModel(
				new FileInputStream(classificationModelFilePath)));
		jazzySpellChecker = new JazzySpellChecker();
	}

	public static void main(String[] args) throws Exception {
		BarclayBotParser barclayBotParser = new BarclayBotParser();
		System.out.println(barclayBotParser.predictAction("Help change in address"));
		System.out.println(barclayBotParser.predictAction("Hello how are you doing"));
	}

	public List<String> predictAction(String statement) throws Exception {
		List<String> predictions = new ArrayList<String>();
		statement = jazzySpellChecker.getCorrectedLine(statement.toLowerCase());
		String[] sentences = sentenceDetect(statement);
		for (String sentence : sentences) {
			double[] classDistributions = classificationME.categorize(sentence);
			if (isClassDistributionsValid(classDistributions)) {
				String predictedCategory = classificationME
						.getBestCategory(classDistributions);
				predictions.add(predictedCategory);
			} else {
				predictions.add("INVALID");
			}
			System.out.println("Model prediction : " + predictions);
		}
		return predictions;
	}
	
	public String[] sentenceDetect(String paragraph)
			throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream(URL + "en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);
		is.close();

		return sentences;
	}

	private static boolean isClassDistributionsValid(double[] classDistributions) {
		Set<Double> values = new HashSet<Double>();
		for (Double classDistribution : classDistributions) {
			values.add(classDistribution);
		}
		return values.size() > 1;
	}

}
