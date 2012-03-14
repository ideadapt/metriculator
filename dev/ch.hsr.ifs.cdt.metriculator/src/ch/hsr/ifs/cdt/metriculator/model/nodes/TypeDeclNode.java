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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;

import ch.hsr.ifs.cdt.metriculator.resources.Icon;


public class TypeDeclNode extends TypeNode {

	public TypeDeclNode(String scopeUniqueName) {
		super(scopeUniqueName);
	}

//	public CompositeTypeNode(ICPPASTElaboratedTypeSpecifier astNode) {
//		super(astNode.getName().toString(), astNode);
//	}
//	
	public TypeDeclNode(ICPPASTElaboratedTypeSpecifier astNode) {
		super(astNode.getName().toString(), astNode);
		typeKey = astNode.getKind();
	}

	@Override
	public String toString() {
		return isAnonymous() ? LogicNode.ANONYMOUS_LABEL : getScopeName();
	}

	@Override
	public String getIconPath() {
		int key = typeKey;// getNodeInfo().getTypeKey();

		switch(key){
			case ICPPASTCompositeTypeSpecifier.k_struct:
				return Icon.Size16.STRUCT;
			case ICPPASTCompositeTypeSpecifier.k_class:
				return Icon.Size16.CLASS;
			case ICPPASTCompositeTypeSpecifier.k_union:
				return Icon.Size16.UNION;
			default:
				return Icon.Size16.CLASS;
		}
	}

	@Override
	void prepareIsFriend(IASTNode astNode) {
		isFriend = ((ICPPASTDeclSpecifier)astNode).isFriend();	
	}

	@Override
	boolean prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTElaboratedTypeSpecifier) astNode).getName();
		binding  = name.resolveBinding();
		return binding != null;
	}

}
