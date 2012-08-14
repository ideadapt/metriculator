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

package ch.hsr.ifs.cdt.metriculator.tests;

import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.checkers.EfferentCouplingMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.EfferentCouplingMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see EfferentCouplingMetricChecker} class, validates the implementation for type efferent coupling.
 */
public class EfferentCouplingMetricCheckerTest extends MetriculatorCheckerTestCase {

	private AbstractMetricChecker checker;
	private WorkspaceNode workspaceNode;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println(super.getName());
		
		enableProblems(EfferentCouplingMetricChecker.EFFERENTCOUPLING_PROBLEM_ID);

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(EfferentCouplingMetricChecker.class);
			metric = new EfferentCouplingMetric(checker, "ECoupling", "Efferent Coupling");
		}
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
	}
	
	@Override
	public void tearDown() throws CoreException {
		super.tearDown();
		
		TreePrinter.printTree(workspaceNode, metric);
	}

	// class X;
	public void testClassDeclCountsZero() {
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(0, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// class X {}
	public void testClassDefCountsZero() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(0, workspaceNode.getValueOf(metric).aggregatedValue);
	}	

	// class Z;
	// class X {
	//	Z a;
	// }	
	public void testClassRefCountsOne() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// class Z;
	// class X {
	//	Z a;
	// }	
	public void testClassInitCountsOne() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}	
	
	// class Z;
	// class X {
	//  void fn(){
	//		Z *a;
	//	}
	// }	
	public void testClassInitInFunctionCountsOne() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}	
	
	// class Z;
	// class X {
	//	Z a;
	//  class XX{
	//		Z aa;
	//	}
	// }	
	public void testNestedClassHasSeparateCounting() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		// class Z
		assertEquals(0, workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().iterator().next().getValueOf(metric).aggregatedValue);
		
		// class X 
		assertEquals(2,  ((AbstractNode)workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().toArray()[1]).getValueOf(metric).aggregatedValue);	
		
		// class X 
		assertEquals(1,  ((AbstractNode)workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().toArray()[1]).getValueOf(metric).nodeValue);
		
		// class XX 
		assertEquals(1,  ((AbstractNode)workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().toArray()[1])
									.getChildren().iterator().next().getValueOf(metric).aggregatedValue);	
		
		// class XX 
		assertEquals(1,  ((AbstractNode)workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().toArray()[1])
									.getChildren().iterator().next().getValueOf(metric).nodeValue);			
	}
	
	// class X {
	// 	class Z;
	// }	
	public void testNestedClassForwardDeclCountsOne() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
		
		// class X
		assertEquals(1, workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().iterator().next().getValueOf(metric).aggregatedValue);		
	}	
	
	// class Z;
	// class X {
	// 	class XX{}
	//  class Z;
	// }	
	public void testScopingWorksOnNestedTypes() {
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);

		// class X 
		assertEquals(1,  ((AbstractNode)workspaceNode.getChildren().iterator().next()
									.getChildren().iterator().next()
									.getChildren().toArray()[1]).getValueOf(metric).aggregatedValue);		
	}	
}
