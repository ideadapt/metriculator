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

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.utils.PathUtil;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.resources.Icon;

public class FileNode extends AbstractNode {

	private String projectRelativePath;
	private boolean isHeaderUnit = false;
	
	public FileNode(String name) {
		super(name);
	}
	
	public FileNode(IASTTranslationUnit tu, String filename) {
		super(filename, tu);
		if(tu != null){
			isHeaderUnit = tu.isHeaderUnit();
		}
	}
	
	public boolean isHeaderUnit() {
		return isHeaderUnit;
	}

	@Override
	public String toString() {

		if(parent instanceof ProjectNode && ((ProjectNode)parent).getProject() != null){
			
			// in test mode getLocation returns null => only shorten paths if not in test mode
			if(((ProjectNode)parent).getProject().getLocation() != null && projectRelativePath == null){
				projectRelativePath = PathUtil.getProjectRelativePath(
						new Path(getScopeUniqueName()), 
						((ProjectNode)parent).getProject()).setDevice(null).toOSString();
			}
		
			return projectRelativePath != null ? projectRelativePath : super.toString();
		}
		return super.toString();
	}

	@Override
	public String getIconPath() {
		
		if(isHeaderUnit()){
			return Icon.Size16.H_FILE;
		}
		
		return Icon.Size16.C_FILE;
	}
}
