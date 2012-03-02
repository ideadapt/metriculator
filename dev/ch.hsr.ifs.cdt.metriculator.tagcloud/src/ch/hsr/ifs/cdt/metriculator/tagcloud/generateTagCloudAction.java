package ch.hsr.ifs.cdt.metriculator.tagcloud;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.tagcloud.model.Type;
import ch.hsr.ifs.cdt.metriculator.tagcloud.views.TagCloudViewPart;
import ch.hsr.ifs.cdt.metriculator.views.MetriculatorView;

public class generateTagCloudAction implements org.eclipse.ui.IViewActionDelegate {

	private MetriculatorView view;
	
	@Override
	public void run(IAction action) {

		System.out.println("running tag cloud");
		
		if(view != null){
			AbstractMetric metric = ((MetriculatorView) view).getSelectedMetric();
			Collection<AbstractNode> nodes = ((MetriculatorView) view).getSelectedNodes();
			this.generateTagCloud(nodes, metric);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("selection tag cloud");
	}

	@Override
	public void init(IViewPart view) {
		System.out.println("init tagcloud action");	
		
		if(view instanceof MetriculatorView){
			this.view = (MetriculatorView) view;
		}
	}
	
	private void generateTagCloud(Collection<AbstractNode> nodes, AbstractMetric metric) {
		ProgressMonitorDialog dialog = null;
		try {
			dialog = new ProgressMonitorDialog(null);
			dialog.setBlockOnOpen(false);
			dialog.open();
			IProgressMonitor pm = dialog.getProgressMonitor();
			pm.beginTask("Generating tag cloud ...", IProgressMonitor.UNKNOWN);
			
			ArrayList<Type> types = new ArrayList<Type>();
			
			for(AbstractNode n : nodes){
				String nodeName = n.toString();
				// shorten strings, otherwise TagCloudViewPart 
				// throws drawing exceptions due to the limited size of the drawing area.
				if(nodeName.length() > 20){
					nodeName = nodeName.substring(0, 20);
				}
				types.add(new Type(nodeName, n.getNodeValue(metric.getKey())));			
			}

			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TagCloudViewPart.VIEW_ID);
			((TagCloudViewPart) view).getViewer().setInput(types, pm);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(dialog != null){
				dialog.close();
			}
		}
	}

}
