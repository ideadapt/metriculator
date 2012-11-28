package ch.hsr.ifs.cdt.metriculator.model.converters;

import java.util.Collection;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public interface IModelConverter<R> {
	
	public abstract void convert(AbstractNode node, Collection<AbstractMetric> metrics);
	
	public abstract R getResult();

}
