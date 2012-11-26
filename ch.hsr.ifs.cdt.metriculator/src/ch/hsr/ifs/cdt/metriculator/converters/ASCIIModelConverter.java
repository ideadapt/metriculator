package ch.hsr.ifs.cdt.metriculator.converters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class ASCIIModelConverter implements IModelConverter<String> {
	
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	
	public void convert(AbstractNode node, AbstractMetric... metrics){
		
		TreePrinter.printTree(node, new PrintStream(os), metrics);
	}
	
	public String getResult(){
		return os.toString();
	}
}
