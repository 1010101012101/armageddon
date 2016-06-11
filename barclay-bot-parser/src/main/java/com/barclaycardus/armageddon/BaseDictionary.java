package com.barclaycardus.armageddon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import org.apache.commons.io.FileUtils;

public class BaseDictionary {

	public static void main(String[] args) throws Exception {
		FileUtils.copyFile(new File(BarclayBotParser.URL
				+ "base_dictionary.txt"), new File(BarclayBotParser.URL
				+ "dictionary.txt"));
		getDistinctWordList(BarclayBotParser.URL + "en-base_barclay_bot.train",
				BarclayBotParser.URL + "dictionary.txt");
		convertToSmall(BarclayBotParser.URL + "en-base_barclay_bot.train",
				BarclayBotParser.URL + "en-barclay_bot.train");
	}

	public static void getDistinctWordList(String inputFile, String outputFile)
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

	public static void convertToSmall(String inputFile, String outputFile)
			throws Exception {

		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		FileWriter fileWriter = new FileWriter(new File(outputFile));
		Set<String> words = new HashSet<String>();
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
			String posedString = POSTag(lowerCaseLine);
			String importantWords = getImportantWords(posedString);
			fileWriter.write(key + " " + importantWords + "\n");
		}
		br.close();
		fileWriter.close();
	}

	public static String POSTag(String input) throws IOException {
		POSModel model = new POSModelLoader().load(new File(
				BarclayBotParser.URL + "en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));

		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(input);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
		POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		return sample.toString();
	}

	public static String[] Tokenize(String input)
			throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream(BarclayBotParser.URL
				+ "en-token.bin");

		TokenizerModel model = new TokenizerModel(is);

		TokenizerME tokenizer = new TokenizerME(model);

		String tokens[] = tokenizer.tokenize(input);

		is.close();
		return tokens;
	}

	public static String getImportantWords(String sentence) {

		String words = "";
		Matcher m = Pattern.compile(
				"\\b\\w+(_NN|_NNP|_NNS|_NNPS|_VB|_VBD|_VBG|_VBN|_JJ|_UH)\\b")
				.matcher(sentence);
		while (m.find()) {
			words = words + " " + m.group().substring(0, m.group().indexOf("_"));
		}
		System.out.println(words);
		return words;
	}

}
