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

import org.eclipse.cdt.codan.core.model.CodanSeverity;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public class XMLBuilderVisitor implements INodeVisitor {
	
	private Collection<AbstractMetric> metrics;
	public MetriculatorXMLDocument xml;
	public Node curr;
	
	public XMLBuilderVisitor(Collection<AbstractMetric> metrics, MetriculatorXMLDocument xml) {
		this.metrics = metrics;
		this.xml = xml;
		curr = this.xml.doc.getDocumentElement();

	}

	private Element createMetricsElement(AbstractNode forNode) {
		Element metrics = xml.doc.createElement("metrics");
		
		for(AbstractMetric m : this.metrics){
			
			Element metric = xml.doc.createElement(m.getName().toLowerCase());
			int aggregatedValue = forNode.getValueOf(m).aggregatedValue;
			metric.setTextContent(Integer.valueOf(aggregatedValue).toString());
			applyProblemsOf(m, forNode, metric);
			metrics.appendChild(metric);
		}
		return metrics;
	}
	
	private void applyProblemsOf(AbstractMetric metric, AbstractNode inNode, Element toElement) {
		Collection<IProblem> problems = metric.getChecker().getProblemsFor(inNode);
		
		if(problems != null){
			for(IProblem p : problems){
				if(p.getSeverity() == CodanSeverity.Warning){
					toElement.setAttribute("problem-state", "warning");
				}else if(p.getSeverity() == CodanSeverity.Error){
					toElement.setAttribute("problem-state", "error");
				}else if(p.getSeverity() == CodanSeverity.Info){
					toElement.setAttribute("problem-state", "none");
				}
			}
		}
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
		createNodeXMLElement(n);
	}
	
	@Override
	public void visit(NamespaceNode n) {
		if(n.isAnonymous()){
			createNodeXMLElement(n, "anonymous");
		}else{			
			createNodeXMLElement(n);
		}
	}
	
	private void createNodeXMLElement(AbstractNode n, String label) {
		Element e = xml.doc.createElement("node");
		e.setAttribute("type", n.getClass().getSimpleName().toLowerCase());
		e.setAttribute("label", label);
		e.appendChild(createMetricsElement(n));	
		curr.appendChild(e);
		
		processChildrenOf(n, e);
	}

	private void createNodeXMLElement(AbstractNode n) {
		createNodeXMLElement(n, n.getScopeName());
	}
}
