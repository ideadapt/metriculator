package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.CompoundContributionItem;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.resources.Icon;
import ch.hsr.ifs.cdt.metriculator.views.reports.HTMLReportGenerator;
import ch.hsr.ifs.cdt.metriculator.views.reports.TextReportGenerator;

/**
 * @see https://dev.eclipse.org/svnroot/technology/eu.geclipse/branches/I20090916/plugins/eu.geclipse.ui/src/eu/geclipse/ui/views/AuthTokenView.java
 * */
public class ExportActionMenuCreator implements IMenuCreator {

	private MenuManager dropDownMenuMgr;
	private MetriculatorView metriculatorView;

	private CompoundContributionItem dropdownMenu = new CompoundContributionItem() {
		@Override
		protected IContributionItem[] getContributionItems() {
			List<IContributionItem> itemList = new LinkedList<IContributionItem>();
			
			// feature: only export displayed metrics or choose metrics in wizard
			final Collection<AbstractMetric> metrics = MetriculatorPluginActivator.getDefault().getMetrics();
			final AbstractNode root = getRootFromActiveView();

			Action exportHTMLAction = new Action() {
				@Override
				public void run() {
					
					try {
						
						DirectoryDialog dialog = new DirectoryDialog(metriculatorView.getViewSite().getShell(), SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
						dialog.setText("Directory to Export");
						dialog.setMessage("Choose Directory to Export to");
						String returnCode = dialog.open(); 
						
						if(returnCode == null) return;
						IPath export_folder = Path.fromOSString(returnCode);
						
						HTMLReportGenerator gen = new HTMLReportGenerator(export_folder, root, metrics);
						gen.report = "static";
						gen.run();
						gen = new HTMLReportGenerator(export_folder, root, metrics);
						gen.report = "dynamic";
						gen.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			exportHTMLAction.setText("HTML");
			exportHTMLAction.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.HTML));
			itemList.add(new ActionContributionItem(exportHTMLAction));

			Action exportTextAction = new Action() {
				@Override
				public void run() {
					
					DirectoryDialog dialog = new DirectoryDialog(metriculatorView.getViewSite().getShell(), SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
					dialog.setText("Directory to Export");
					dialog.setMessage("Choose Directory to Export to");
					String returnCode = dialog.open(); 
					
					if(returnCode == null) return;
					IPath export_folder = Path.fromOSString(returnCode);
					
					TextReportGenerator gen = new TextReportGenerator(export_folder, root, metrics);
					gen.run();
				}
			};
			exportTextAction.setText("ASCII");
			exportTextAction.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.TEXT));
			itemList.add(new ActionContributionItem(exportTextAction));
			
			
			return itemList.toArray(new IContributionItem[0]);
		}
	};
	
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

	public ExportActionMenuCreator(MetriculatorView metriculatorView) {
		this.metriculatorView = metriculatorView;
	}

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
