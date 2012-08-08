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

package ch.hsr.ifs.cdt.metriculator.test;

import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.checkers.NumberParamsMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.NumberParamsMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see NumberParamsChecker} class, validates the implementation for Number Params per Function.
 */
public class NumberParamsCheckerTest extends MetriculatorCheckerTestCase {
	private NumberParamsMetricChecker checker;
	private WorkspaceNode workspaceNode;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println(super.getName());

		enableProblems(NumberParamsMetricChecker.NBPARAMS_PROBLEM_ID);

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(NumberParamsMetricChecker.class);
			metric = new NumberParamsMetric(checker, "NbParams", "number of parameters");
		}
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
	}



	@Override
	public void tearDown() throws CoreException {
		super.tearDown();

		TreePrinter.printTree(workspaceNode, metric);
	}

	//	int oneParam(int i){
	//		return i;
	//	}
	public void testOneParameter(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	int twoParam(int i, int j){
	//		return i + j;
	//	}
	public void testTwoParameters(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	int twoParam(int i, int j){
	//		return i + j;
	//	}
	//
	//	int threeParam(int i, bool b, long l){
	//		return 0;
	//	}
	public void testTwoFunctionsWithParameters(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(5, workspaceNode.getValueOf(metric).aggregatedValue);
	}


	//	class MyClass {
	//	public:
	//		MyClass();
	//		virtual ~MyClass();
	//		int JulesIndex(int);
	//	};
	//
	//	int MyClass::JulesIndex(int i){
	//		return 100;
	//	}
	public void testFunctionDefinitionOverwritesDeclaration(){
		loadcode(getAboveComment());
		runOnProject();
		
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}
}
