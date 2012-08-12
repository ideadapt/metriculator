package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

class TreeContentProvider implements ITreeContentProvider {
	
	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof AbstractNode){
			return ((AbstractNode)parentElement).getChildren().toArray();
		}
		
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof AbstractNode){
			return ((AbstractNode) element).getParent();
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		
		if (element instanceof AbstractNode){
			return ((AbstractNode) element).getChildren().size() > 0;
		}
		
		return false;
	}
	
	@Override
	public Object[] getElements(Object node) {
		return getChildren(node);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
