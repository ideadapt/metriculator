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

import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.ScopedASTVisitor;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo.FuncDefNodeInfo;

public class NumberParamsScopedASTVisitor extends ScopedASTVisitor {

	String key = AbstractMetric.getKeyFor(NumberParamsMetric.class);

	public NumberParamsScopedASTVisitor(AbstractNode scopeNode, TreeBuilder builder) {
		super(scopeNode, builder);
		shouldVisitParameterDeclarations = true;
	}
	
	@Override
	public int visit(IASTParameterDeclaration parameterDeclaration) {
		if(parameterDeclaration.getParent() instanceof ICPPASTFunctionDeclarator){
			if(scopeNode.getNodeInfo() instanceof FuncDefNodeInfo){
				if(parameterDeclaration.getParent().getParent() instanceof ICPPASTFunctionDefinition){
					count();
				}
			}else{
				count();				
			}
		}
		return PROCESS_CONTINUE;
	}

	private void count(){
		scopeNode.setNodeValue(key, scopeNode.getNodeValue(key) + 1);
	}
}
