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
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.FlatTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.HybridTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.LogicTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;
import ch.hsr.ifs.cdt.metriculator.test.MetriculatorCheckerTestCase;

/**
 * Test for {@see AbtractNode} and other model classes. Validates tree structure operations like 'add a node' and recursive metric calculation.
 */
public class TreeBuilderTest extends MetriculatorCheckerTestCase {

	private AbstractNode root;
	private AbstractMetricChecker checkerLsloc;
	private AbstractMetricChecker checkerMcCabe;
	private AbstractMetric metricMcCabe;
	private AbstractMetric metricLsLoc;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		if (checkerLsloc == null) {
			checkerLsloc = AbstractMetricChecker.getChecker(LSLOCMetricChecker.class);
			metricLsLoc = new LSLOCMetric(checkerLsloc, "LSLOC", "lines of code");
		}
		if (checkerMcCabe == null) {
			checkerMcCabe = AbstractMetricChecker.getChecker(McCabeMetricChecker.class);
			metricMcCabe = new McCabeMetric(checkerMcCabe, "McCabe", "CC");
		}
		
		System.out.println(getName());
		root = new WorkspaceNode("rootnotmodified");
	}
	
	@Override
	public void tearDown() throws CoreException {
		super.tearDown();
		
		TreePrinter.printTree(root, metricLsLoc, metricMcCabe);
	}
	
	
	public void testCreateTreeFromFileWithLeadingSeparator() {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("/Test.cpp"), null);
				
		assertTrue(file instanceof FileNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromFile()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("Test.cpp"), null);
				
		assertTrue(file instanceof FileNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromFileFolder()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("cute/Test.cpp"), null);
				
		assertTrue(file instanceof FileNode);
		assertTrue(file.getParent() instanceof FolderNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromFileInNestedFolders()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("cute/tests/Test.cpp"), null);
				
		assertTrue(file instanceof FileNode);
		assertTrue(file.getParent() instanceof FolderNode);
		assertTrue(file.getParent().getParent() instanceof FolderNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromOneSegment()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("cute"), null);
				
		assertTrue(file instanceof FileNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromOneSegmentWithTrailingSeparator()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("cute/"), null);
				
		assertTrue(file instanceof FileNode);
		assertEquals("cute", file.getScopeUniqueName());
		assertEquals(0, file.getChildren().size());
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromOneSegmentWithLeadingSeparator()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("/cute"), null);
		
		assertTrue(file instanceof FileNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromFileInRoot()  {
		AbstractNode file = TreeBuilder.createTreeFromPath(new Path("test.cpp"), null);
		
		assertTrue(file instanceof FileNode);
		
		root = file.getRoot();
	}
	
	public void testCreateTreeFromPathAndMergeWithHybrid()  {
		AbstractNode file1 = TreeBuilder.createTreeFromPath("workspace"+TreeBuilder.PATH_SEPARATOR+"project", new Path("/src/cute/folder1/test.cpp"), null);
		AbstractNode file11 = TreeBuilder.createTreeFromPath("workspace"+TreeBuilder.PATH_SEPARATOR+"project", new Path("/src/cute/folder1/folder11/test.cpp"), null);
		AbstractNode fileSystemTop1  = file1.getRoot();
		AbstractNode fileSystemTop11  = file11.getRoot();
		
		HybridTreeBuilder b = new HybridTreeBuilder("workspace");		
		AbstractNode n      = new ProjectNode("project");
		
		AbstractNode currentScopeNode = b.addChild(b.root, n);
				
		// test merge file1
		b.addChild(currentScopeNode, fileSystemTop1);
		
		assertTrue(file1 instanceof FileNode);
		assertTrue(fileSystemTop1 instanceof FolderNode);
		assertTrue(fileSystemTop1.getChildren().iterator().next() instanceof FolderNode);
		assertTrue(fileSystemTop1
					.getChildren().iterator().next()
						.getChildren().iterator().next()
							.getChildren().iterator().next() instanceof FileNode);
		
		currentScopeNode = fileSystemTop1.getParent();
		
		// test merge file11
		b.addChild(currentScopeNode, fileSystemTop11);
		
		assertTrue(file11 instanceof FileNode);
		assertTrue(fileSystemTop11 instanceof FolderNode);
		assertTrue(fileSystemTop11.getChildren().iterator().next() instanceof FolderNode);
		assertTrue(fileSystemTop11
					.getChildren().iterator().next()
						.getChildren().iterator().next()
							.getChildren().iterator().next()
								.getChildren().iterator().next() instanceof FileNode);
		
		root = b.root;
	}
	
	public void testBuildTreeTwiceEachWithDifferentMetrics(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
		AbstractNode n   = new ProjectNode("testproject");
		AbstractNode n2  = new FileNode("file1");
		AbstractNode n3  = new NamespaceNode("ns1");
		
		// lsloc metric
		b.addChild(b.root, n);
			b.addChild(n, n2);
				b.addChild(n2, n3);
				
		n3.setNodeValue(AbstractMetric.getKeyFor(LSLOCMetric.class), 10);
		
		// mccabe metric
		b.addChild(n, n2);
			b.addChild(n2, n3);
			
		n3.setNodeValue(AbstractMetric.getKeyFor(McCabeMetric.class), 80);
		
		
		assertEquals(10, n3.getNodeValue(AbstractMetric.getKeyFor(LSLOCMetric.class)));
		assertEquals(80, n3.getNodeValue(AbstractMetric.getKeyFor(McCabeMetric.class)));
		
		root = b.root;
	}
	
	public void testCreateFlatFromUniqueTree(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
		AbstractNode n   = new ProjectNode("testproject");
		AbstractNode n2  = new FileNode("file1");
		AbstractNode n3  = new NamespaceNode("ns1");
		
		b.addChild(b.root, n);
			b.addChild(n, n2);
				b.addChild(n2, n3);
		
		root = FlatTreeBuilder.buildFrom(b).root;
		
		assertEquals(3, root.getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof ProjectNode);
	}
	
	public void testFlatTreeHasSameAggregatedValuesAsHybrid(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		String lslocKey = AbstractMetric.getKeyFor(LSLOCMetric.class);
		
		AbstractNode n   = new ProjectNode("testproject");
		AbstractNode n2  = new FileNode("file1");
		n2.setNodeValue(lslocKey, 7);
		AbstractNode n3  = new NamespaceNode("ns1");
		n3.setNodeValue(lslocKey, 23);
		
		b.addChild(b.root, n);
			b.addChild(n, n2);
				b.addChild(n2, n3);
		
		metricLsLoc.aggregate(b.root);
		root = FlatTreeBuilder.buildFrom(b).root;
		int hybridAggregatedValue = b.root.getValueOf(metricLsLoc).aggregatedValue;
		int flatAggregatedValue   = root.getValueOf(metricLsLoc).aggregatedValue;
		
		assertEquals(3, root.getChildren().size());
		assertEquals(hybridAggregatedValue, flatAggregatedValue);
		assertEquals(30, flatAggregatedValue);
	}	
	
	public void testCreateFlatFromDuplicateTree(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
		AbstractNode proj1  = new ProjectNode("testproject1");
		AbstractNode f11    = new FileNode("file1");
		AbstractNode ns111  = new NamespaceNode("ns1");
		AbstractNode proj2  = new ProjectNode("testproject2");
		AbstractNode f21    = new FileNode("file1");
		AbstractNode ns212  = new NamespaceNode("ns2");
		
		b.addChild(proj1, f11);
			b.addChild(f11, ns111);
			
		b.addChild(proj2, f21);
			b.addChild(f21, ns212);
		
		b.addChild(b.root, proj1);
		b.addChild(b.root, proj2);

		root = FlatTreeBuilder.buildFrom(b).root;
		
		assertEquals(6, root.getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof ProjectNode);
	}
	
	public void testCreateHybridWithDuplicates(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
		AbstractNode proj = new ProjectNode("testproject");
		AbstractNode f1   = new FileNode("file1");
		AbstractNode f1Dupl  = new FileNode("file1");
		AbstractNode ns1  = new NamespaceNode("ns1");
		AbstractNode ns2  = new NamespaceNode("ns2");
		
		b.addChild(b.root, proj);
				b.addChild(proj, f1);
					b.addChild(f1, ns1);
					b.addChild(f1Dupl, ns2);
				b.addChild(proj, f1Dupl);	    // children of f1Dupl will be merged into f1
		
		assertEquals(1, b.root.getChildren().size());
		assertEquals(1, b.root.getChildren().iterator().next().getChildren().size());
		assertEquals(2, b.root.getChildren().iterator().next().getChildren().iterator().next().getChildren().size());
		
		root = b.root;
	}
	
	public void testCreateLogicTree(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
		root = b.root;
		
		AbstractNode n1 = new ProjectNode("testproject1");

		AbstractNode n2 = new FileNode("file1");
		AbstractNode n3 = new FileNode("file2");

		AbstractNode n4  = new NamespaceNode("ns1");
		AbstractNode n5 = new NamespaceNode("ns2");
		AbstractNode n6  = new NamespaceNode("ns3");
		
		b.addChild(root, n1);
			b.addChild(n1, n2);
			b.addChild(n1, n3);
				b.addChild(n2, n4);
				b.addChild(n2, n5);
				b.addChild(n3, n6);
				
		root = LogicTreeBuilder.buildFrom(b).root;
				
		assertEquals(3, root.getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof NamespaceNode);
	}

	
	public void testCreateLogicTreeWithDuplicates(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		String slocKey = AbstractMetric.getKeyFor(LSLOCMetric.class);
		
		root = b.root;
		
		AbstractNode p = new ProjectNode("testproject1");

		AbstractNode f1 = new FileNode("file1");
		AbstractNode f2 = new FileNode("file2");

		AbstractNode ns11  = new NamespaceNode("ns1");
		ns11.setNodeValue(slocKey, 1);
		AbstractNode ns12 = new NamespaceNode("ns1");
		ns12.setNodeValue(slocKey, 1);
		AbstractNode ns2  = new NamespaceNode("ns2");
		ns2.setNodeValue(slocKey, 1);

		AbstractNode t1 = new TypeDefNode("type1");
		t1.setNodeValue(slocKey, 1);

		AbstractNode fn1 = new FunctionDefNode("mfunc1");
		fn1.setNodeValue(slocKey, 5);
		AbstractNode fn2 = new FunctionDefNode("mfunc2");
		fn2.setNodeValue(slocKey, 50);
		AbstractNode fn3 = new FunctionDefNode("func3");
		fn3.setNodeValue(slocKey, 20);
		AbstractNode fn4 = new FunctionDefNode("func4");
		fn4.setNodeValue(slocKey, 100);
		
		b.addChild(root, p);
			b.addChild(p, f1);
				b.addChild(f1, ns11);
					b.addChild(ns11, fn3);
				b.addChild(f1, ns2);
					b.addChild(ns2, fn4);
			b.addChild(p, f2);
				b.addChild(f2, ns12);
					b.addChild(ns12, t1);
						b.addChild(t1, fn1);
						b.addChild(t1, fn2);
						
		root = LogicTreeBuilder.buildFrom(b).root;
						
		assertEquals(2, root.getChildren().size());
		assertEquals(2, root.getChildren().iterator().next().getChildren().size());
		assertTrue(root.getChildren().iterator().next() instanceof NamespaceNode);
		
		assertEquals(179, root.getValueOf(metricLsLoc).aggregatedValue);
		assertEquals(0, root.getValueOf(metricLsLoc).nodeValue);
		assertEquals(78, root.getChildren().iterator().next().getValueOf(metricLsLoc).aggregatedValue);
		assertEquals(2, root.getChildren().iterator().next().getValueOf(metricLsLoc).nodeValue);

	}
	
}