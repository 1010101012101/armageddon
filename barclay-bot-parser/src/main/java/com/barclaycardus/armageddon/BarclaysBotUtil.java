package com.barclaycardus.armageddon;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class BarclaysBotUtil {
	public static Map<String, String> card_map = new HashMap<String, String>();
	static{
		card_map.put("suggestionpremiumquery","With barclay's premium credit card you’ll be charged a 3.5% fee, but transfer balance within 60 days and will refund 1.05%, so the effective fee is 2.45%. For more details visit ...");
		card_map.put("suggestiontravelquery","Barclay's travel card which has association with number of airlines which gives great discounts on your tickets and also provide reward point. For more details visit ...");
		card_map.put("suggestionapplequery","Barclay's apple card which gives great discounts on apple store and apple products and also provide great benefits on purchase online through apple pay store. For more details visit ...");
		card_map.put("suggestiononlinequery"," Barclays online card provides great discounts and rewards when you shop online and also provides great user support and reduced interest on emi. For more details visit ...");
	}

	public static InputStream getFileAsInputStream(String fileName) throws IOException{
		return BarclaysBotUtil.class.getClassLoader().getResourceAsStream(fileName);
	}

	public static OutputStream getFileAsOutputStream(String fileName) throws IOException{
		return new FileOutputStream(getFile(fileName));
	}

	public static File getFile(String fileName) throws IOException{
		return new File(BarclaysBotUtil.class.getClassLoader().getResource(fileName).getFile());
	}

	public static String POSTag(String input) throws IOException {
		POSModel model = new POSModelLoader().load(getFile("en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));

		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(input);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
		POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		return sample.toString();
	}
}
