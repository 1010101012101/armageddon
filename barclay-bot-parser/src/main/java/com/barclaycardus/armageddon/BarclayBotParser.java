package com.barclaycardus.armageddon;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Component
public class BarclayBotParser {

	DocumentCategorizerME classificationME;
	JazzySpellChecker jazzySpellChecker;

	public BarclayBotParser() {
		try {
			BarclaysBotTrainer barclaysBotTrainer = new BarclaysBotTrainer();
			classificationME = new DocumentCategorizerME(new DoccatModel(BarclaysBotUtil.getFileAsInputStream("en-barclay_bot.bin")));
			jazzySpellChecker = new JazzySpellChecker();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		BarclayBotParser barclayBotParser = new BarclayBotParser();
		System.out.println(barclayBotParser.predictAction("Help change in address"));
		System.out.println(barclayBotParser.predictAction("Hello how are you doing"));
	}

	public List<String> predictAction(String statement) throws Exception {
		List<String> predictions = new ArrayList<String>();
		statement = jazzySpellChecker.getCorrectedLine(statement.toLowerCase());
		double[] classDistributions = classificationME.categorize(statement);
		if (isClassDistributionsValid(classDistributions)) {
			String predictedCategory = classificationME
					.getBestCategory(classDistributions);
			predictions.add(predictedCategory);
		} else {
			predictions.add("INVALID");
		}
		System.out.println("Model prediction : " + predictions);
		return predictions;
	}
	
	public String[] sentenceDetect(String paragraph)
			throws InvalidFormatException, IOException {
		InputStream is = BarclaysBotUtil.getFileAsInputStream("en-sent.bin");
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
