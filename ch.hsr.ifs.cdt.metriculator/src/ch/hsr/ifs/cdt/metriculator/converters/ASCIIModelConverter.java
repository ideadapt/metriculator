package ch.hsr.ifs.cdt.metriculator.converters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class ASCIIModelConverter {

	protected AbstractNode node;
	protected AbstractMetric[] metrics;
	
	public void convert(AbstractNode node, AbstractMetric... metrics){
		this.node = node;
		this.metrics = metrics;
	}
	
	public String getResult(){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		TreePrinter.printTree(node, ps, metrics);
		return os.toString();
	}
}
