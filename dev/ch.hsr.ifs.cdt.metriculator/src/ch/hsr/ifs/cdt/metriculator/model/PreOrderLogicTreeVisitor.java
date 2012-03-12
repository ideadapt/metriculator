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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeTypeNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.LogicNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;

public class PreOrderLogicTreeVisitor extends PreOrderTreeVisitor{

	private HashMap<String, AbstractNode> logicNodes  = new HashMap<String, AbstractNode>();
	private HashMap<AbstractNode, String> memberNodes = new HashMap<AbstractNode, String>();
	private AbstractNode currentNode = null;
	private int anoNsCount = 0;

	@Override
	void visitNode(AbstractNode n) {
		if(n.getParent() == null){
			rootNode    = n.shallowClone();
			currentNode = rootNode;
		}else{
			if(n instanceof FileNode){
				anoNsCount = 0;
			}
			
			if(n instanceof LogicNode){
				if(((LogicNode) n).isAnonymous() && n instanceof NamespaceNode){
					++anoNsCount;
				}
				
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
				//scopeName = node.getScopeUniqueName();
				/**
				 * Problem:
				 * anons eindeutig identifizieren
				 * 1. ansatz astnode hashcode:
				 * 	- im binding owner nicht verfügbar
				 * 2. ansatz owner hashcode:
				 *  - in der namespace ast node nicht verfügbar
				 * 3. ansatz anonns mit scopeName = "" versehen
				 *  - dann werden alle anons members in eine einzige anons node gemerged
				 * 4. ansatz anons pro file durchnummerieren (löst 1. & 2.)
				 *  - beim mergen der definition fehlt die info zu welcher anons nummer die definition gehört
				 * 5. ansatz anons pro file durchnummerieren
				 *  -? diese nummer im logicaluniquename und im logicalownername verwenden
				 * */
				((LogicNode) node).anoId = anoNsCount;
				scopeName = "(anonymous)"+((LogicNode)node).anoId;
				//scopeName = "";
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
			System.out.println("ScopeName: " + node.getScopeUniqueName());
			System.out.println("OwnerKey: " + logicalOwnerName);
			
			logicalOwnerName = addIdentifiers(Arrays.asList(logicalOwnerName.split(TreeBuilder.PATH_SEPARATOR)), node.getParent());
			
//			String logUName = getLogicalUniqueNameOf(node);
//			//logUName        = logUName.replaceAll(TreeBuilder.LOGIC_SEPARATOR, TreeBuilder.PATH_SEPARATOR);
//						
//			System.out.println("LogUName: " + logUName);
//			if(logUName.lastIndexOf(TreeBuilder.PATH_SEPARATOR) > 0){
//				logicalOwnerName = logUName.substring(0, logUName.lastIndexOf(TreeBuilder.PATH_SEPARATOR));
//			}else{
//				logicalOwnerName = logUName;
//			}
			System.out.println("LogOwner: " + logicalOwnerName);

			if(logicNodes.get(logicalOwnerName) != null ){
				logicNodes.get(logicalOwnerName).add(node);
			}
			System.out.println("-");
		}
	}
	
	private String addIdentifiers(List<String> scopeNames, AbstractNode owner){
		
		for(int i=scopeNames.size()-1; i>-1; i--){
			String scopeName = scopeNames.get(i);
			
			if(owner != null && owner instanceof LogicNode && ((LogicNode)owner).isAnonymous()){
				scopeName = "(anonymous)"+((LogicNode)owner).anoId;
			}
			
			if(owner != null)
				owner = owner.getParent();
			scopeNames.set(i, scopeName);
		} 
		
		return combine(scopeNames, "#");
	}
	
	private String combine(List<String> names, String separator) {
		int k = names.size();
		if (k == 0)
			return null;
		StringBuilder out = new StringBuilder();
		out.append(names.get(0));
		for (int x = 1; x < k; ++x)
			out.append(separator).append(names.get(x));
		return out.toString();
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
