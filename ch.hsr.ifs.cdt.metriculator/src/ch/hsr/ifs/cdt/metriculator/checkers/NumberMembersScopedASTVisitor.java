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

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.ScopedASTVisitor;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class NumberMembersScopedASTVisitor extends ScopedASTVisitor {
	
	String key = AbstractMetric.getKeyFor(NumberMembersMetric.class);

	public NumberMembersScopedASTVisitor(AbstractNode scopeNode, TreeBuilder builder) {
		super(scopeNode, builder);
		shouldVisitDeclarators = true;
	}

	@Override
	public int visit(IASTDeclSpecifier declSpec) {
		int process_status = super.visit(declSpec);
		
		if(declSpec instanceof IASTCompositeTypeSpecifier){	
			
			IASTDeclaration[] members = ((IASTCompositeTypeSpecifier) declSpec).getMembers();
			
			for (IASTDeclaration member : members) {
				
				if(!(member instanceof ICPPASTVisibilityLabel) && !(member instanceof ICPPASTUsingDeclaration)){			
					if(isNotFriend(member)){
						count();
					}
				}
			}
		}
		
		return process_status;
	}

	private boolean isNotFriend(IASTDeclaration member) {
		if(member instanceof ICPPASTFunctionDefinition){
			return !((ICPPASTFunctionDefinition)member).getDeclSpecifier().getRawSignature().contains("friend");
		}
		
		if(member instanceof IASTSimpleDeclaration){
			return !((IASTSimpleDeclaration)member).getDeclSpecifier().getRawSignature().contains("friend");
		}
		
		return true;
	}
	
	private void count(){
		scopeNode.setNodeValue(key, scopeNode.getNodeValue(key) + 1);
	}
}
