package ch.hsr.ifs.cdt.metriculator.model.converters;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public interface IModelConverter<R> {
	
	public abstract void convert(AbstractNode node, AbstractMetric... metrics);
	
	public abstract R getResult();

}
