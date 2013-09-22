package org.coode.lexical.pattern.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LexicalPatternParser {

	private final MultiMap<String, OWLEntity> lexicalPatternMap = new MultiMap<String, OWLEntity>();
	private final OWLOntology o;

	public LexicalPatternParser(String xml, OWLOntology ontology) {
		this.o = ontology;
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			XMLHandler handler = new XMLHandler();
			parser.parse(xml, handler);
			MultiMap<String, String> map = handler.getLexicalPatternMap();
			buildEntityMap(map);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildEntityMap(MultiMap<String, String> map) {
		// OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
		Set<String> lexicals = map.keySet();
		for (String pat : lexicals) {
			Collection<String> uris = map.get(pat);
			Set<OWLEntity> set = new HashSet<OWLEntity>();
			for (String uri : uris) {
				set.addAll(o.getEntitiesInSignature(IRI.create(uri)));
			}
			lexicalPatternMap.putAll(pat, set);
		}

	}

	public MultiMap<String, OWLEntity> getLexicalPatternMap() {
		return lexicalPatternMap;
	}

	public class XMLHandler extends DefaultHandler {

		private final MultiMap<String, String> lexicalPatternMap = new MultiMap<String, String>();
		private String currentPattern;
		boolean inLexicalPattern = false;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("lexicalPattern")) {
				currentPattern = attributes.getValue("strPattern");
				inLexicalPattern = true;
			} else if (inLexicalPattern && qName.equalsIgnoreCase("entity")) {
				lexicalPatternMap.put(currentPattern,
						attributes.getValue("uri"));
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

		}

		public MultiMap<String, String> getLexicalPatternMap() {
			return lexicalPatternMap;
		}

	}
}
