package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public interface ITagCloudDataProvider {

	public abstract AbstractMetric getMetric();

	public abstract Collection<AbstractNode> getNodes();
}