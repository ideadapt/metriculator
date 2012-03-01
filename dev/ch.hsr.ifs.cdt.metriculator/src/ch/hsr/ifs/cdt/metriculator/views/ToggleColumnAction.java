package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeColumn;

public class ToggleColumnAction<T extends Item> extends Action {

	public T column;
	
	public ToggleColumnAction(T col) {
		column = col;
	}
	
	private boolean isChecked = true;
	
	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public String getText() {
		return column.getText();
	}

	@Override
	public void run() {
		isChecked = !isChecked;
		if(isChecked){
			if(column instanceof TreeColumn){
				MetricColumn.showColumn((TreeColumn)column);
			}
		}else{
			if(column instanceof TreeColumn){
				MetricColumn.hideColumn((TreeColumn)column);
			}
		}
		setChecked(isChecked);
	}
}
