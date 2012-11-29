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

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.cdt.metriculator.model.INodeVisitor;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitorAccepter;
import ch.hsr.ifs.cdt.metriculator.resources.Icon;

public class ProjectNode extends AbstractNode implements INodeVisitorAccepter {

	private IProject project;
	
	public ProjectNode(IProject project) {
		super(project.getName());
		this.project = project;
	}

	@Override
	public void accept(INodeVisitor v){
		v.visit(this);
	}
	
	public ProjectNode(String projectName) {
		super(projectName);
	}

	public IProject getProject() {
		return project;
	}

	@Override
	public String getIconPath() {
		return Icon.Size16.C_PROJECT;
	}
}
