package ch.hsr.ifs.cdt.metriculator.model.test;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.testplugin.CProjectHelper;
import org.eclipse.cdt.core.testplugin.CTestPlugin;
import org.eclipse.cdt.core.testplugin.TestScannerProvider;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNameBase;
import org.eclipse.cdt.internal.index.tests.IndexBindingResolutionTestBase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

//@SuppressWarnings("restriction")
//public class IndexBindingTests extends TestSuite {
//	
//	public static Test suite() {
//		TestSuite suite = new IndexBindingTests();
//		
//		suite.addTest(IndexBindingTest.suite());
//		
//		return suite;
//	}
////	
//////	TestSuite suite = new TestSuite(IndexBindingTest.class);
//////	IndexCPPBindingResolutionTest.SingleProject proj = new IndexCPPBindingResolutionTest.SingleProject();
//////	IndexCPPBindingResolutionTest
////	
////	public IndexBindingTests() {
////		// TODO Auto-generated constructor stub
//////		addTests(new TestSuite(IndexBindingTests.class));
////		
//////		IndexCPPBindingResolutionTest.SingleProject proj = new IndexCPPBindingResolutionTest.SingleProject();
//////		proj.countTestCases();
////		
////	}
//}
public class IndexBindingTest extends IndexBindingResolutionTestBase {
	
//	public static TestSuite suite() {
//		return suite(IndexBindingTest.class);
//	}
	
//	public static class SingleProject extends IndexCPPBindingResolutionTest {
//		public SingleProject() {setStrategy(new SinglePDOMTestStrategy(true));}
//		public static TestSuite suite() {return suite(SingleProject.class);}
//	}
	
	@Override
	protected void setUp() throws Exception {
//		super.setUp();
		CPPASTNameBase.sAllowRecursionBindings = false;
		CPPASTNameBase.sAllowNameComputation   = false;

		strat = new MySinglePDOMTestStrategy(true);
		strat.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestScannerProvider.clear();
	}
	
	@Override
	protected IASTName findName(String section, int len) {
		IASTTranslationUnit ast = strat.getAst();
		final IASTNodeSelector nodeSelector = ast.getNodeSelector(null);
		final int offset = strat.getTestData()[1].indexOf(section);
		IASTName name= nodeSelector.findName(offset, len);
		if (name == null)
			name= nodeSelector.findImplicitName(offset, len);
		
		return name;
	}
	
	@Override
	protected IIndex getIndex() {
		return strat.getIndex();
	}

	class MySinglePDOMTestStrategy implements IMyTestStrategy {
		private IIndex index;
		private ICProject cproject;
		private StringBuffer[] testData;
		private IASTTranslationUnit ast;
		private boolean cpp;
	
		public MySinglePDOMTestStrategy(boolean cpp) {
			this.cpp = cpp;
		}
	
		public ICProject getCProject() {
			return cproject;
		}
		
		public StringBuffer[] getTestData() {
			return testData;
		}
	
		public IASTTranslationUnit getAst() {
			return ast;
		}
	
		public void setUp() throws Exception {
			cproject = cpp ? CProjectHelper.createCCProject(getName()+System.currentTimeMillis(), "bin", IPDOMManager.ID_NO_INDEXER) 
					: CProjectHelper.createCProject(getName()+System.currentTimeMillis(), "bin", IPDOMManager.ID_NO_INDEXER);
			Bundle b = CTestPlugin.getDefault().getBundle();
			testData = TestSourceReader.getContentsForTest(b, "parser", IndexBindingTest.this.getClass(), getName(), 2);
	
			IFile file = TestSourceReader.createFile(cproject.getProject(), new Path("header.h"), testData[0].toString());
			CCorePlugin.getIndexManager().setIndexerId(cproject, IPDOMManager.ID_FAST_INDEXER);
			assertTrue(CCorePlugin.getIndexManager().joinIndexer(360000, new NullProgressMonitor()));
	
			IFile cppfile= TestSourceReader.createFile(cproject.getProject(), new Path("references.c" + (cpp ? "pp" : "")), testData[1].toString());
			assertTrue(CCorePlugin.getIndexManager().joinIndexer(360000, new NullProgressMonitor()));
			
			index= CCorePlugin.getIndexManager().getIndex(cproject);
	
			index.acquireReadLock();
			ast = TestSourceReader.createIndexBasedAST(index, cproject, cppfile);
		}
	
		public void tearDown() throws Exception {
			if (index != null) {
				index.releaseReadLock();
			}
			if (cproject != null) {
				cproject.getProject().delete(IResource.FORCE | IResource.ALWAYS_DELETE_PROJECT_CONTENT, new NullProgressMonitor());
			}
		}
	
		public IIndex getIndex() {
			return index;
		}
		
		public boolean isCompositeIndex() {
			return false;
		}
	}
	
	interface IMyTestStrategy {
		IIndex getIndex();
		void setUp() throws Exception;
		void tearDown() throws Exception;
		public IASTTranslationUnit getAst();
		public StringBuffer[] getTestData();
		public ICProject getCProject();
		public boolean isCompositeIndex();
	}	
	
	IMyTestStrategy strat = null;

	//int forwardFunc(int);
	//
	//int forwardFunc(int i){
	//	return 0;
	//}
	//
	//int main(){
	//	return forwardFunc(1);
	//}
	public void testMergOfFunctionDefinitionAndDeclarationInSameFile1() throws Exception{
		
		IBinding tdAST = getBindingFromASTName("forwardFunc;", 11);
		assertFalse(tdAST instanceof IIndexBinding);
		IBinding tdIndex= getIndex().adaptBinding(tdAST);
		assertTrue(tdIndex instanceof IIndexBinding);	
	}
}

