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
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public abstract class PreOrderTreeVisitor implements INodeVisitor{
	
	protected AbstractNode rootNode = null;
	
	@Override
	public void visit(AbstractNode n){
		
		visitNode(n);
		
		for(AbstractNode child : n.getChildren()){
			child.accept(this);
			leaveNode(child);
		}
	}
	
	protected abstract void visitNode(AbstractNode n);
	
	protected void leaveNode(AbstractNode n){
	}

	@Override
	public void visit(WorkspaceNode n) {
		visit((AbstractNode)n);
	}

	@Override
	public void visit(ProjectNode n) {
		visit((AbstractNode)n);
	}
	
	@Override
	public void visit(NamespaceNode n) {
		visit((AbstractNode)n);
	}
	
	@Override
	public void visit(FolderNode n) {
		visit((AbstractNode)n);
	}
}
