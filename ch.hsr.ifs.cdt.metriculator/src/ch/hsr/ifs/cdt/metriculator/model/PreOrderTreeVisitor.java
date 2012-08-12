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

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;


public abstract class PreOrderTreeVisitor{
	
	protected AbstractNode rootNode = null;

	protected void visit(AbstractNode n){
		
		visitNode(n);
		
		for(AbstractNode child : n.getChildren()){
			visit(child);
			leaveNode(child);
		}
	}
	
	abstract void visitNode(AbstractNode n);
	
	protected void leaveNode(AbstractNode n){
	}
}
