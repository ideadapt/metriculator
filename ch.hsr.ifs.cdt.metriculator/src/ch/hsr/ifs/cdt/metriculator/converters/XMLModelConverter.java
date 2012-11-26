package ch.hsr.ifs.cdt.metriculator.converters;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLModelConverter implements IModelConverter<Document> {

	protected Document doc;

	@Override
	public void convert(AbstractNode node, AbstractMetric... metrics) {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		doc = documentBuilder.newDocument();
		Element root = doc.createElement("metriculator");
		doc.appendChild(root);
		
		root.appendChild(doc.createElement("Directory"));
		
	}

	@Override
	public Document getResult() {
		return doc;
	}

	public String getXML() {

		OutputFormat format = new OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		try {
			serializer.serialize(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toString();
	}
}
