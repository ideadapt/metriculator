package ch.hsr.ifs.cdt.metriculator.views.reports;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.IModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.ModelToASCIIConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class TextReportGenerator extends FileReportGenerator {

	public TextReportGenerator(IPath export_folder, AbstractNode root, Collection<AbstractMetric> metrics) {
		super(export_folder, root, metrics);
	}

	@Override
	public void run() {
		IModelConverter<String> a = new ModelToASCIIConverter();
		a.convert(root, metrics);
	
		IPath textFilename = export_to_dir.append("metrics").addFileExtension("txt");
		writeTo(textFilename, a.getResult());
		open(textFilename);
	}

}
