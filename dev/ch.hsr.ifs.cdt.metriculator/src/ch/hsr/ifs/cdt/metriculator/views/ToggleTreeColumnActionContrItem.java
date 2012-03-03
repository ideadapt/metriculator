package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.swt.widgets.TreeColumn;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;

public class ToggleTreeColumnActionContrItem extends ToggleColumnActionItem<TreeColumn> {

	public ToggleTreeColumnActionContrItem(TreeColumn column) {
		super(new ToggleTreeColumnAction(column));
	}

	@Override
	public void toggleVisibility() {
		if (((AbstractMetric) getColumn().getData(MetricColumn.DATAKEY_COLUMNMETRIC)).getChecker().hasEnabledProblems()) {
			MetricColumn.showColumn(getColumn());
			getAction().setChecked(true);
		} else {
			MetricColumn.hideColumn(getColumn());
			getAction().setChecked(false);
		}
	}
}
