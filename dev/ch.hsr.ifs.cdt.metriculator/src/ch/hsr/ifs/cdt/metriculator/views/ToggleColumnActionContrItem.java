package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Item;

public abstract class ToggleColumnActionContrItem<T extends Item> extends ActionContributionItem{

	private ToggleColumnActionContrItem(IAction action) {
		super(action);
	}
	
	public ToggleColumnActionContrItem(T column){
		super(new ToggleColumnAction<T>(column));
	}
	
	@SuppressWarnings("unchecked")
	public T getColumn(){
		return ((ToggleColumnAction<T>)getAction()).column;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
	
	public abstract void toggleVisibility();
}
