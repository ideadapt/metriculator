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

import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public class PreOrderXMLTreeVisitor implements INodeVisitor {
	
	private Collection<AbstractMetric> metrics;
	public Document doc;
	public Node curr;
	
	public PreOrderXMLTreeVisitor(Collection<AbstractMetric> metrics) {
		this.metrics = metrics;
		
		iniXMLDoc();
		curr = createRootElement();
	}

	private Node createRootElement() {
		return doc.appendChild(doc.createElement("metriculator"));
	}

	private void iniXMLDoc() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		doc = documentBuilder.newDocument();
	}
	
	private Element createMetricsElement(AbstractNode n) {
		Element metrics = doc.createElement("metrics");
		
		for(AbstractMetric m : this.metrics){
			
			Element metric = doc.createElement(m.getName().toLowerCase());
			int aggregatedValue = n.getValueOf(m).aggregatedValue;
			metric.setTextContent(Integer.valueOf(aggregatedValue).toString());
			
			metrics.appendChild(metric);
		}
		return metrics;
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
	
	public void visit(ProjectNode n){
		createNodeXMLElement(n);
	}
	
	public void visit(FolderNode n){
		createNodeXMLElement(n);
	}

	@Override
	public void visit(AbstractNode n) {
		//throw new InvalidOpenTypeException("Should never come here, implement visitor for each node type.");
		
		createNodeXMLElement(n);
	}
	
	@Override
	public void visit(NamespaceNode n) {
		//throw new InvalidOpenTypeException("Should never come here, implement visitor for each node type.");
		
		if(n.isAnonymous()){
			createNodeXMLElement(n, "anonymous");
		}else{			
			createNodeXMLElement(n);
		}
		
	}
	
	private void createNodeXMLElement(AbstractNode n, String label) {
		Element e = doc.createElement("node");
		e.setAttribute("type", n.getClass().getSimpleName().toLowerCase());
		e.setAttribute("label", label); // TODO xml / html escape
		e.appendChild(createMetricsElement(n));	
		curr.appendChild(e);
		
		processChildrenOf(n, e);
	}

	private void createNodeXMLElement(AbstractNode n) {
		createNodeXMLElement(n, n.getScopeName());
	}
}
