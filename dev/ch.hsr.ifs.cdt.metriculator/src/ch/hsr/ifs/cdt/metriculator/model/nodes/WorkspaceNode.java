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

import java.util.Collection;

import ch.hsr.ifs.cdt.metriculator.resources.Icon;

public class WorkspaceNode extends AbstractNode {
	
	public WorkspaceNode(String name) {
		super(name);
		setHybridId(name);
	}

	@Override
	public String getIconPath() {
		return Icon.Size16.WORKSPACE;
	}

	public void add(Collection<AbstractNode> all) {
		for(AbstractNode child : all){
			add(child);
		}
	}
}
