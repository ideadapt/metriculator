package ch.hsr.ifs.cdt.metriculator.report.views;

import java.util.List;

import ch.hsr.ifs.cdt.metriculator.report.ReportConfigurationStore;


public interface IConfigurableReport {

	List<ConfigurationView> getConfigurationViews(ReportConfigurationStore config);
}
