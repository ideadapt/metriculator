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

package ch.hsr.ifs.cdt.metriculator.model.nodes;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import ch.hsr.ifs.cdt.metriculator.resources.Icon;

public class FunctionNode extends LogicNode {

	public FunctionNode(String name) {
		super(name);
	}

	public FunctionNode(ICPPASTFunctionDefinition fnNode) {
		super(fnNode);
		setAstNode(new NodeInfo(fnNode));

		setScopeUniqueName(fnNode.getDeclarator().getRawSignature());
	}

	public FunctionNode(ICPPASTFunctionDeclarator fnNode) {
		super(fnNode);
		setAstNode(new NodeInfo(fnNode));
		
		setScopeUniqueName(fnNode.getRawSignature());
	}

	private void setScopeUniqueName(String name) {
		setScopeName(name);
	}


	@Override
	public String toString() {
		return getScopeName();
	}

	@Override
	public String getIconPath() {
		return Icon.Size16.METHOD_PUBLIC;
	}

	@Override
	public boolean isAnonymous() {
		return getScopeName().trim().isEmpty();
	}
}
