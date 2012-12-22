package ch.hsr.ifs.cdt.metriculator.report.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import ch.hsr.ifs.cdt.metriculator.report.ReportConfigurationStore;
import ch.hsr.ifs.cdt.metriculator.views.MetriculatorView;

public abstract class ConfigurationView {

	private Group group = null; 
	protected ReportConfigurationStore config = null;
	
	public ConfigurationView(ReportConfigurationStore config){
		this.config = config;
	}

	public void create(Composite parent) {
		group = new Group(parent, SWT.NONE);
		group.setBackgroundMode(SWT.INHERIT_FORCE);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		group.setLayoutData(gridData);
		
		createViewControls(group);
	}
	
	public abstract void writeConfiguration();
	
	public abstract void createViewControls(Group parent);

	protected MetriculatorView getMetriculatorView() {
		return (MetriculatorView) config.get(MetriculatorView.class, "instance", null);
	}
}
