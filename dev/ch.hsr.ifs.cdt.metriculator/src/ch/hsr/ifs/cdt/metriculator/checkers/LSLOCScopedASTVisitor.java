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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTWhileStatement;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.ScopedASTVisitor;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class LSLOCScopedASTVisitor extends ScopedASTVisitor {

	private List<IASTNode> astNodesToIgnore = new ArrayList<IASTNode>();
	private String key = AbstractMetric.getKeyFor(LSLOCMetric.class);

	public LSLOCScopedASTVisitor(AbstractNode scopeNode, TreeBuilder builder) {
		super(scopeNode, builder);
	}

	private void count(IASTNode node){
		if(!astNodesToIgnore.contains(node)){
			scopeNode.setNodeValue(key, scopeNode.getNodeValue(key) + 1);
		}
	}
	
	/**
	 * Special treatment for namespace scope required, 
	 * since the ast does not provide a IASTCompoundStatement for namespace declarations (namespace x{ }).
	 * (function nodes contain a IASTCompoundStatement as last child and will be counted in the IASTStatement visitor).
	 * */
	@Override
	public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
		super.visit(namespaceDefinition);
		count(namespaceDefinition);
		return PROCESS_CONTINUE;
	}

	@Override
	public int visit(IASTDeclaration declaration) {
		super.visit(declaration);
		if(declaration instanceof IASTSimpleDeclaration){
			// ignore class/struct declaration, compositetypespecifiers are visited and counted separately
			if(declaration.getChildren().length > 0 && declaration.getChildren()[0] instanceof ICPPASTCompositeTypeSpecifier){
				astNodesToIgnore.add(declaration);
			}else{
				count(declaration);
			}
		}
		else if(declaration instanceof ICPPASTUsingDirective){
			count(declaration);
		}
		
		return PROCESS_CONTINUE;
	}
	
	@Override
	public int visit(IASTDeclSpecifier declSpec) {
		int process_status = super.visit(declSpec);
		if(declSpec instanceof ICPPASTCompositeTypeSpecifier){
			
			IASTNode[] children = declSpec.getParent().getChildren();
			if(children.length > 1 && children[1] instanceof ICPPASTDeclarator){
				count(children[1]);
			}
			
			count(declSpec);
		}
		return process_status;
	}


	@Override
	public int visit(IASTStatement statement) {
		if(shouldAnalyseChildrenOf(statement)){
			
			int currChildNumber = 0;
			int nrOfChildren = statement.getChildren().length;
			
			for(IASTNode child : statement.getChildren()){
				++currChildNumber;
				
				if(!(child instanceof IASTStatement)){
					astNodesToIgnore.add(child);
				}
				
				boolean isLastChild = currChildNumber == nrOfChildren;
				boolean isElseChild = !isLastChild && statement instanceof ICPPASTIfStatement && child instanceof IASTCompoundStatement;
				boolean isNotLastChildAndNotCatch = !isLastChild && !(child instanceof ICPPASTCatchHandler);

				if(isElseChild || isNotLastChildAndNotCatch || child instanceof IASTCompoundStatement || child instanceof IASTNullStatement){
					astNodesToIgnore.add(child);
				}
				
				if(isDeclarationWithSimpleDeclaration(child)){
					astNodesToIgnore.add(child.getChildren()[0]);
				}
			}
		}
		
		if(isDeclarationWithSimpleDeclaration(statement) || isDeclarationWithUsingDirective(statement)){
			astNodesToIgnore.add(statement);
		}
		
		if(statement instanceof IASTCaseStatement || statement instanceof IASTDefaultStatement){
			astNodesToIgnore.add(statement);
		}
		
		count(statement);
		
		return PROCESS_CONTINUE;			
	}

	private boolean shouldAnalyseChildrenOf(IASTStatement statement) {
		return isIterationStatement(statement) || 
				statement instanceof ICPPASTIfStatement || statement instanceof IASTSwitchStatement ||
				statement instanceof ICPPASTTryBlockStatement || statement instanceof ICPPASTCatchHandler;
	}

	private boolean isIterationStatement(IASTStatement statement){
		return statement instanceof IASTDoStatement || statement instanceof ICPPASTForStatement || statement instanceof ICPPASTWhileStatement;
	}
	
	/**
	 * This method identifies declaration statements that encapsulate an IASTSimpleDeclaration.
	 * */
	private boolean isDeclarationWithSimpleDeclaration(IASTNode statement){
		return statement instanceof IASTDeclarationStatement 
				&& statement.getChildren().length > 0 
				&& statement.getChildren()[0] instanceof IASTSimpleDeclaration;
	}
	
	/**
	 * This method identifies declaration statements that encapsulate an ICPPASTUsingDirective.
	 * 
	 * using directives are not treated the same way in all scopes. The declaration visitor is called for every using directive. 
	 * on class / function level the using directive is encapsulated as a child in a IASTDeclarationStatement node.	
	 * the enclosing IASTDeclarationStatement should be ignored, otherwise one using directive could count 2.
	 * */
	private boolean isDeclarationWithUsingDirective(IASTStatement statement) {
		return statement instanceof IASTDeclarationStatement 
				&& statement.getChildren().length > 0 
				&& statement.getChildren()[0] instanceof ICPPASTUsingDirective;
	}

}
