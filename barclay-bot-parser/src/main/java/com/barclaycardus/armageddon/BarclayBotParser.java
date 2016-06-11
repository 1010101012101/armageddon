package com.barclaycardus.armageddon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

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

public class BarclayBotParser {

	private static String URL = "C:\\Users\\jyotik\\workspace\\barclay-bot-parser\\src\\main\\resources\\";

	public static void main(String[] args) throws Exception {
		// SentenceDetect();
		// POSTag();
		SentenceDetectorTrainer();
	}

	public static void SentenceDetect() throws InvalidFormatException,
			IOException {
		String paragraph = "Hi. How are you? This is Mike.";

		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream(URL + "en-sent.zip");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);

		System.out.println(sentences[0]);
		System.out.println(sentences[1]);
		is.close();
	}

	public static void SentenceDetectorTrainer() throws Exception {
		DoccatModel model = null;

		InputStream dataIn = null;
		dataIn = new FileInputStream(URL + "en-welcome.train");
		ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
				"UTF-8");
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
				lineStream);

		model = DocumentCategorizerME.train("en", sampleStream);
		dataIn.close();
		OutputStream modelOut = null;
		modelOut = new BufferedOutputStream(new FileOutputStream(URL + "en-welcome.zip"));
		model.serialize(modelOut);
		
			  String classificationModelFilePath = URL + "en-welcome.zip";
			  DocumentCategorizerME classificationME =
			    new DocumentCategorizerME(
			      new DoccatModel(
			        new FileInputStream(
			          classificationModelFilePath)));
			  String documentContent = "perhaps the madness was always there but only the schools bring it out?";
			  double[] classDistribution = classificationME.categorize(documentContent);
			  String predictedCategory = classificationME.getBestCategory(classDistribution);
			  System.out.println("" + classDistribution[0] + classDistribution[1] +classDistribution[2]);
			  System.out.println("Model prediction : " + predictedCategory);
	}

	public static void POSTag() throws IOException {
		POSModel model = new POSModelLoader().load(new File(URL
				+ "/en-pos-maxent.zip"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);

		String input = "Hi. How are you? This is Mike.";
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
