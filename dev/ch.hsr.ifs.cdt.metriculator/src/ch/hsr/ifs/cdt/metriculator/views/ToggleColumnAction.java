package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * T is either TreeColumn or TableColumn
 * */
public abstract class ToggleColumnAction<T extends Item> extends Action {

	public T column;
	
	protected ToggleColumnAction(T col) {
		column = col;
	}

	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public String getText() {
		return column.getText();
	}
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