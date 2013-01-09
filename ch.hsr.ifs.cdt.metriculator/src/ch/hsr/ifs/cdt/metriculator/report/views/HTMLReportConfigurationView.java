package ch.hsr.ifs.cdt.metriculator.report.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import ch.hsr.ifs.cdt.metriculator.report.HTMLReportGenerator;
import ch.hsr.ifs.cdt.metriculator.report.ReportConfigurationStore;

public class HTMLReportConfigurationView extends ConfigurationView {

	private String report = "dynamic";
	private List list;

	public HTMLReportConfigurationView(ReportConfigurationStore config) {
		super(config);
	}

	@Override
	public void createViewControls(Group parent) {
		parent.setText("HTML Report");

		RowLayout layout = new RowLayout();
		layout.spacing = 10;
		parent.setLayout(layout);

		list = new List(parent, SWT.BORDER | SWT.SINGLE);
		// TODO dynamically add items according to report sub-directory names
		list.add("dynamic");
		list.add("static");
		list.setSelection(0);
		list.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				report = list.getSelection()[0];
			}
		});
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("Choose report template");
	}

	@Override
	public void writeConfiguration() {
		config.set(HTMLReportGenerator.class, HTMLReportGenerator.CONFIG_REPORT, this.report);
	}
}
