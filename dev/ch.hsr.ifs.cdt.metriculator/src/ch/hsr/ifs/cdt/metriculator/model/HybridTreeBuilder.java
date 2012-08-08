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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.index.IIndexBinding;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionDeclNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.MemberNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDeclNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public class HybridTreeBuilder extends TreeBuilder {

	private HashMap<String,AbstractNode> descendants     = new HashMap<String,AbstractNode>();
	private HashMap<IIndexBinding, MemberNode> declarations = new HashMap<IIndexBinding, MemberNode>();

	public HybridTreeBuilder(String workspace){
		root = new WorkspaceNode(workspace);
	}

	@Override
	public AbstractNode addChild(AbstractNode parent, AbstractNode child){

		String childsHybridId = combine(TreeBuilder.PATH_SEPARATOR, parent.getHybridId(), child.getScopeUniqueName());
		AbstractNode existing = parent.getChildBy(childsHybridId);

		prepareDeclBinding(child);

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

	private void prepareDeclBinding(AbstractNode child) {
		if(child instanceof FunctionDeclNode || child instanceof TypeDeclNode){
			declarations.put(((MemberNode) child).getIndexBinding(), (MemberNode) child);
		}
	}

	public void mergeDeclarationsAndDefinitions(IASTTranslationUnit tu) {

		for (IASTDeclaration decl : tu.getDeclarations()) {
			
			if(decl instanceof IASTSimpleDeclaration){
				IIndexBinding declBinding = null;
				
				if(isTypeDecl((IASTSimpleDeclaration) decl)){
					declBinding = getTypeBinding(tu, (IASTSimpleDeclaration) decl);
				}else{
					declBinding = getFuncBinding(tu, ((IASTSimpleDeclaration)decl).getDeclarators());
				}
				findDeclsOfDefs(tu, declBinding);
			}
		}
		deleteBindings();
	}

	private void findDeclsOfDefs(IASTTranslationUnit tu, IIndexBinding declBinding) {
		if(declBinding != null){
			for(IName name : tu.getDefinitions(declBinding)){
				
				if(name instanceof IASTName && name.isDefinition()){
					IASTName iastName = (IASTName)name;
					AbstractNode foundDecl = declarations.get(tu.getIndex().adaptBinding(iastName.getBinding()));
					removeFromTree(foundDecl);
				}
			}
		}
	}

	private boolean isTypeDecl(IASTSimpleDeclaration decl) {
		return decl.getDeclSpecifier() instanceof ICPPASTElaboratedTypeSpecifier;
	}

	private IIndexBinding getTypeBinding(IASTTranslationUnit tu, IASTSimpleDeclaration decl) {
		ICPPASTElaboratedTypeSpecifier typeDecl = (ICPPASTElaboratedTypeSpecifier)decl.getDeclSpecifier();
		
		return tu.getIndex().adaptBinding(typeDecl.getName().getBinding());
	}

	private IIndexBinding getFuncBinding(IASTTranslationUnit tu,IASTDeclarator[] declarators) {
		IIndexBinding declBinding = null;
		if(declarators.length > 0){
			declBinding = tu.getIndex().adaptBinding(declarators[0].getName().getBinding());
		}
		return declBinding;
	}

	private void removeFromTree(AbstractNode foundDecl) {
		if(foundDecl != null){
			foundDecl.removeFromParent();
		}
	}

	private void deleteBindings() {
		for (MemberNode node : declarations.values()) {
			node.clearBindings();
		}
		declarations.clear();
	}
}
