package ch.hsr.ifs.cdt.metriculator.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.IModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.ModelToASCIIConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.report.views.ConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.FileReportConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.IConfigurableReport;

public class TextReportGenerator extends FileReportGenerator implements IConfigurableReport {

	@Override
	public void run(ReportConfigurationStore configStore, AbstractNode root, Collection<AbstractMetric> metrics) {
		readConfiguration(configStore);
		
		IModelConverter<String> a = new ModelToASCIIConverter();
		a.convert(root, metrics);
	
		IPath textFilename = export_to_dir.append("metrics").addFileExtension("txt");
		writeTo(textFilename, a.getResult());
		openFileWithDefaultHandler(textFilename);
	}

	@Override
	public List<ConfigurationView> getConfigurationViews(ReportConfigurationStore config) {
		List<ConfigurationView> views = new ArrayList<ConfigurationView>();
		views.add(new FileReportConfigurationView(config));
		return views;
	}

	public void readConfiguration(ReportConfigurationStore configuration) {
		this.export_to_dir = (IPath) configuration.get(FileReportConfigurationView.class, FileReportConfigurationView.CONFIG_EXPORT_DIR, this.export_to_dir);
		if (export_to_dir.isEmpty()) {
			export_to_dir = Path.fromOSString(System.getProperty("user.home")).append("metriculator-export");
		}
	}
}
