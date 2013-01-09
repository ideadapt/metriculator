package ch.hsr.ifs.cdt.metriculator.report.views;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import ch.hsr.ifs.cdt.metriculator.report.FileReportGenerator;
import ch.hsr.ifs.cdt.metriculator.report.ReportConfigurationStore;

public class FileReportConfigurationView extends ConfigurationView {

	public static final String CONFIG_EXPORT_DIR = "export_dir";
	private DirectoryDialog dialog;
	private IPath path = Path.fromOSString(System.getProperty("user.home")).append("metriculator-export");

	public FileReportConfigurationView(ReportConfigurationStore config) {
		super(config);
	}

	@Override
	public void createViewControls(Group parent) {
		parent.setLayout(new RowLayout());
		parent.setText("Export Location");
		
		dialog = new DirectoryDialog(getMetriculatorView().getViewSite().getShell(), SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
		dialog.setText("Directory to Export");
		dialog.setMessage("Choose Directory to Export to");
		
		final Label lbl = new Label(parent, SWT.NONE);
		lbl.setText(path.toOSString());
		
		Button btnOpen = new Button(parent, SWT.BUTTON1);
		btnOpen.setText("Browse Directories");
		btnOpen.addListener (SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				String returnCode = dialog.open();
				path = Path.fromOSString(returnCode);
				lbl.setText(path.toOSString());
			}
		});
	}

	@Override
	public void writeConfiguration() {
		config.set(FileReportGenerator.class, FileReportGenerator.CONFIG_EXPORT_DIR, path);		
	}
}
