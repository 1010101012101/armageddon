package com.barclaycardus.armageddon;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class BarclaysBotUtil {
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
}
