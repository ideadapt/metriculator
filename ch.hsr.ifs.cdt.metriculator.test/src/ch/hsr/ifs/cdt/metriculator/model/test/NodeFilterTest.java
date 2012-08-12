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
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter.CompositeNodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter.FileNodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter.FunctionNodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter.NamespaceNodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter.NoneFilter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;

/**
 * Test for {@see NodeFilter}.
 */
public class NodeFilterTest extends TestCase {

	private NodeFilter filter;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		System.out.println(getName());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCompositeNodeFilter(){
		filter = NodeFilter.composite();
	
		assertTrue(filter instanceof CompositeNodeFilter);
		assertTrue(filter.canPassThrough(new TypeDefNode("")));
		assertFalse(filter.canPassThrough(new FunctionDefNode("")));
		assertFalse(filter.canPassThrough(new NamespaceNode("")));
		assertFalse(filter.canPassThrough(new FileNode("")));
	}
	
	public void testFileNodeFilter(){
		filter = NodeFilter.file();
		
		assertTrue(filter instanceof FileNodeFilter);
		assertTrue(filter.canPassThrough(new FileNode("")));
		assertFalse(filter.canPassThrough(new FunctionDefNode("")));
		assertFalse(filter.canPassThrough(new NamespaceNode("")));
		assertFalse(filter.canPassThrough(new TypeDefNode("")));		
	}
	
	public void testNamespaceNodeFilter(){
		filter = NodeFilter.namespace();
		
		assertTrue(filter instanceof NamespaceNodeFilter);
		assertTrue(filter.canPassThrough(new NamespaceNode("")));
		assertFalse(filter.canPassThrough(new FunctionDefNode("")));
		assertFalse(filter.canPassThrough(new FileNode("")));
		assertFalse(filter.canPassThrough(new TypeDefNode("")));		
	}
	
	public void testFunctionNodeFilter(){
		filter = NodeFilter.function();
		
		assertTrue(filter instanceof FunctionNodeFilter);
		assertTrue(filter.canPassThrough(new FunctionDefNode("")));
		assertFalse(filter.canPassThrough(new NamespaceNode("")));
		assertFalse(filter.canPassThrough(new FileNode("")));
		assertFalse(filter.canPassThrough(new TypeDefNode("")));
	}
	
	public void testNoneNodeFilter(){
		filter = NodeFilter.none();
		
		assertTrue(filter instanceof NoneFilter);
		assertTrue(filter.canPassThrough(new FunctionDefNode("")));
		assertTrue(filter.canPassThrough(new NamespaceNode("")));
		assertTrue(filter.canPassThrough(new FileNode("")));
		assertTrue(filter.canPassThrough(new TypeDefNode("")));
		
	}
}