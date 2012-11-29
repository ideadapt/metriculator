/******************************************************************************
* Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik 
* Rapperswil, University of applied sciences and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html 
*
* Contributors:
* 	Ueli Kunz <kunz@ideadapt.net> - initial API and implementation
******************************************************************************/

package ch.hsr.ifs.cdt.metriculator.model;

import java.io.PrintStream;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class TreePrinter {

	public static final int labelColumnWidth = 30;
	public static final int valueColumnWidth = 15;
	
	private static void printTree(AbstractNode node, int level, PrintStream out, AbstractMetric... metrics){
		level                = Math.max(0, level);
		String nodeLabel     = node.toString();
		int labelColumnFillCount  = labelColumnWidth - 2*level - nodeLabel.length();
		String metricColumnValues = generateMetricColumns(node, metrics);
		
		if(labelColumnFillCount < 0){
			nodeLabel = nodeLabel.substring(Math.max(0, -labelColumnFillCount), nodeLabel.length());
		}
		labelColumnFillCount = Math.max(0, labelColumnFillCount);
		
		if(level == 0){
			out.printf("[metric columns:");
			for(AbstractMetric metric : metrics){
				out.printf(" '%s'", metric.getName());
			}
			out.printf("]");
		}
		
		out.printf("\n%s %s %s %s", 
					repeatString("  ", level), 
					nodeLabel,
					repeatString(" ", labelColumnFillCount),
					metricColumnValues
				);
		
		for(AbstractNode n : node.getChildren()){
			printTree(n, level + 1, out, metrics);
		}
	}
	
	private static String generateMetricColumns(AbstractNode node, AbstractMetric... metrics) {
		
		StringBuilder out = new StringBuilder();
		
		for(AbstractMetric metric : metrics){
			int aggregatedValue = node.getValueOf(metric).aggregatedValue;
			int nodeValue       = node.getValueOf(metric).nodeValue;
			
			out.append(String.format("%s (%s)%s", 
					aggregatedValue, 
					nodeValue,
					repeatString(" ", valueColumnWidth - (aggregatedValue + "" + nodeValue).length())
					));
		}
		
		return out.toString();
	}

	public static void printTree(AbstractNode node, AbstractMetric... metricsToPrint){
		printTree(node, 0, System.out, metricsToPrint);
		System.out.println("\n----------");
	}
	
	public static void printTree(AbstractNode node, PrintStream out, AbstractMetric... metricsToPrint){
		printTree(node, 0, out, metricsToPrint);
		out.println("\n----------");
	}
	
	private static String repeatString(String str, int times){
		return times == 0 ? "" : String.format("%0" + times + "d", 0).replace("0", str);
	}
}
