package ch.hsr.ifs.cdt.metriculator.model.test;

import junit.framework.TestSuite;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.internal.index.tests.IndexCPPBindingResolutionTest;

@SuppressWarnings("restriction")
public class IndexBindingTests extends IndexCPPBindingResolutionTest {
	
//	TestSuite suite = new TestSuite(IndexBindingTest.class);
//	IndexCPPBindingResolutionTest.SingleProject proj = new IndexCPPBindingResolutionTest.SingleProject();
//	IndexCPPBindingResolutionTest
	
	public IndexBindingTests() {
		// TODO Auto-generated constructor stub
//		addTests(new TestSuite(IndexBindingTests.class));
		
//		IndexCPPBindingResolutionTest.SingleProject proj = new IndexCPPBindingResolutionTest.SingleProject();
//		proj.countTestCases();
		
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
		IBinding tdAST = getBindingFromASTName("forwardFunc;", 11);
		assertFalse(tdAST instanceof IIndexBinding);
		IBinding tdIndex= getIndex().adaptBinding(tdAST);
		assertTrue(tdIndex instanceof IIndexBinding);	
	}

}
