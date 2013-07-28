package org.coode.owl.experiments.quality.assurance.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.coode.owl.experiments.quality.assurance.LexicalPatternsParser;
import org.junit.Test;

public class LexicalPatternsParserTest {

	@Test
	public void testLexicalPatternsParser() {
		File xml = new File(
				"snomed/manuel_snomed_outputs/2013/Snomed_2013_LexAnal_Full_0.1-0.4Perc_.xml");
		LexicalPatternsParser parser = new LexicalPatternsParser(xml);
		List<String> lexicalPatterns = parser.getLexicalPatterns();
		assertTrue(!lexicalPatterns.isEmpty());
		assertEquals(464, lexicalPatterns.size());
		for (String s : lexicalPatterns) {
			System.out.println(s);
		}
	}
}
