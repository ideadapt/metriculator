package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

abstract class ToggleColumnActionItem<T extends Item> extends ActionContributionItem{

	protected ToggleColumnActionItem(IAction action) {
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

class ToggleTableColumnActionContrItem extends ToggleColumnActionItem<TableColumn>{

	public ToggleTableColumnActionContrItem(TableColumn column) {
		super(new ToggleTableColumnAction(column));
	}

	@Override
	public void toggleVisibility() {
		if(MetricColumn.getMetric(getColumn()).getChecker().hasEnabledProblems()){
			MetricColumn.showColumn(getColumn());
			getAction().setChecked(true);
		}else{
			MetricColumn.hideColumn(getColumn());
			getAction().setChecked(false);
		}
	}
}

class ToggleTreeColumnActionContrItem extends ToggleColumnActionItem<TreeColumn> {

	public ToggleTreeColumnActionContrItem(TreeColumn column) {
		super(new ToggleTreeColumnAction(column));
	}

	@Override
	public void toggleVisibility() {
		if(MetricColumn.getMetric(getColumn()).getChecker().hasEnabledProblems()){
			MetricColumn.showColumn(getColumn());
			getAction().setChecked(true);
		} else {
			MetricColumn.hideColumn(getColumn());
			getAction().setChecked(false);
		}
	}
}
