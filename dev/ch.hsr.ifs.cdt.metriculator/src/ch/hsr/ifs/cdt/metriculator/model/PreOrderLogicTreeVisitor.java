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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeTypeNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ILogicNode;

public class PreOrderLogicTreeVisitor extends PreOrderTreeVisitor{

	private HashMap<String, AbstractNode> logicChildren = new HashMap<String, AbstractNode>();
//	private HashMap<AbstractNode, IBinding> members     = new HashMap<AbstractNode, IBinding>();
	private HashMap<AbstractNode, String> members2     = new HashMap<AbstractNode, String>();

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
//		if(node.getNodeInfo().hasInfos() && (node instanceof FunctionNode || node instanceof CompositeTypeNode)){
//			IBinding owner = node.getNodeInfo().getBinding().getOwner();
//			if(owner != null && owner instanceof ICompositeType){
//				members.put(node, owner);
//			}
//		}
		if(node.getNodeInfo().hasInfos() && (node instanceof FunctionNode || node instanceof CompositeTypeNode)){
			if(node.getNodeInfo().isMember()){
				members2.put(node, node.getNodeInfo().getLogicalOwnerName());
			}
		}
	}

	public void mergeMembers() {
		String logicalName;
		for (AbstractNode node : members2.keySet()) {
//			for (AbstractNode node : members.keySet()) {
			node.removeFromParent();
//			logicalName = buildLogicalOwnerName(members.get(node));
			logicalName = members2.get(node);
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

	private String buildLogicalOwnerName(IBinding owner) {
		if(owner.getOwner() == null){
			return owner.getName().toString();
		}
		return buildLogicalOwnerName(owner.getOwner()) + TreeBuilder.PATH_SEPARATOR + owner.getName();
	}

	public void mergeFunctionDefinitionsAndDeclarations() {
		for(AbstractNode node : members2.keySet()){
//			for(AbstractNode node : members.keySet()){
			if(node.getNodeInfo().isFunctionDefinition()){
				replaceDeclarationWith(node);
			}
		}
	}

	private void replaceDeclarationWith(AbstractNode def) {
		IBinding binding = def.getNodeInfo().getBinding();
		for(AbstractNode node : members2.keySet()){
//			for(AbstractNode node : members.keySet()){
			if(node.getNodeInfo().isFunctionDeclarator()){
				node = removeDeclaration(binding, node);
			}
		}
	}

	private AbstractNode removeDeclaration(IBinding binding, AbstractNode node) {
		
		if(binding.equals(node.getNodeInfo().getBinding())){
			node.removeFromParent();
			node = null;
		}

		return node;
	}

}
