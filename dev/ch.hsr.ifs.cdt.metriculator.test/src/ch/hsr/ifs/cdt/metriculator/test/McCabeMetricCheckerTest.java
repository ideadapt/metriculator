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
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see McCabeMetricChecker} class, validates the implementation for McCabe.
 */
public class McCabeMetricCheckerTest extends MetriculatorCheckerTestCase {

	private AbstractMetricChecker checker;
	private WorkspaceNode workspaceNode;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println(super.getName());

		enableProblems(McCabeMetricChecker.MCCABE_PROBLEM_ID);

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(McCabeMetricChecker.class);
			metric = new McCabeMetric(checker, "McCabe", "CC");
		}
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
	}



	@Override
	public void tearDown() throws CoreException {
		super.tearDown();

		TreePrinter.printTree(workspaceNode, metric);
	}

	//   int main(){
	//	   int i = 0;
	//     if(i==0){
	//       return -1;
	//     }
	//     return 0;
	//   }
	public void testOneIf(){
		loadcode(getAboveComment());
		runOnProject();


		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   int i = 0;
	//     if(i==0){
	//       return -1;
	//     }
	//     if(i==1){
	//       return -1;
	//     }
	//     return 0;
	//   }
	public void testTwoIfs(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   int i = 0;
	//     if(i==0){
	//     	if(i==0){
	//       return -1;
	//      }
	//    }
	//     return 0;
	//   }
	public void testTwoNestedIfs(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		
	}

	// int main(){
	//     while(true); 
	// }
	public void testOneWhile(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	// int main()
	// {
	// for(;;){
	// }
	// }
	public void testOneFor(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	//	int main() {
	//		bool b = false;
	//		switch (b) {
	//			case true:
	//				break;
	//			default:
	//				break;
	//		}
	//	}
	public void testOneCase(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);		
	}

	//   int main(){
	//	   try{
	//	   }catch(exception &x){
	//	   }
	//   }
	public void testOneCatch(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);		
	}
	
	
	//   int main(){
	//	   int i = getNumber();
	//     if(i==0){
	//       return -1;
	//     }
	//     return 0;
	//   }
	//	
	//	int getNumber(){
	//		int i = 0;
	//		if(i==0){
	//			return -1
	//		}
	//		return 1;
	//	}
	public void testTwoFunctions(){
		loadcode(getAboveComment());
		runOnProject();

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	//	#if a<0
	//	#endif
	public void testPreprocessorIfStatement(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);		
	}

	//	#if a<0
	//	#endif
	//	#if b<0
	//	#endif
	public void testTwoPreprocessorIfStatements(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);		
	}
	
	//	#if a<0
	//	#elif a > 0
	//	#endif
	public void testPreprocessorElifStatement(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	//	#ifdef __cplusplus
	//	#endif
	public void testPreprocessorIfdefStatement(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	
	//	#ifndef __cplusplus
	//	#else
	//	#endif
	public void testPreprocessorIfndefStatement(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	
	//	int main(){
	//		int i = 0;
	//		return i == 0 ? -1 : 1;
	//	}
	public void testConditionalExpression(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	//	int main(){
	//		int i = 0;
	//		if(i == 1 || i == 0 )
	//			return 1;
	//		return 0;
	//	}
	public void testLogicalOrExpression(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	//	int main(){
	//	int i = 0;
	//	int j = 0;
	//	if(i == 1 && j == 0 )
	//		return 1;
	//	return 0;
	//	}
	public void testLogicalAndExpression(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
	//	int main(){	// 1
	//		int i = 0;
	//		int j = 0;
	//		if((i == 0 && j == 0) || i == 0 ) // 4
	//			return i == 0 ? -1 : 1; // 5
	//		return 1;
	//	}
	public void testMultipleExpressions(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(5, workspaceNode.getValueOf(metric).aggregatedValue);	
	}
	
//    bool operator&&(const Boolean &right) const
//    {
//      return this->value && right.value;
//    }
	public void testIgnoreOverloadedExpression(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	
	
	//namespace test {
	//   int main(){
	//	   int i = getNumber();
	//     if(i==0){
	//       return -1;
	//     }
	//     return 0;
	//   }
	//	
	//	int getNumber(){
	//		int i = 0;
	//		if(i==0){
	//			return -1
	//		}
	//		return 1;
	//	}
	//}
	public void testTwoFunctionsInNamespace(){
		loadcode(getAboveComment());
		runOnProject();
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	//	class MyClass {
	//	public:
	//		MyClass(){	
	//			int i = 0;
	//		}
	//		virtual ~MyClass();
	//		int func1();
	//		int func2();
	//	};
	//	
	//	int MyClass::func1(){
	//		if(true)
	//		return 100;
	//	}
	//	int MyClass::func2(){
	//		if(true)
	//		return 100;
	//	}
	public void testClassWithMemberfunctions(){
		loadcode(getAboveComment());
		runOnProject();
		
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getLogicTreeBuilder().root;

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);		
	}
	
}
