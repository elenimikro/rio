package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** @author eleni */
public class LexicalPatternsParser {
    protected final Set<String> lexicalPatterns = new LinkedHashSet<String>();

    /** @param xml
     *            xml */
    public LexicalPatternsParser(File xml) {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            LexicalPatternHandler handler = new LexicalPatternHandler();
            parser.parse(xml, handler);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class LexicalPatternHandler extends DefaultHandler {
        private final StringBuilder content = new StringBuilder();

        public LexicalPatternHandler() {}

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            content.setLength(0);
            if (qName.equals("lexicalPattern")) {
                lexicalPatterns.add(attributes.getValue("strPattern"));
            }
        }
    }

    /** @return patterns */
    public List<String> getLexicalPatterns() {
        List<String> lexList = new ArrayList<String>(lexicalPatterns);
        Collections.reverse(lexList);
        return lexList;
    }
}
