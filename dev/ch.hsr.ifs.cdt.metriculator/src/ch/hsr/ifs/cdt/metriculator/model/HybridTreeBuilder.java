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

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public class HybridTreeBuilder extends TreeBuilder {

	private HashMap<String,AbstractNode> descendants         = new HashMap<String,AbstractNode>();
	//new merging
	private HashMap<IBinding, AbstractNode> funcDeclarations = new HashMap<IBinding, AbstractNode>();
	private HashMap<IBinding, AbstractNode> typeDeclarations = new HashMap<IBinding, AbstractNode>();

	public HybridTreeBuilder(String workspace){
		root = new WorkspaceNode(workspace);
	}

	@Override
	public AbstractNode addChild(AbstractNode parent, AbstractNode child){

		String childsHybridId = combine(TreeBuilder.PATH_SEPARATOR, parent.getHybridId(), child.getScopeUniqueName());
		AbstractNode existing = parent.getChildBy(childsHybridId);

		prepareDeclDefMerging(child);

		if(existing != null){
			mergeChildrenOf(child, existing);
			child = existing;
		}else{
			child.setHybridId(childsHybridId);
			child = parent.add(child);
			PreOrderTreeVisitor visitor = new PreOrderTreeVisitor() {
				@Override
				void visitNode(AbstractNode n) {
					descendants.put(n.getHybridId(), n);
				}
			};
			visitor.visit(child);
		}

		return child;
	}

	private void mergeChildrenOf(AbstractNode node, AbstractNode intoParent){
		for(AbstractNode n : node.getChildren()){
			addChild(intoParent, n);
		}
	}

	public AbstractNode getChildBy(String hybridId){
		return descendants.get(hybridId);
	}

	private void prepareDeclDefMerging(AbstractNode child) {
		if(child.getNodeInfo().isFunctionDeclarator()){
			funcDeclarations.put(child.getNodeInfo().getBinding(), child);
		}else if(child.getNodeInfo().isElaboratedTypeSpecifier()){
			typeDeclarations.put(child.getNodeInfo().getTypeBinding(), child);
		}
	}

	public void mergeDeclarationsAndDefinitions(IASTTranslationUnit tu) {

		for (IASTDeclaration decl : tu.getDeclarations()) {
			if(decl instanceof IASTSimpleDeclaration){
				IBinding declBinding = null;
				if(isTypeDecl(decl)){
					declBinding = getTypeBinding(decl);
					findDeclsOfDefs(tu, declBinding, true);
				}else{
					declBinding = getFuncBinding(tu, ((IASTSimpleDeclaration)decl).getDeclarators());
					findDecslOfDefs(tu, declBinding);
				}
			}
		}
		removeAllBindings();
	}


	private void findDecslOfDefs(IASTTranslationUnit tu, IBinding declBinding) {
		findDeclsOfDefs(tu, declBinding, false);
	}

	private void findDeclsOfDefs(IASTTranslationUnit tu, IBinding declBinding, boolean isTypeDecl) {
		if(declBinding != null){
			for(IName name : tu.getDefinitions(declBinding)){
				if(name instanceof IASTName && name.isDefinition()){
					IASTName iastName = (IASTName)name;
					AbstractNode foundDecl;
					if(isTypeDecl){
						foundDecl = typeDeclarations.get(iastName.getBinding());
					}else{
						foundDecl = funcDeclarations.get(tu.getIndex().adaptBinding(iastName.getBinding()));
					}
					removeFoundDecl(foundDecl);
				}
			}
		}
	}

	private boolean isTypeDecl(IASTDeclaration decl) {
		return ((IASTSimpleDeclaration) decl).getDeclSpecifier() instanceof ICPPASTElaboratedTypeSpecifier;
	}

	private IBinding getTypeBinding(IASTDeclaration decl) {
		ICPPASTElaboratedTypeSpecifier typeDecl = (ICPPASTElaboratedTypeSpecifier)((IASTSimpleDeclaration) decl).getDeclSpecifier();
		IBinding declBinding = typeDecl.getName().getBinding();
		return declBinding;
	}

	private IBinding getFuncBinding(IASTTranslationUnit tu,IASTDeclarator[] declarators) {
		IBinding declBinding = null;
		if(declarators.length > 0){
			declBinding = tu.getIndex().adaptBinding(declarators[0].getName().getBinding());
			if(declBinding == null){
				return declarators[0].getName().getBinding();
			}
		}
		return declBinding;
	}



	private void removeFoundDecl(AbstractNode foundDecl) {
		if(foundDecl != null){
			foundDecl.removeFromParent();
			foundDecl = null;
		}
	}


	private void removeAllBindings() {
		funcDeclarations.clear();
		typeDeclarations.clear();
	}

}
