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

package ch.hsr.ifs.cdt.metriculator.model;

import java.util.HashMap;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeTypeNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.LogicNode;

public class PreOrderLogicTreeVisitor extends PreOrderTreeVisitor{

	private HashMap<String, AbstractNode> logicNodes  = new HashMap<String, AbstractNode>();
	private HashMap<AbstractNode, String> memberNodes = new HashMap<AbstractNode, String>();
	private AbstractNode currentNode = null;

	@Override
	void visitNode(AbstractNode n) {
		if(n.getParent() == null){
			rootNode    = n.shallowClone();
			currentNode = rootNode;
		}else{
			if(n instanceof LogicNode){
				AbstractNode copy     = n.shallowClone();
				AbstractNode existing = logicNodes.get(getLogicalUniqueNameOf(copy));
				
				if(existing != null){
					currentNode = existing;
					currentNode.addNodeValuesFrom(copy);
				}else{
					currentNode = currentNode.add(copy);
					logicNodes.put(getLogicalUniqueNameOf(copy), copy);
					prepareMembers(currentNode);
				}
			}
		}
	}

	@Override
	protected void leaveNode(AbstractNode n) {
		if(n instanceof LogicNode){
			currentNode = currentNode.getParent();
		}
	}

	private String getLogicalUniqueNameOf(AbstractNode node){
		return getLogicalUniqueNameOf(node, new StringBuilder());
	}

	private String getLogicalUniqueNameOf(AbstractNode node, StringBuilder logicalNamePrefix){

		if(node instanceof LogicNode){
			String scopeName = node.getScopeName();
			if(((LogicNode)node).isAnonymous()){
				scopeName = node.getScopeUniqueName();
			}
			
			if(logicalNamePrefix.length() == 0){
				logicalNamePrefix.append(scopeName);
			}else{
				logicalNamePrefix.insert(0, TreeBuilder.PATH_SEPARATOR).insert(0, scopeName);
			}
		}

		if(node.getParent() != null){
			return getLogicalUniqueNameOf(node.getParent(), logicalNamePrefix);
		}
		return logicalNamePrefix.toString();
	}
	
	private void prepareMembers(AbstractNode node) {
		if(node instanceof FunctionNode || node instanceof CompositeTypeNode){
			if(node.getNodeInfo().isMember()){
				memberNodes.put(node, node.getNodeInfo().getLogicalOwnerName());
			}
		}
	}

	public void mergeMembers() {
		String logicalOwnerName;
		for (AbstractNode node : memberNodes.keySet()) {
			node.removeFromParent();
			logicalOwnerName = memberNodes.get(node);

			if(logicNodes.get(logicalOwnerName) != null ){
				logicNodes.get(logicalOwnerName).add(node);
			}
			System.out.println("-");
		}
	}

	public void mergeDefinitionsAndDeclarations() {
		for(AbstractNode def : memberNodes.keySet()){
			if(def.getNodeInfo().isFunctionDefinition()){
				replaceFuncDeclarationWith(def);
			}else if(def.getNodeInfo().isCompositeTypeSpecifier()){
				replaceTypeDeclarationWith(def);
			}
		}
	}

	private void replaceFuncDeclarationWith(AbstractNode def) {
		for(AbstractNode decl : memberNodes.keySet()){
			if(decl.getNodeInfo().isFunctionDeclarator()){
				removeDeclaration(def, decl);
			}
		}
	}

	private void replaceTypeDeclarationWith(AbstractNode def) {
		for(AbstractNode decl : memberNodes.keySet()){
			if(decl.getNodeInfo().isElaboratedTypeSpecifier()){
				removeDeclaration(def, decl);
			}
		}
	}
	
	private void removeDeclaration(AbstractNode def, AbstractNode decl) {
		if(def.getNodeInfo().getLogicalOwnerName().equals(decl.getNodeInfo().getLogicalOwnerName())){
			if(def.getNodeInfo().getLogicalName().equals(decl.getNodeInfo().getLogicalName())){
				decl.removeFromParent();
				decl = null;
			}
		}
	}
	
}
