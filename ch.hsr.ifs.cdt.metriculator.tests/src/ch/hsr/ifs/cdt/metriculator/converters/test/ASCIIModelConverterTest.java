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

package ch.hsr.ifs.cdt.metriculator.converters.test;

import java.util.Arrays;

import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.HybridTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.TreePrinter;
import ch.hsr.ifs.cdt.metriculator.model.converters.ASCIIModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;
import ch.hsr.ifs.cdt.metriculator.tests.MetriculatorCheckerTestCase;

public class ASCIIModelConverterTest extends MetriculatorCheckerTestCase {

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
	
	public void testSimpleNestedHybridTree(){
		HybridTreeBuilder b = new HybridTreeBuilder("wstest");
		
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
				
		ASCIIModelConverter conv = new ASCIIModelConverter();
		AbstractMetric[] abstractMetrics = new AbstractMetric[]{ metricLsLoc, metricMcCabe};
		conv.convert(b.root, Arrays.asList(abstractMetrics));
		String result = conv.getResult();
		
		System.out.println(result);
		System.out.println("---\n");
		TreePrinter.printTree(b.root, metricLsLoc, metricMcCabe);
	}	
}