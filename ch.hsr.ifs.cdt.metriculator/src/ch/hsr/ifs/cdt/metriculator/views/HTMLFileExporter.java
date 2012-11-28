package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;

import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.XMLModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class HTMLFileExporter extends FileExporter {

	public HTMLFileExporter(Path export_location, AbstractNode root, Collection<AbstractMetric> metrics) {
		super(export_location, root, metrics);
	}

	@Override
	public void run() {
		XMLModelConverter x = new XMLModelConverter();
		x.convert(root, metrics);
	
		// write xml to file
		String xml = x.getXML();
		System.out.println(xml);
	}

}
