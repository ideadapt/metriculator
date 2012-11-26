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

import org.junit.Assert;
import org.w3c.dom.Document;

import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetricChecker;
import ch.hsr.ifs.cdt.metriculator.converters.IModelConverter;
import ch.hsr.ifs.cdt.metriculator.converters.XMLModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.HybridTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;
import ch.hsr.ifs.cdt.metriculator.tests.MetriculatorCheckerTestCase;

public class XMLModelConverterTest extends MetriculatorCheckerTestCase {

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
	
	public void testXmlBasics(){
				
		IModelConverter<Document> conv = new XMLModelConverter();
		conv.convert(root, metricLsLoc, metricMcCabe);
		Document result = ((XMLModelConverter)conv).getResult();
		String resultString = ((XMLModelConverter)conv).getXML();

		System.out.println(resultString);
		
		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<metriculator>\n" +
				"  <Directory/>\n" +
				"</metriculator>\n";
		
		Assert.assertEquals(expected, resultString);
	}	
}