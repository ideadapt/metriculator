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
import ch.hsr.ifs.cdt.metriculator.model.nodes.ILogicNode;

public class PreOrderLogicTreeVisitor extends PreOrderTreeVisitor{

	private HashMap<String, AbstractNode> logicChildren = new HashMap<String, AbstractNode>();
	private HashMap<AbstractNode, String> members     = new HashMap<AbstractNode, String>();

	private AbstractNode currentNode = null;

	@Override
	void visitNode(AbstractNode n) {
		if(n.getParent() == null){
			rootNode = n.shallowClone();
			currentNode = rootNode;
		}else{
			if(n instanceof ILogicNode){
				AbstractNode copy = n.shallowClone();
				AbstractNode existing = logicChildren.get(getLogicalUniqueNameOf(copy));
				if(existing != null){
					currentNode = existing;
					currentNode.addNodeValuesFrom(copy);
				}else{
					currentNode = currentNode.add(copy);
					logicChildren.put(getLogicalUniqueNameOf(copy), copy);
					prepareMemberFunctionsAndNestedTypes(currentNode);
				}
			}
		}
	}

	@Override
	protected void leaveNode(AbstractNode n) {
		if(n instanceof ILogicNode){
			currentNode = currentNode.getParent();
		}
	}

	private String getLogicalUniqueNameOf(AbstractNode node){
		return getLogicalUniqueNameOf(node, "");
	}

	private String getLogicalUniqueNameOf(AbstractNode node, String logicalNamePrefix){

		if(node instanceof ILogicNode){
			String scopeName = node.getScopeName();
			if(((ILogicNode)node).isAnonymous()){
				scopeName = node.getScopeUniqueName();
			}
			logicalNamePrefix = scopeName + (logicalNamePrefix.trim().isEmpty() ? logicalNamePrefix : TreeBuilder.PATH_SEPARATOR + logicalNamePrefix);
		}

		if(node.getParent() != null){
			return getLogicalUniqueNameOf(node.getParent(), logicalNamePrefix);
		}
		return logicalNamePrefix;
	}

	private void prepareMemberFunctionsAndNestedTypes(AbstractNode node) {
		if(node.getNodeInfo().hasInfos() && (node instanceof FunctionNode || node instanceof CompositeTypeNode)){
			if(node.getNodeInfo().isMember()){
				members.put(node, node.getNodeInfo().getLogicalOwnerName());
			}
		}
	}

	public void mergeMembers() {
		String logicalName;
		for (AbstractNode node : members.keySet()) {
			node.removeFromParent();
			logicalName = members.get(node);
			logicalName = addAstHashes(logicalName, node);
			if(logicChildren.get(logicalName) != null ){
				logicChildren.get(logicalName).add(node);
			}
		}
	}

	private String addAstHashes(String logicalName, AbstractNode node) {
		if(node == null){
			return logicalName;
		}
		if(node instanceof ILogicNode){
			if(((ILogicNode)node).isAnonymous()){
				return addAstHashes(node.getNodeInfo().getASTNodeHash() + logicalName, node.getParent());
			}
		}
		return addAstHashes(logicalName, node.getParent());
	}

	public void mergeFunctionDefinitionsAndDeclarations() {
		for(AbstractNode def : members.keySet()){
			if(def.getNodeInfo().isFunctionDefinition()){
				replaceDeclarationWith(def);
			}
		}
	}

	private void replaceDeclarationWith(AbstractNode def) {
		for(AbstractNode decl : members.keySet()){
			if(decl.getNodeInfo().isFunctionDeclarator()){
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
