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

import org.junit.Assert;
import org.w3c.dom.Document;

import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.LSLOCMetricChecker;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetric;
import ch.hsr.ifs.cdt.metriculator.checkers.McCabeMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.converters.IModelConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.ModelToXMLConverter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
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
			checkerLsloc = AbstractMetricChecker
					.getChecker(LSLOCMetricChecker.class);
			metricLsLoc = new LSLOCMetric(checkerLsloc, "LSLOC",
					"lines of code");
		}
		if (checkerMcCabe == null) {
			checkerMcCabe = AbstractMetricChecker
					.getChecker(McCabeMetricChecker.class);
			metricMcCabe = new McCabeMetric(checkerMcCabe, "McCabe", "CC");
		}

		System.out.println(getName());
		root = new WorkspaceNode("rootnotmodified");
	}

	public void testNestedWithMetrics() {

		AbstractNode n = new ProjectNode("testproject");

		FolderNode f1 = new FolderNode(null, "testfolder1");
		f1.setNodeValue(metricLsLoc.getKey(), 10);
		f1.setNodeValue(metricMcCabe.getKey(), 7);

		FolderNode f2 = new FolderNode(null, "testfolder2");
		f2.setNodeValue(metricLsLoc.getKey(), 20);
		f2.setNodeValue(metricMcCabe.getKey(), 0);

		root.add(n)
			.add(f1).getParent()
			.add(f2);

		IModelConverter<Document> conv = new ModelToXMLConverter();
		AbstractMetric[] abstractMetrics = new AbstractMetric[]{metricLsLoc, metricMcCabe};
		conv.convert(root, Arrays.asList(abstractMetrics));
		String resultString = ((ModelToXMLConverter) conv).getXML();

		System.out.println(resultString);

		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<metriculator>\n" +
				"  <ProjectNode label=\"testproject\">\n" +
				"    <metrics>\n" +
				"      <lsloc>30</lsloc>\n" +
				"      <mccabe>8</mccabe>\n" +
				"    </metrics>\n" +
				"    <FolderNode label=\"testfolder1\">\n" +
				"      <metrics>\n" +
				"        <lsloc>10</lsloc>\n" +
				"        <mccabe>8</mccabe>\n" +
				"      </metrics>\n" +
				"    </FolderNode>\n" +
				"    <FolderNode label=\"testfolder2\">\n" +
				"      <metrics>\n" +
				"        <lsloc>20</lsloc>\n" +
				"        <mccabe>1</mccabe>\n" +
				"      </metrics>\n" +
				"    </FolderNode>\n" +
				"  </ProjectNode>\n" +
				"</metriculator>\n";

		Assert.assertEquals(expected, resultString);
	}

	public void testNestedNoMetrics() {

		AbstractNode n = new ProjectNode("testproject");

		FolderNode f1 = new FolderNode(null, "testfolder1");
		f1.setNodeValue(metricLsLoc.getKey(), 10);
		f1.setNodeValue(metricMcCabe.getKey(), 7);
		FolderNode f2 = new FolderNode(null, "testfolder2");
		f2.setNodeValue(metricLsLoc.getKey(), 20);
		f2.setNodeValue(metricMcCabe.getKey(), 0);

		root.add(n)
			.add(f1).getParent()
			.add(f2);

		IModelConverter<Document> conv = new ModelToXMLConverter();
		conv.convert(root, null);
		String resultString = ((ModelToXMLConverter) conv).getXML();

		System.out.println(resultString);

		final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<metriculator>\n" +
				"  <ProjectNode label=\"testproject\">\n" +
				"    <metrics/>\n" +
				"    <FolderNode label=\"testfolder1\">\n" +
				"      <metrics/>\n" +
				"    </FolderNode>\n" +
				"    <FolderNode label=\"testfolder2\">\n" +
				"      <metrics/>\n" +
				"    </FolderNode>\n" +
				"  </ProjectNode>\n" +
				"</metriculator>\n";

		Assert.assertEquals(expected, resultString);
	}
}