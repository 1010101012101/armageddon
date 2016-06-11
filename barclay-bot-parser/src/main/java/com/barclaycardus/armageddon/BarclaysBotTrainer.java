package com.barclaycardus.armageddon;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarclaysBotTrainer {
	
	public static String URL = "G:\\Code\\armageddon\\armageddon\\barclay-bot-parser\\src\\main\\resources\\";
	
	public BarclaysBotTrainer() {
		try{
			train();
		}catch(Exception e){
			throw new RuntimeException("Training failed");
		}
	}
	public void train() throws Exception{
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
			modelOut = new BufferedOutputStream(new FileOutputStream(URL + "en-barclay_bot.bin"));
			model.serialize(modelOut);
	}
	
	public void loadDictionary() throws Exception{
		FileUtils.copyFile(new File(BarclayBotParser.URL
				+ "base_dictionary.txt"), new File(BarclayBotParser.URL
				+ "dictionary.txt"));
		addWordsFromTrainingData(BarclayBotParser.URL + "en-barclay_bot.train",
				BarclayBotParser.URL + "dictionary.txt");
	}
	
	public void addWordsFromTrainingData(String inputFile, String outputFile)
			throws Exception {

		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		FileWriter fileWriter = new FileWriter(new File(outputFile), true);
		Set<String> words = new HashSet<String>();
		fis = new FileInputStream(inputFile);
		dis = new DataInputStream(fis);
		br = new BufferedReader(new InputStreamReader(dis));
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, " ,.;:\"");
			while (st.hasMoreTokens()) {
				String word = st.nextToken().toLowerCase();
				words.add(word);
			}
		}
		br.close();

		for (String word : words) {
			fileWriter.write(word + "\n");
		}

		fileWriter.close();
	}

	
	public void preProcess() throws Exception{
		convertToSmall(BarclayBotParser.URL + "en-base_barclay_bot.train", BarclayBotParser.URL + "en-barclay_bot.train");
	}
	
	public void convertToSmall(String inputFile, String outputFile)
			throws Exception {

		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		FileWriter fileWriter = new FileWriter(new File(outputFile));
		fis = new FileInputStream(inputFile);
		dis = new DataInputStream(fis);
		br = new BufferedReader(new InputStreamReader(dis));
		String line = null;
		while ((line = br.readLine()) != null) {
			String lowerCaseLine = line.toLowerCase();
			String key = lowerCaseLine.substring(0, lowerCaseLine.indexOf(" "));
			if(key.equalsIgnoreCase("Welcome") || key.equalsIgnoreCase("Status")){
				fileWriter.write(lowerCaseLine + "\n");
				continue;
			}
			lowerCaseLine = lowerCaseLine.replaceFirst(key + " " , "");
			String posedString = BarclaysBotUtil.POSTag(lowerCaseLine);
			String importantWords = getImportantWords(posedString);
			fileWriter.write(key + " " + importantWords + "\n");
		}
		br.close();
		fileWriter.close();
	}
	
	public static String getImportantWords(String sentence) {

		String words = "";
		Matcher m = Pattern.compile(
				"\\b\\w+(_NN|_NNP|_NNS|_NNPS|_VB|_VBD|_VBG|_VBN)\\b")
				.matcher(sentence);
		while (m.find()) {
			words = words + " " + m.group().substring(0, m.group().indexOf("_"));
		}
		System.out.println(words);
		return words;
	}
}
