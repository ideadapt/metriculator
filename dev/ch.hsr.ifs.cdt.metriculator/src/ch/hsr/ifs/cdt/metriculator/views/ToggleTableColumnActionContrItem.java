package ch.hsr.ifs.cdt.metriculator.views;


import org.eclipse.swt.widgets.TableColumn;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;

public class ToggleTableColumnActionContrItem extends ToggleColumnActionItem<TableColumn>{

	public ToggleTableColumnActionContrItem(TableColumn column) {
		super(new ToggleTableColumnAction(column));
	}

	@Override
	public void toggleVisibility() {
		if(((AbstractMetric) getColumn().getData(MetricColumn.DATAKEY_COLUMNMETRIC)).getChecker().hasEnabledProblems()){
			MetricColumn.showColumn(getColumn());
			getAction().setChecked(true);
		}else{
			MetricColumn.hideColumn(getColumn());
			getAction().setChecked(false);
		}
	}

}
