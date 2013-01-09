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

import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTWhileStatement;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.ScopedASTVisitor;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class McCabeScopedASTVisitor extends ScopedASTVisitor {
	
	String key = AbstractMetric.getKeyFor(McCabeMetric.class);

	public McCabeScopedASTVisitor(AbstractNode scopeNode, AbstractTreeBuilder builder) {
		super(scopeNode, builder);
		shouldVisitExpressions     = true;
		shouldVisitTranslationUnit = true;
	}

	@Override
	public int visit(IASTStatement statement) {
		if(isComplexity(statement)){
			count();
		}
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IASTExpression expression) {
		if(expression instanceof ICPPASTBinaryExpression && isComplexityLogic((ICPPASTBinaryExpression) expression)){
			if(isNotOverloaded((ICPPASTBinaryExpression) expression)){
				count();
			}
		}else if(expression instanceof IASTConditionalExpression){
			count();
		}
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IASTTranslationUnit tu) {
		for(IASTPreprocessorStatement statement : tu.getAllPreprocessorStatements()){
			if(isComplexityPreProcessor(statement)){
				count();
			}
		}
		return PROCESS_CONTINUE;
	}

	private boolean isNotOverloaded(ICPPASTBinaryExpression expression) {
		return 	expression.getOverload() == null;
	}

	private void count(){
		scopeNode.setNodeValue(key, scopeNode.getNodeValue(key) + 1);
	}

	private boolean isComplexity(IASTStatement statement) {
		return statement instanceof ICPPASTIfStatement || statement instanceof ICPPASTForStatement || statement instanceof ICPPASTWhileStatement || statement instanceof IASTCaseStatement || statement instanceof ICPPASTCatchHandler;
	}

	private boolean isComplexityPreProcessor(
			IASTPreprocessorStatement statement) {
		return statement instanceof IASTPreprocessorIfStatement || statement instanceof IASTPreprocessorIfdefStatement || statement instanceof IASTPreprocessorIfndefStatement || statement instanceof IASTPreprocessorElifStatement;
	}

	private boolean isComplexityLogic(ICPPASTBinaryExpression expression) {
		return expression.getOperator() == ICPPASTBinaryExpression.op_logicalAnd || expression.getOperator() == ICPPASTBinaryExpression.op_logicalOr;
	}

}
