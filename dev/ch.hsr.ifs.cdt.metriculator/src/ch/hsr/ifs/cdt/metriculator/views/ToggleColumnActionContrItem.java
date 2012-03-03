package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

abstract class ToggleColumnActionContrItem<T extends Item> extends ActionContributionItem{

	protected ToggleColumnActionContrItem(IAction action) {
		super(action);
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

class ToggleTreeColumnAction extends ToggleColumnAction<TreeColumn>{

	public ToggleTreeColumnAction(TreeColumn col) {
		super(col);
	}

	@Override
	public void run() {
		if(isChecked()){
			MetricColumn.showColumn(column);
		}else{
			MetricColumn.hideColumn(column);
		}
	}
}

class ToggleTableColumnAction extends ToggleColumnAction<TableColumn>{

	public ToggleTableColumnAction(TableColumn col) {
		super(col);
	}

	@Override
	public void run() {
		if(isChecked()){
			MetricColumn.showColumn(column);
		}else{
			MetricColumn.hideColumn(column);
		}
	}
}
