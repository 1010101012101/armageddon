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
	
	public BarclaysBotTrainer() {
		try{
			preProcess();
			train();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Training failed");
		}
	}
	public void train() throws Exception{
			InputStream dataIn = BarclaysBotUtil.getFileAsInputStream("en-barclay_bot.train");
			ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
					"UTF-8");
			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(
					lineStream);

			TrainingParameters trainingParameters = new TrainingParameters();
			trainingParameters.put(TrainingParameters.CUTOFF_PARAM, "1");
			System.out.println(trainingParameters.getSettings());
			DoccatModel model  = DocumentCategorizerME.train("en", sampleStream,trainingParameters);
			dataIn.close();
			OutputStream modelOut = null;
			modelOut = new BufferedOutputStream(BarclaysBotUtil.getFileAsOutputStream("en-barclay_bot.bin"));
			model.serialize(modelOut);
	}
	
	public void loadDictionary() throws Exception{
		FileUtils.copyFile(BarclaysBotUtil.getFile("base_dictionary.txt"), BarclaysBotUtil.getFile("dictionary.txt"));
		addWordsFromTrainingData("en-barclay_bot.train","dictionary.txt");
	}
	
	public void addWordsFromTrainingData(String inputFile, String outputFile)
			throws Exception {
		FileWriter fileWriter = new FileWriter(BarclaysBotUtil.getFile(outputFile), true);
		Set<String> words = new HashSet<String>();
		DataInputStream dis = new DataInputStream(BarclaysBotUtil.getFileAsInputStream(inputFile));
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
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
		loadDictionary();
		convertToSmall("en-base_barclay_bot.train", "en-barclay_bot.train");
	}
	
	public void convertToSmall(String inputFile, String outputFile)
			throws Exception {
		FileWriter fileWriter = new FileWriter(BarclaysBotUtil.getFile(outputFile));
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(BarclaysBotUtil.getFile(inputFile)))));
		String line = null;
		while ((line = br.readLine()) != null) {
			String lowerCaseLine = line.toLowerCase();
			String key = lowerCaseLine.substring(0, lowerCaseLine.indexOf(" "));
			if(key.startsWith("--")){
				fileWriter.write(lowerCaseLine.substring(2) + "\n");
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
