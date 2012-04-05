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

package ch.hsr.ifs.cdt.metriculator.model.test;

import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;
import ch.hsr.ifs.cdt.metriculator.test.MetriculatorCheckerTestCase;

public class TreeBuilderIndexerTest extends MetriculatorCheckerTestCase {
	AbstractNode root;
	private AbstractMetricChecker checker;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		System.out.println(getName());
		root = new WorkspaceNode("rootnotmodified");

		enableProblems(LSLOCMetricChecker.LSLOC_PROBLEM_ID);
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(LSLOCMetricChecker.class);
			metric = new LSLOCMetric(checker, "LSLOC", "lines of code");
		}
	}

	@Override
	public void tearDown() throws CoreException {
		super.tearDown();

		TreePrinter.printTree(root, metric);
	}

	
	//	namespace {
	//	}
	//
	//	namespace {
	//	}
	public void testCreateHybridWithAnonymousAstNodesOnSameLevel(){ 		
		loadCodeAndRun(getAboveComment());
		
		root = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
		
		AbstractNode file1 = root.getChildren().iterator().next().getChildren().iterator().next();
		assertEquals(2, file1.getChildren().size());
	}
	
	//	namespace N {
	//	}
	//
	//	namespace N {
	//	}
	public void testCreateHybridWithAstNodesOfSameNameOnSameLevel(){		
		loadCodeAndRun(getAboveComment());
		
		root = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
		
		AbstractNode file1 = root.getChildren().iterator().next().getChildren().iterator().next();
		assertEquals(2, file1.getChildren().size());
	}	
	
	//	namespace N {
	//	}
	//
	//	namespace N {
	//	}
	public void testCreateLogicAstNodesWithSameNameOnSameLevel(){ 		
		loadCodeAndRun(getAboveComment());
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
		
		assertEquals(1, root.getChildren().size());
	}	
	
	//	namespace {
	//	}
	//
	//	namespace {
	//	}
	public void testCreateLogicWithAnonymousAstNodesOnSameLevel(){ 		
		loadCodeAndRun(getAboveComment());
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
					
		assertEquals(2, root.getChildren().size());
	}

	//	class MyClass {
	//	public:
	//		MyClass();
	//		virtual ~MyClass();
	//		int JulesIndex();
	//	};
	//
	//	int MyClass::JulesIndex(){
	//		return 100;
	//	}
	public void testMergeOfMemberFunctions(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(3, root.getChildren().iterator().next().getChildren().size());
	}

	//	class MyClass {			
	//	public:
	//		MyClass(){
	//			int i = 1;
	//		}
	//		virtual ~MyClass();
	//		int JulesIndex();
	//	};
	//	
	//	MyClass::~MyClass() {
	//	}
	//
	//	int MyClass::JulesIndex(){
	//		return 100;
	//	}
	public void testMergeOfMemberFunctionsWithInternDefinition(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(3, root.getChildren().iterator().next().getChildren().size());

	}

	//	class MyClass {			
	//	public:
	//		MyClass(){
	//			int i = 1;
	//		}
	//		virtual ~MyClass();
	//		int JulesIndex(int i);
	//	};
	//	
	//	MyClass::~MyClass() {
	//	}
	//
	//	int MyClass::JulesIndex(int i){
	//		return 100;
	//	}
	public void testMergeOfMemberFunctionsWithInternDefinition2(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(3, root.getChildren().iterator().next().getChildren().size());

	}

	//	class MyClass {			
	//	public:
	//		MyClass(){
	//			int i = 1;
	//		}
	//		virtual ~MyClass();
	//		int JulesIndex(int);
	//	};
	//	
	//	MyClass::~MyClass() {
	//	}
	//
	//	int MyClass::JulesIndex(int i){
	//		return 100;
	//	}
	public void testMergeOfMemberFunctionsWithInternDefinition3(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(3, root.getChildren().iterator().next().getChildren().size());

	}

	//	namespace testOwner{
	//		class Laser {
	//		public:
	//			Laser();
	//			virtual ~Laser();
	//		};
	//	}
	//	
	//	namespace testOwner{
	//		Laser::Laser() {
	//		}
	//		Laser::~Laser() {
	//		}
	//	}	
	public void testMergOfMemberFunctionsInNamespace(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(2, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
	}

	//int forwardFunc(int);
	//
	//int forwardFunc(int i){
	//	return 0;
	//}
	//
	//int main(){
	//	return forwardFunc(1);
	//}
	public void testMergOfFunctionDefinitionAndDeclarationInSameFile1(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
		
		assertEquals(2, root.getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof FunctionDefNode);
		assertEquals(0, root.getChildren().iterator().next().getChildren().size());
	}

	//int forwardFunc(int i);
	//
	//int forwardFunc(int i){
	//	return 0;
	//}
	//
	//int main(){
	//	return forwardFunc(1);
	//}
	public void testMergOfFunctionDefinitionAndDeclarationInSameFile2(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
		
		assertEquals(2, root.getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof FunctionDefNode);
		assertEquals(0, root.getChildren().iterator().next().getChildren().size());
	}
	
	//	class A;
	//	class B;
	//	class A{
	//	};
	//	class B{
	//	};
	public void testMergOfTypeDefinitionAndDeclarationInSameFile(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
		
		assertEquals(2, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		assertTrue(root.getChildren().iterator().next().getChildren().iterator().next().getChildren().iterator().next() instanceof TypeDefNode);
	}

	//	class MyClass {			
	//	public:
	//		MyClass(){
	//			int i = 1;
	//		}
	//		virtual ~MyClass();
	//		struct InnerStruct;
	//	};
	//	
	//	MyClass::~MyClass() {
	//	}
	//	struct MyClass::InnerStruct{
	//	};
	public void testMergeOfNestedTypeDeclarationAndDefition(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(3, root.getChildren().iterator().next().getChildren().size());
	}

	//	namespace N {
	//		struct A {
	//		    void fx() { }
	//		};
	//	}
	//
	//	namespace N {
	//		struct B {
	//		    void f() { }
	//		    void f1() { }
	//		};
	//	}
	public void testMergeOfFunctionsInNamespaces(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(2, root.getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
	}
	
	//	namespace {
	//		struct A {
	//		    void fx() { }
	//		};
	//	}
	//
	//	namespace {
	//		struct B {
	//		    void f() { }
	//		    void f1() { }
	//		};
	//	}
	public void testTwoAnonymousNamespaces(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(2, root.getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
	}
	
	//	namespace {
	//		struct A {
	//		    void fx();
	//		};
	//	}
	//	void A::fx(){}
	public void testAnonymousNamespaceMemberMergingOutsideNamespace(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
		assertEquals(2, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
		
		assertEquals(1, root.getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		
	}
	
	//	namespace {
	//		struct A {
	//		    void fx();
	//		};
	//		void A::fx(){}
	//	}
	public void testAnonymousNamespaceMemberMerging(){
		loadcode(getAboveComment());
		runOnProject();
		
		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;
		
		assertEquals(1, root.getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().size());
		assertEquals(1, root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
	}
	
	//	namespace Outer { // at depth 0
	//		namespace {   
	//			struct A {
	//		    	void fx();
	//			};
	//          void A::fx(){}
	//		}
	//	}
	public void testNestedAnonymousNamespaceMemberMerging(){
		loadcode(getAboveComment());
		runOnProject();

		root = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(1, root.getChildren().size());
		assertEquals(1, getFirstChildInDepth(root, 1).getChildren().size());
		assertEquals(1, getFirstChildInDepth(root, 2).getChildren().size());
		assertTrue(getFirstChildInDepth(root, 3) instanceof FunctionDefNode);
	}	
}
