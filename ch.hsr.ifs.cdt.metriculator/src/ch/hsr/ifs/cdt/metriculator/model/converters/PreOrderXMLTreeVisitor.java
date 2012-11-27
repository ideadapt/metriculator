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

package ch.hsr.ifs.cdt.metriculator.model.converters;

import javax.management.openmbean.InvalidOpenTypeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public class PreOrderXMLTreeVisitor implements INodeVisitor {
	
	public Document doc;
	public Node curr;
	
	public PreOrderXMLTreeVisitor() {
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		doc = documentBuilder.newDocument();
		Element root = doc.createElement("metriculator");
		doc.appendChild(root);
		curr = root;
	}

	public void visit(ProjectNode n){
		Element e = doc.createElement(n.getClass().getSimpleName());
		e.setAttribute("label", n.getScopeName());
		curr.appendChild(e);
		
		processChildrenOf(n, e);
	}
	
	public void visit(FolderNode n){
		Element e = doc.createElement(n.getClass().getSimpleName());
		e.setAttribute("label", n.getScopeName());
		curr.appendChild(e);
		
		processChildrenOf(n, e);
	}

	public void processChildrenOf(AbstractNode parent, Node parentXmlNode){

		curr = parentXmlNode;
		
		for(AbstractNode child : parent.getChildren()){
			child.accept(this);
		}
		
		curr = parentXmlNode.getParentNode();
	}
	
	@Override
	public void visit(WorkspaceNode n){
		processChildrenOf(n, curr);
	}

	@Override
	public void visit(AbstractNode n) {
		throw new InvalidOpenTypeException("Should never come here, implement visitor for each node type.");
	}
}
