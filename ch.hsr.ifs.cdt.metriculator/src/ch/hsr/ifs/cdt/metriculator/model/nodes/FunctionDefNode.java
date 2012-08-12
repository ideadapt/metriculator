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

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

public class FunctionDefNode extends FunctionNode {

	public FunctionDefNode(ICPPASTFunctionDefinition fnNode) {
		super(fnNode.getDeclarator().getRawSignature(), fnNode);
	}
	
	public FunctionDefNode(String scopeUniqueName) {
		super(scopeUniqueName);
	}

	@Override
	protected IASTName getASTName(IASTNode astNode) {
		return ((ICPPASTFunctionDefinition)astNode).getDeclarator().getName();
	}

}
