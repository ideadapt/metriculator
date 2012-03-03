package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Item;

/**
 * T is either TreeColumn or TableColumn
 * */
public abstract class ToggleColumnAction<T extends Item> extends Action {

	public T column;
	
	public ToggleColumnAction(T col) {
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
