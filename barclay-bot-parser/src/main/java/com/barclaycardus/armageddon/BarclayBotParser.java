package com.barclaycardus.armageddon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class BarclayBotParser {

	public static String URL = "C:\\Users\\jyotik\\workspace\\barclay-bot-parser\\src\\main\\resources\\";

	public static void main(String[] args) throws Exception {
//		 SentenceDetect();
//		 POSTag();
		SentenceDetectorTrainer();
	}

	public static String[] SentenceDetect(String paragraph) throws InvalidFormatException,
			IOException {
		InputStream is = new FileInputStream(URL + "en-sent.zip");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);
		is.close();
		
		return sentences;
	}

	public static void SentenceDetectorTrainer() throws Exception {
		DoccatModel model = null;

		InputStream dataIn = null;
		dataIn = new FileInputStream(URL + "en-barclay_bot.train");
		ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
				"UTF-8");
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
				lineStream);

		TrainingParameters trainingParameters = new TrainingParameters();
		trainingParameters.put(TrainingParameters.CUTOFF_PARAM, "1");
		System.out.println(trainingParameters.getSettings());
		model = DocumentCategorizerME.train("en", sampleStream,trainingParameters);
		dataIn.close();
		OutputStream modelOut = null;
		modelOut = new BufferedOutputStream(new FileOutputStream(URL + "en-barclay_bot.zip"));
		model.serialize(modelOut);
		
			  String classificationModelFilePath = URL + "en-barclay_bot.zip";
			  DocumentCategorizerME classificationME =
			    new DocumentCategorizerME(
			      new DoccatModel(
			        new FileInputStream(
			          classificationModelFilePath)));
			  String documentContent = "My name is Junaid";
			  JazzySpellChecker jazzySpellChecker = new JazzySpellChecker();
			  documentContent = jazzySpellChecker.getCorrectedLine(documentContent);
			  String[] sentences = SentenceDetect(documentContent);
			  for(String sentence : sentences){
				  System.out.println(sentence);
				  double[] classDistributions = classificationME.categorize(sentence);
				  if(isClassDistributionsValid(classDistributions)){
					  String predictedCategory = classificationME.getBestCategory(classDistributions);
					  System.out.println();
					  for(double classDistribution : classDistributions){
						  System.out.println( " " + classDistribution);
					  }
					  System.out.println();
					  System.out.println("Model prediction : " + predictedCategory);
				  }else{
					  System.out.println("Sorry, I am not able to understand your statement, Could you please elaborate?");
				  }
				  
			  }
	}

	private static boolean isClassDistributionsValid(double[] classDistributions){
		Set<Double> values = new HashSet<Double>();
		for(Double classDistribution : classDistributions){
			values.add(classDistribution);
		}
		return values.size()>1;
	}
	public static void POSTag() throws IOException {
		POSModel model = new POSModelLoader().load(new File(URL
				+ "/en-pos-maxent.zip"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);

//		String input = "can you help me change my addresss to 215 pune wanowrie?";
		String input = "what is my name";
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));

		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {

			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);

			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());

			perfMon.incrementCounter();
		}
	}

}
