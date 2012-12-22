package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.CompoundContributionItem;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.report.FileReportGenerator;
import ch.hsr.ifs.cdt.metriculator.report.HTMLReportGenerator;
import ch.hsr.ifs.cdt.metriculator.report.ReportConfigurationStore;
import ch.hsr.ifs.cdt.metriculator.report.TextReportGenerator;
import ch.hsr.ifs.cdt.metriculator.report.views.ConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.IConfigurableReport;
import ch.hsr.ifs.cdt.metriculator.report.views.ReportConfigurationDialog;
import ch.hsr.ifs.cdt.metriculator.resources.Icon;

/**
 * @see https://dev.eclipse.org/svnroot/technology/eu.geclipse/branches/I20090916/plugins/eu.geclipse.ui/src/eu/geclipse/ui/views/AuthTokenView.java
 * */
public class ExportActionMenuCreator implements IMenuCreator {

	private MenuManager dropDownMenuMgr;
	private MetriculatorView metriculatorView;

	public ExportActionMenuCreator(MetriculatorView metriculatorView) {
		this.metriculatorView = metriculatorView;
	}
	
	private CompoundContributionItem dropdownMenu = new CompoundContributionItem() {
		
		private AbstractNode getRootFromActiveView() {
			switch(metriculatorView.getViewMode()){
				case Filtered:
					return MetriculatorPluginActivator.getDefault().getFlatTreeBuilder().root;
				case Logical:
					return MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
				default:
					return MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
			}
		}
		
		private void runReportGenerator(FileReportGenerator gen) {
			try {
				// feature: only export displayed metrics or choose metrics in wizard
				final Collection<AbstractMetric> metrics = MetriculatorPluginActivator.getDefault().getMetrics();
				final AbstractNode root = getRootFromActiveView();
				
				// possibly init settingsstore with defaults (from cookies etc.)
				ReportConfigurationStore configStore = new ReportConfigurationStore();
				configStore.set(MetriculatorView.class, "instance", metriculatorView);
				
				if (gen instanceof IConfigurableReport) {
					List<ConfigurationView> views = ((IConfigurableReport) gen).getConfigurationViews(configStore);
					int status = createConfigurationView(views).open();
					
					if(status != Window.OK){
						return;
					}
					
					for(ConfigurationView view : views){
						view.writeConfiguration();
					}
				}
				gen.run(configStore, root, metrics);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private ReportConfigurationDialog createConfigurationView(List<ConfigurationView> views) {
			
			ReportConfigurationDialog reportConfigurationDialog = new ReportConfigurationDialog(metriculatorView.getSite().getShell(), views);
			reportConfigurationDialog.create();
			reportConfigurationDialog.setHelpAvailable(false);
			
			return reportConfigurationDialog;
		}
		
		@Override
		protected IContributionItem[] getContributionItems() {
			List<IContributionItem> itemList = new LinkedList<IContributionItem>();

			Action exportHTMLAction = new Action() {
				@Override
				public void run() {
					runReportGenerator(new HTMLReportGenerator());
				}
			};
			exportHTMLAction.setText("HTML");
			exportHTMLAction.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.HTML));
			itemList.add(new ActionContributionItem(exportHTMLAction));

			Action exportTextAction = new Action() {
				@Override
				public void run() {
					runReportGenerator(new TextReportGenerator());
				}
			};
			exportTextAction.setText("ASCII");
			exportTextAction.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.TEXT));
			itemList.add(new ActionContributionItem(exportTextAction));
			
			return itemList.toArray(new IContributionItem[0]);
		}
	};

	private void createDropDownMenuMgr() {
		if (this.dropDownMenuMgr == null) {
			this.dropDownMenuMgr = new MenuManager();
			this.dropDownMenuMgr.add(dropdownMenu);
		}
	}

	public void dispose() {
		if (this.dropDownMenuMgr != null) {
			this.dropDownMenuMgr.dispose();
			this.dropDownMenuMgr = null;
		}
	}

	public Menu getMenu(final Control parent) {
		createDropDownMenuMgr();
		return this.dropDownMenuMgr.createContextMenu(parent);
	}

	public Menu getMenu(final Menu parent) {
		createDropDownMenuMgr();
		Menu menu = new Menu(parent);
		IContributionItem[] items = this.dropDownMenuMgr.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			IContributionItem newItem = item;
			if (item instanceof ActionContributionItem) {
				newItem = new ActionContributionItem(
						((ActionContributionItem) item).getAction());
			}
			newItem.fill(menu, -1);
		}
		return menu;
	}
}
