package ch.hsr.ifs.cdt.metriculator.model.converters;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Document;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLModelConverter implements IModelConverter<Document> {

	protected Document doc;

	@Override
	public void convert(AbstractNode node, AbstractMetric... metrics) {
		
		INodeVisitor v = new PreOrderXMLTreeVisitor(metrics);
		node.accept(v);
		
		doc = ((PreOrderXMLTreeVisitor)v).doc;
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
