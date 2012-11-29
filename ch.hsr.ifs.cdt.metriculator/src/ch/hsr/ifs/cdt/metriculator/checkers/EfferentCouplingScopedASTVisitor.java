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

package ch.hsr.ifs.cdt.metriculator.checkers;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.IScopeListener;
import ch.hsr.ifs.cdt.metriculator.model.ScopedASTVisitor;
import ch.hsr.ifs.cdt.metriculator.model.AbstractTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.LogicNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDeclNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;

public class EfferentCouplingScopedASTVisitor extends ScopedASTVisitor {

	String key = AbstractMetric.getKeyFor(EfferentCouplingMetric.class);
	private HashMap<LogicNode, HashSet<IIndexBinding>> countedBindingsInNode = new HashMap<LogicNode, HashSet<IIndexBinding>>();
	private LogicNode currType = null;

	private int typeNestingLevel = 0;

	private boolean isInType() {
		return typeNestingLevel > 0;
	}

	public EfferentCouplingScopedASTVisitor(final AbstractNode scopeNode, AbstractTreeBuilder builder) {
		super(scopeNode, builder);

		this.add(new IScopeListener() {

			private boolean isNotElaboratedType(AbstractNode node) {
				return node instanceof TypeDefNode && !(node instanceof TypeDeclNode);
			}

			@Override
			public void visiting(AbstractNode node) {
				if(isNotElaboratedType(node)){

					if(!countedBindingsInNode.containsKey(node)){					
						countedBindingsInNode.put((LogicNode) node, new HashSet<IIndexBinding>());
					}
					typeNestingLevel++;
					currType = (LogicNode) node;
				}
			}

			@Override
			public void leaving(AbstractNode node) {
				if(isNotElaboratedType(node)){
					typeNestingLevel--;
					if(typeNestingLevel <= 0){
						currType = null;
					}else{
						currType = (LogicNode) node.getParent();
					}
				}
			}			
		});
	}

	@Override
	public int visit(IASTDeclSpecifier declSpec) {
		int process_status = super.visit(declSpec);

		if(isInType()){

			IASTName name = null;
			if(declSpec instanceof ICPPASTNamedTypeSpecifier){
				name = ((ICPPASTNamedTypeSpecifier)declSpec).getName();
			}
			if(declSpec instanceof ICPPASTElaboratedTypeSpecifier){
				name = ((ICPPASTElaboratedTypeSpecifier)declSpec).getName();
			}

			if(name != null){
				IIndexBinding specBinding = getBindingFor(name, declSpec.getTranslationUnit());

				if(countedBindingsInNode.get(currType) != null && !countedBindingsInNode.get(currType).contains(specBinding)){
					count();
					countedBindingsInNode.get(currType).add(specBinding);
				}
			}
		}
		return process_status;
	}

	private void count(){
		scopeNode.setNodeValue(key, scopeNode.getNodeValue(key) + 1);
	}
	
	private static IIndexBinding getBindingFor(IASTName name, IASTTranslationUnit tu) {
		IIndexBinding indexBinding;

		IIndex index = tu.getIndex();
		indexBinding = index.adaptBinding(name.resolveBinding());

		return indexBinding;
	}
}
