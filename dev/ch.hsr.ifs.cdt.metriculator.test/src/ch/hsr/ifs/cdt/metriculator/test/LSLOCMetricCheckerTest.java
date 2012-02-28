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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeTypeNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see LSLOCMetricMetricChecker} class, validates the implementation of the USC Counting Standard rules
 * for logical source lines of code (LSLOC)
 * 
 * @see http://sunset.usc.edu/research/CODECOUNT/
 */
public class LSLOCMetricCheckerTest extends MetriculatorCheckerTestCase {

	private AbstractMetricChecker checker;
	private WorkspaceNode workspaceNode;
	private AbstractMetric metric;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		System.out.println(super.getName());
		
		enableProblems(LSLOCMetricChecker.LSLOC_PROBLEM_ID);

		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(LSLOCMetricChecker.class);
			metric = new LSLOCMetric(checker, "LSLOC", "lines of code");
		}
		MetriculatorPluginActivator.getDefault().resetTreeBuilders();
		workspaceNode = (WorkspaceNode) MetriculatorPluginActivator.getDefault().getHybridTreeBuilder().root;
	}
	
	@Override
	public void tearDown() throws CoreException {
		super.tearDown();
		
		TreePrinter.printTree(workspaceNode, metric);
	}

	// int main(){} // 1
	public void testBraceCountsOne() {
		loadCodeAndRun(getAboveComment());

		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
		
	//   int main(){
	//	   int i = 0;//1
	//     int ii;//2
	//     i = 1;//3
	//   }//4
	public void testVariableDeclarationOrDefinitionCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(4, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	// FOR -----------------------
	
	// int main()
	// {
	// for(;;){//1 for
	// }//1 compound
	// }//2 compound
	public void testEmptyForCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}

	// int main()
	// {
	// for(;;);//1 for
	// }//2 compound
	public void testEmptyNoBracesForCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// for(;;){//1 
	//   for(;;){//2 	
	//   }//2 	
	// }//2
	// }//3
	public void testEmptyNestingForCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}	
	
	// int main()
	// {
	// for(int i=0;i<0;++i){//1 for
	// }//1 compound
	// }//2 compound
	public void testForCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// for(int i=0;i<0;++i){//1 
	//   for(int i=0;i<0;++i){//2 
	//   }//2
	// }//2
	// }//3 
	public void testNestingForCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// for(int i=0;i<0;++i){//1 
	//   int a = 0;//2
	//   for(int i=0;i<0;++i){//3 
	//        a = 1;//4
	//   }//4
	// }//4
	// }//5 
	public void testNestingForWithContentCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(5, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	
	
	// WHILE -----------------------
	
	// int main()
	// {
	// while(true){//1 while
	// };//2
	// }//3
	public void testWhileWithSemicolonAndBracesCountsTwo() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// while(true){//1 while
	// }//1
	// }//2
	public void testEmptyWhileCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// while(true); //1
	// }//2
	public void testEmptyNoBracesWhileCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	
	
	// IF -----------------------
	
	// int main()
	// {
	// if(true){//1 for
	// }//1 compound
	// }//2 compound
	public void testEmptyIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true);//1 for
	// }//2 compound
	public void testEmptyNoBracesIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true){//1 
	//   if(true){//2 	
	//   }//2 	
	// }//2
	// }//3
	public void testEmptyNestingIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true)//1 
	//   if(true);//2
	// }//3
	public void testEmptyNoBraceNestingIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true && true || (true & false)){//1 for
	// }//1 compound
	// }//2 compound
	public void testIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true){//1 if
	// }else{//1 else
	// }//1 compound
	// }//2 compound
	public void testElseCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true){//1 if
	// }else;//1 else
	// }//2 compound
	public void testElseNoBracesCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main() {
	//   int i = true ? 0 : 1;//1
	// }//2
	public void testExpressionStatementCountsOne() {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// enum x{
	//   val1,
	//	 val2
	// } //1
	public void testEnumCountsOne(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int main()
	// {
	// if(true){//1 if
	// }else if(false){}//1 else, 2 if
	// }//3 compound
	public void testElseIfCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// DO WHILE -----------------------
	
	// int main()
	// {
	// do{}//1 do, 1 compound
	// while(true);// 1 some expressions
	// }//2 compound
	public void testDoWhileCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// int h()
	// {} //1
	// int h(int)
	// {} //2
	// const int h()
	// {} //3
	public void testOverloadedFunctionCountsTwo(){
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);		
	}
	
	// int h()
	// {} //1
	// int h(int)
	// {} //2
	// const int h()
	// {} //3
	public void testEachOverloadedFunctionResultInOneNode(){
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode
						.getChildren(ProjectNode.class).iterator().next()
							.getChildren(FileNode.class).iterator().next()
								.getChildren().size());		
	}
	
	
	// PREPROCESSOR STATEMENTS & DIRECTIVES -----------------------
	
	// using namespace std;//1
	// int main()
	// {
	// }//2 compound
	public void testUsingNamespaceCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	// using namespace std;//1
	// int main()
	// {
	//   using namespace std;//2
	// }//3 compound
	public void testFunctionNestsUsingNamespaceCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}

	// using namespace std;//1
	// namespace x{
	//   using namespace std;//2
	//   int main(){
	//   }//3
	// }//4 compound
	public void testNamespaceNestsUsingNamespaceCountsOne() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(3, (workspaceNode
							.getChildren(ProjectNode.class).iterator().next()
							.getChildren(FileNode.class).iterator().next()
							.getChildren(NamespaceNode.class).iterator().next())
								.getValueOf(metric).aggregatedValue);
	}
	
	// #include <iostream>;//1
	// int main()
	// {
	// 		#if //2
	//		#endif //3
	// }//4
	public void testPrecProcCounts() throws IOException {
		loadCodeAndRun(getAboveComment());

		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
	}
	
	
	//   int main(){
	//   }//1
	//	 struct y{
	//	 };//2
	public void testStructCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(0, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//   }//1
	//	 struct y{
	//     private:
	//     public:
	//	 };//2
	public void testStructAccessLabelIgnored(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(0, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//   }//1
	//	 typedef struct y{
	//	 };//2
	public void testTypedefStructCountsTwo(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(0, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//   }//1
	//	 struct y{
	//	 }Y;//3
	public void testNamedStructCountsTwo(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(0, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//   }//1
	//	 typedef struct y{
	//	 }Y;//3
	public void testNamedTypedefStructCountsTwo(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
		
		assertEquals(0, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//   }//1
	//	 typedef int I;//2
	public void testTypedefCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getValueOf(metric).nodeValue);
	}
	
	//   int main(){
	//	   switch(true && true){//1
	//	   }
	//   }//2
	public void testSwitchCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   switch(true && true){//1
	//	   case false:
	//		break;//2
	//   }//3
	public void testBreakCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(3, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   switch(true && true){//1
	//	   case false:
	//		cout << "";//2
	//		break;//3
	//   }//4
	public void testExpressionInCaseCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(4, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   switch(true && true){//1
	//	   case false:
	//	   default:
	//      break;//2
	//   }//3
	public void testCaseAndDefaultIgnored(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(3, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}	
	
	//   int main(){
	//	   auto f = (int x, int y) {
	//              };//1 compound
	//   }//2
	public void testEmptyLambdaWithoutCaptureCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   auto f = [z](int x, int y) {//1
	//              };//2
	//   }//3
	public void testLambdaWithCaptureCountsTwo(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(3, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   auto f = [z, a](int x, int y) {//1
	//              };//2
	//   }//3
	public void testLambdaWithTwoCapturesCountsTwo(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(3, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(3, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   auto f = [z](int x, int y) {//1
	//                 return x + y;//2
	//              };//3
	//   }//4
	public void testStatementInLambdaCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(4, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//   int main(){
	//	   try{//1
	//	   }catch(exception &x){//2
	//	   }catch(exception &x){//3
	//	   }
	//   }//4
	public void testTryAndCatchBlockEachCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(4, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(4, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//	template<class xy> class Array {
	//	};//1
	public void testTemplateDeclarationCountsOne(){
		loadCodeAndRun(getAboveComment());		
		
		assertEquals(1, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(1, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(CompositeTypeNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//	template <typename T, int Groesse>
	//	T &Array<T, Groesse>::getObjekt(int Index)
	//	{
	//	  return objekte[Index];//1
	//	}//2
	public void testFunctionTemplateDeclarationCountsTwo(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(2, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(2, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(FunctionNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
	}
	
	//	namespace myNS2{		
	//		int doIt(){			
	//			int i = 0;		// 1
	//			return 1;		// 2
	//		}	// 3
	//		namespace myNS21{	 
	//			int doIt(){	 
	//				return 0;	// 4
	//			}	//5
	//		}	//6
	//	}	//7
	public void testNestedNamespaces(){
		loadCodeAndRun(getAboveComment());
		
		assertEquals(7, workspaceNode.getValueOf(metric).aggregatedValue);
		assertEquals(7, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(NamespaceNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue);
		assertEquals(3, workspaceNode
				.getChildren(ProjectNode.class).iterator().next()
				.getChildren(FileNode.class).iterator().next()
				.getChildren(NamespaceNode.class).iterator().next()
				.getChildren(NamespaceNode.class).iterator().next()
				.getValueOf(metric).aggregatedValue); 
	}
}
