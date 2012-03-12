/******************************************************************************
* Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik 
* Rapperswil, University of applied sciences and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html 
*
* Contributors:
* 	Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
******************************************************************************/

package ch.hsr.ifs.cdt.metriculator.tagcloud.test;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.tagcloud.GenerateTagCloudAction;

/**
 * 
 */
public class GenerateTagCloudActionTest extends TestCase {

	private GenerateTagCloudAction action;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		System.out.println(getName());
		action = new GenerateTagCloudAction();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGenerationWithLargeInputWorks(){
		
		Collection<AbstractNode> nodes = new ArrayList<AbstractNode>();
		AbstractMetric metric = new LSLOCMetric(null, "LSLOC", "LSLOC Descr");
		
		int randomValue = 2;
		String randomName = "jlijijijijijijijijijijijiiji";
		int nodeCount   = 1000;
		
		for(int i = 0; i<nodeCount; i++){
			
			AbstractNode node = new FunctionNode(randomName);
			node.setNodeValue(metric.getKey(), randomValue);
			nodes.add(node);
		}
		
		action.generateTagCloud(nodes, metric);
	}
}