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
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDeclNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.LogicNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.MemberNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDeclNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;

public class PreOrderLogicTreeVisitor extends PreOrderTreeVisitor{

	private HashMap<String, AbstractNode> logicNodes  = new HashMap<String, AbstractNode>();
	private HashMap<MemberNode, String> memberNodes = new HashMap<MemberNode, String>();
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
					prepareMembers((LogicNode)currentNode);
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
	
	private void prepareMembers(LogicNode node) {
		if(node instanceof MemberNode){
			if(((MemberNode) node).isMember()){
				memberNodes.put((MemberNode) node, ((MemberNode) node).getLogicalOwnerName());
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
		}
	}

	public void mergeDefinitionsAndDeclarations() {
		for(MemberNode def : memberNodes.keySet()){
			if(def instanceof FunctionDefNode){
				replaceFuncDeclarationWith(def);
			}else if(def instanceof TypeDefNode){
				replaceTypeDeclarationWith(def);
			}
		}
	}

	private void replaceFuncDeclarationWith(MemberNode def) {
		for(MemberNode decl : memberNodes.keySet()){
			if(decl instanceof FunctionDeclNode){
				removeDeclaration(def, decl);
			}
		}
	}

	private void replaceTypeDeclarationWith(MemberNode def) {
		for(MemberNode decl : memberNodes.keySet()){
			if(decl instanceof TypeDeclNode){
				removeDeclaration(def, decl);
			}
		}
	}
	
	private void removeDeclaration(MemberNode def, MemberNode decl) {
		if(def.getLogicalOwnerName().equals(decl.getLogicalOwnerName())){
			if(def.getLogicalName().equals(decl.getLogicalName())){
				decl.removeFromParent();
				decl = null;
			}
		}
	}
	
}
