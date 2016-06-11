package com.barclaycardus.armageddon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

public class BaseDictionary {

	public static void main(String[] args) throws Exception{
		FileUtils.copyFile(new File(BarclayBotParser.URL + "base_dictionary.txt"), new File(BarclayBotParser.URL + "dictionary.txt"));
		getDistinctWordList(BarclayBotParser.URL + "en-base_barclay_bot.train", BarclayBotParser.URL + "dictionary.txt");
		convertToSmall(BarclayBotParser.URL + "en-base_barclay_bot.train", BarclayBotParser.URL + "en-barclay_bot.train");
	}
	public static void getDistinctWordList(String inputFile, String outputFile) throws Exception{

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
			
			for(String word : words){
				fileWriter.write(word + "\n");
			}
			
			fileWriter.close();
	}
	
	public static void convertToSmall(String inputFile, String outputFile) throws Exception{

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
				fileWriter.write(line.toLowerCase() + "\n");
			}
			br.close();
			fileWriter.close();
	}

}
