package ch.hsr.ifs.cdt.metriculator.model.converters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;

public class MetriculatorXMLDocument {
	
	public org.w3c.dom.Document doc;
	public Node propertiesElement;
	
	public MetriculatorXMLDocument(){
		initXMLDoc();
	}
	
	private void initXMLDoc() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		doc = documentBuilder.newDocument();
		createRootElement();
		createPreferencesElement();
	}
	
	private void createPreferencesElement() {
		propertiesElement = doc.getDocumentElement().appendChild(doc.createElement("properties"));
	}

	private void createRootElement() {
		doc.appendChild(doc.createElement("metriculator"));
	}
}
