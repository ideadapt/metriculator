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
import ch.hsr.ifs.cdt.metriculator.checkers.NumberMembersMetricChecker;
import ch.hsr.ifs.cdt.metriculator.checkers.NumberMembersMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see NumberMembersChecker} class, validates the implementation for Number Members per Type.
 */
public class NumberMembersCheckerTest extends MetriculatorCheckerTestCase {
	private NumberMembersMetricChecker checker;
	private WorkspaceNode workspaceNode;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println(super.getName());

		enableProblems(NumberMembersMetricChecker.NBMEMBERS_PROBLEM_ID);

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(NumberMembersMetricChecker.class);
			metric = new NumberMembersMetric(checker, "NbMembers", "number of type members");
		}
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
	}

	@Override
	public void tearDown() throws CoreException {
		super.tearDown();

		TreePrinter.printTree(workspaceNode, metric);
	}

	//	struct X{
	//		int dataMember1;
	//	};
	public void testOneDataMemberInStruct(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers : public SuperClassMembers {
	//		public:
	//		protected:
	//		private:
	//	};
	public void testDoNotCountVisibilityLabels(){
		loadCodeAndRun(getAboveComment());

		assertEquals(0, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	struct X{
	//		int dataMember1;
	//		int dataMember2;
	//	};
	public void testTwoDataMembersInStruct(){
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//		private:
	//			MyClassMembers(int value);
	//	};
	public void testOnePrivateDataMemberInClass(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);	
	}

	//	class MyClassMembers{
	//	public:
	//		MyClassMembers(int value);
	//	};
	public void testOnePublicDataMemberInClass(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	protected:
	//		MyClassMembers(int value);
	//	};
	public void testOneProtectedDataMemberInClass(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);	
	}

	//	union UnionMember{
	//		int dataMember1;
	//	};
	public void testOneDataMemberInUnion(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	struct X{
	//		static int dataMember1;
	//		int dataMember2;
	//	};
	public void testStaticMember(){
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		class InnerClassMember;
	//	};
	public void testNestedClassMember(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		struct InnerStructMember;
	//	};
	public void testNestedStructMember(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		union InnerUnionMember;
	//	};
	public void testNestedUnionMember(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		enum { a, b, c=0 };
	//	};
	public void testAnonymousEnum(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		enum colorMember14 { red, yellow, green=20, blue };
	//	};
	public void testEnum(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		struct InnerClassMember12;
	//		typedef int ganzZahl;
	//		typedef struct InnerClassMember12 X;	
	//	};
	public void testTypeDefDeclaration(){
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		class InnerClassMember10{
	//			struct InnerInnerStructMember11;
	//
	//		};
	//	};
	public void testNestedInNestedType(){
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		class InnerClassMember1{
	//			int dataMember2;
	//			struct InnerInnerStructMember3;
	//	
	//		};
	//	};
	public void testMembersOfNestedTypes(){
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		class InnerClassMember1{
	//			int dataMember2;
	//			struct InnerInnerStructMember3{
	//				int dataMember4;
	//			};
	//	
	//		};
	//	};
	public void testMembersOfNestedTypesInNesting(){
		loadCodeAndRun(getAboveComment());

		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers : public SuperClassMembers {
	//		public:
	//			using SuperClassMembers::superMemberFunction2;
	//			using SuperClassMembers::superMember1;
	//	};
	public void testDoNotCountUsingDeclarations(){
		loadCodeAndRun(getAboveComment());

		assertEquals(0, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//		private:
	//	};
	//	int noMemberFunctions(){
	//		return 1;
	//	}
	public void testDoNotCountGlobalFunctions(){
		loadCodeAndRun(getAboveComment());

		assertEquals(0, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		MyClassMembers(int value);
	//		friend void func();
	//	};
	public void testDoNotCountFriendFunctionDeclaration(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		MyClassMembers(int value);
	//		friend int func( MyClassMembers*, int){
	//			return 1;
	//		}
	//	};
	public void testDoNotCountFriendFunctionDefinition(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	class MyClassMembers{
	//	public:
	//		MyClassMembers(int value);
	//		friend class MyClass;
	//	};
	public void testDoNotCountFriendClass(){
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	//	class MyClassMembers{
	//	public:
	//		MyClassMembers(int value);
	//		class Friendfriend{
	//			int friend;
	//		}
	//		int friendFriend;
	//		int funcFriendfriend(int i){
	//			return 1;
	//		}
	//	};
	public void testCountMembersCalledFriend(){
		loadCodeAndRun(getAboveComment());

		assertEquals(5, workspaceNode.getValueOf(metric).aggregatedValue);
	}
}
