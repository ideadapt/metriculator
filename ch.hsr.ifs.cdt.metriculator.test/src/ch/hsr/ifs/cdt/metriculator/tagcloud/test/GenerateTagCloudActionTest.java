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
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDefNode;
import ch.hsr.ifs.cdt.metriculator.tagcloud.GenerateTagCloudAction;

/**
 * 
 */
public class GenerateTagCloudActionTest extends TestCase {

	private GenerateTagCloudAction action = null;
	
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
	
	private static String repeatString(String str, int times){
		return times == 0 ? "" : String.format("%0" + times + "d", 0).replace("0", str);
	}
	
	public void testGenerationWithLargeInputWorks(){
		
		Collection<AbstractNode> nodes = new ArrayList<AbstractNode>();
		AbstractMetric metric = new LSLOCMetric(null, "LSLOC", "LSLOC Descr");
		
		int randomValue = 1;
		Random random = new Random();
		String randomName = repeatString("123", 1);
		int nodeCount   = 400;
		
		for(int i = 0; i<nodeCount; i++){
						
			System.out.println(randomName + ", " +  randomValue);
			
			AbstractNode node = new FunctionDefNode(randomName);
			node.setNodeValue(metric.getKey(), randomValue);
			nodes.add(node);
						
			randomName = repeatString("123", i%10+1);
			randomValue = random.nextInt(100)+1;
		}
		try{
			action.generateTagCloud(nodes, metric);
		}catch(Exception ex){
			Assert.fail("test fails");
		}
	}
}