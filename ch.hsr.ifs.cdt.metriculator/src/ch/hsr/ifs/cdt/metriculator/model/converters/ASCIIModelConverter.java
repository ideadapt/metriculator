package ch.hsr.ifs.cdt.metriculator.model.converters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class ASCIIModelConverter implements IModelConverter<String> {
	
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	
	@Override
	public void convert(AbstractNode node, Collection<AbstractMetric> metrics) {
		TreePrinter.printTree(node, new PrintStream(os), (AbstractMetric[]) metrics.toArray(new AbstractMetric[0]));
	}
	
	public String getResult(){
		return os.toString();
	}
}
