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

import junit.framework.TestCase;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

/**
 * Test for {@see AbtractNode} and other model classes. Validates tree structure operations like 'add a node' and recursive metric calculation.
 * 
 */
public class NodeCompositeTest extends TestCase {

	private static final String TESTWORKSPACE = "testworkspace";
	private AbstractNode root;
	private AbstractMetricChecker checker;
	private AbstractMetric metricLSLOC;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		System.out.println(getName());
		root = new WorkspaceNode(TESTWORKSPACE);
		
		
		if (checker == null) {
			checker = AbstractMetricChecker.getChecker(LSLOCMetricChecker.class);
			metricLSLOC = new LSLOCMetric(checker, "LSLOC", "lines of code");
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		TreePrinter.printTree(root, metricLSLOC);
	}
	
	public void testAnonymousNamespaceHasSpecialToString(){
		assertEquals(0, root.getChildren().size());
		
		AbstractNode n = new NamespaceNode("");
		
		assertEquals("(anonymous)", n.toString());
	}
	

	public void testGetChildren()  {
		assertEquals(0, root.getChildren().size());
		
		AbstractNode n = new ProjectNode("testproject");
		root.add(n);
		
		assertEquals(1, root.getChildren().size());
	}
	
	public void testAddChild() {
		AbstractNode n = new ProjectNode("testproject");
		root.add(n);
		
		assertEquals(n, root.getChildren().toArray()[0]);
	}
	
	public void testAddingSameChildOnSameLevelWorks()  {
		assertEquals(0, root.getChildren().size());
		
		AbstractNode n   = new ProjectNode("testproject");
		AbstractNode n2  = new FileNode("file1");
		AbstractNode n3  = new NamespaceNode("ns1");
		AbstractNode n31 = new NamespaceNode("ns1"); // adding duplicates on same level works just fine
		n31.setNodeValue(metricLSLOC.getKey(), 23);
		
		root.add(n)
				.add(n2)
					.add(n3).getParent()
					.add(n31); 
		
		assertEquals(2, n2.getChildren().size());
		assertEquals(1, n.getChildren().size());
		assertEquals(0, ((AbstractNode)n.getChildren().toArray()[0]).getNodeValue(metricLSLOC.getKey()));
	}
	
	public void testGetGlobalUniqueName() {
		assertEquals(0, root.getChildren().size());

		AbstractNode n = new ProjectNode("testproject");
		root.add(n);

		AbstractNode n2 = new FileNode("file1");
		n.add(n2);

		assertEquals(TESTWORKSPACE, root.getPath());
		assertEquals(TESTWORKSPACE + TreeBuilder.PATH_SEPARATOR+ "testproject", n.getPath());
		assertEquals(TESTWORKSPACE + TreeBuilder.PATH_SEPARATOR+ "testproject" + TreeBuilder.PATH_SEPARATOR + "file1", n2.getPath());
	}
	
	public void testGetMetricResultWithEmptyTree(){
		assertEquals(0, root.getChildren().size());
		assertEquals(0, root.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, root.getValueOf(metricLSLOC).nodeValue);
		
		AbstractNode n = new ProjectNode("testproject");
		
		root.add(n);
		
		assertEquals(0, root.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, root.getValueOf(metricLSLOC).nodeValue);
		assertEquals(0, n.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, n.getValueOf(metricLSLOC).nodeValue);
	}
	
	public void testGetMetricResultWithAggregatedValues(){
		assertEquals(0, root.getChildren().size());
		
		AbstractNode n = new ProjectNode("testproject");		
		AbstractNode n2 = new FileNode("file1");
		n2.setNodeValue(metricLSLOC.getKey(), 2);
		AbstractNode n3 = new NamespaceNode("ns1");
		n3.setNodeValue(metricLSLOC.getKey(), 10);
		
		root.add(n)
				.add(n2)
					.add(n3); 
		
		assertEquals(12, root.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, root.getValueOf(metricLSLOC).nodeValue);
		assertEquals(12, n.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, n.getValueOf(metricLSLOC).nodeValue);		
		assertEquals(12, n2.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(2, n2.getValueOf(metricLSLOC).nodeValue);
		assertEquals(10, n3.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(10, n3.getValueOf(metricLSLOC).nodeValue);		
	}
	
	public void testGetMetricResultWithManyAggregatedValues(){
		assertEquals(0, root.getChildren().size());
		
		AbstractNode p = new ProjectNode("testproject");
		AbstractNode f1 = new FileNode("file1");
		f1.setNodeValue(metricLSLOC.getKey(), 10);
		AbstractNode ns1 = new NamespaceNode("ns1");
		ns1.setNodeValue(metricLSLOC.getKey(), 10);
		AbstractNode ns31 = new NamespaceNode("ns31");
		ns31.setNodeValue(metricLSLOC.getKey(), 310);
		AbstractNode ns32 = new NamespaceNode("ns32");
		ns32.setNodeValue(metricLSLOC.getKey(), 320);
		AbstractNode f2 = new FileNode("file2");
		f2.setNodeValue(metricLSLOC.getKey(), 20);
		
		root.add(p)
				.add(f1)
					.add(ns1).getParent()
					.add(ns31).getParent()
					.add(ns32).getParent().getParent()
				.add(f2); 
		
		assertEquals(670, root.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, root.getValueOf(metricLSLOC).nodeValue);		
		assertEquals(670, p.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(0, p.getValueOf(metricLSLOC).nodeValue);
		assertEquals(650, f1.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(10, f1.getValueOf(metricLSLOC).nodeValue);
		assertEquals(20, f2.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(20, f2.getValueOf(metricLSLOC).nodeValue);
		assertEquals(10, ns1.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(10, ns1.getValueOf(metricLSLOC).nodeValue);
		assertEquals(310, ns31.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(310, ns31.getValueOf(metricLSLOC).nodeValue);
		assertEquals(320, ns32.getValueOf(metricLSLOC).aggregatedValue);
		assertEquals(320, ns32.getValueOf(metricLSLOC).nodeValue);
	}
}