package ch.hsr.ifs.cdt.metriculator.model.converters;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class ModelToXMLConverter implements IModelConverter<MetriculatorXMLDocument> {

	protected MetriculatorXMLDocument xml;

	@Override
	public void convert(AbstractNode node, Collection<AbstractMetric> metrics) {
		
		if(metrics == null){
			metrics = new ArrayList<AbstractMetric>();
		}

		xml = new MetriculatorXMLDocument();
		INodeVisitor v = new XMLBuilderVisitor(metrics, xml);
		node.accept(v);
	}

	@Override
	public MetriculatorXMLDocument getResult() {
		return xml;
	}

	public String getFormattedXML() {
		OutputFormat format = new OutputFormat(xml.doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		try {
			serializer.serialize(xml.doc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out.toString();
	}
}
