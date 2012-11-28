package ch.hsr.ifs.cdt.metriculator.views;

import java.util.Collection;

import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public abstract class FileExporter {
	
	Path export_location;
	Collection<AbstractMetric> metrics;
	AbstractNode root;

	public FileExporter(Path export_location, AbstractNode root, Collection<AbstractMetric> metrics){
		this.export_location = export_location;
		this.metrics = metrics;
		this.root = root;
	}
	
	public abstract void run();
}
