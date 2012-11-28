package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;

import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.ASCIIModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.IModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class TextFileExporter extends FileExporter {

	public TextFileExporter(Path export_location, AbstractNode root, Collection<AbstractMetric> metrics) {
		super(export_location, root, metrics);
	}

	@Override
	public void run() {
		IModelConverter<String> a = new ASCIIModelConverter();
		a.convert(root, metrics);
	
		// TODO write to file
		System.out.println(a.getResult());
	}

}
