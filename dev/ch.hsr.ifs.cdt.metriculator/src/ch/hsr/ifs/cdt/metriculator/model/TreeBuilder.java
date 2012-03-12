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

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.utils.PathUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;

public abstract class TreeBuilder {
	
	public AbstractNode root;
	public static final String PATH_SEPARATOR = "#";
	public static final String LOGIC_SEPARATOR = "::";
	
	public AbstractNode addChild(AbstractNode parent, AbstractNode child){
		child = parent.add(child);
		return child;
	}

	/**
	 * Creates a tree of FolderNodes and FileNodes that represents the project relative path of the tu.
	 * If the project relative path has only one segment (e.g. 'src' or 'test.h'), that segment is assumed to be a file.
	 * <pre>
	 * e.g. src/pimpl/Impl.cpp will result in 
	 * FolderNode, src
	 * --FolderNode, pimpl
	 * ----FileNode, Impl.cpp
	 * </pre>
	 * Where FileNode would be the return value of this method.
	 * @param project
	 * @param tu
	 * @return FileNode or FolderNode
	 */
	public static AbstractNode createTreeFromPath(ProjectNode projectNode, IASTTranslationUnit tu){
		IPath projRel = PathUtil.getProjectRelativePath(new Path(tu.getFilePath()), projectNode.getProject()).setDevice(null);	
		
		return createTreeFromPath(projectNode.getHybridId(), projRel, tu);
	}
	
	/**
	 * @see TreeBuilder.createTreeFromPath(ProjectNode, IASTTranslationUnit) for a description
	 * */
	public static AbstractNode createTreeFromPath(IPath projectRelativePath, IASTTranslationUnit tu){
		return createTreeFromPath("", projectRelativePath, tu);
	}
	
	/**
	 * @see TreeBuilder#createTreeFromPath(IProject, IASTTranslationUnit)
	 * @param projectRelativePath
	 * @param tu used in FileNode and FolderNode constructors, can be null
	 * @return
	 */
	public static AbstractNode createTreeFromPath(String hybridIdPrefix, IPath projectRelativePath, IASTTranslationUnit tu){
		AbstractNode currentNode = null;
		String segment           = null;
		int nrOfSegments         = projectRelativePath.segmentCount();
		
		for (int segmentIndex = 0; segmentIndex < nrOfSegments; segmentIndex++) {
			
			segment 				= projectRelativePath.segments()[segmentIndex];
			boolean isLastSegment 	= segmentIndex == nrOfSegments -1;			
			AbstractNode newNode 	= isLastSegment ? new FileNode(tu, segment) : new FolderNode(tu, segment);
			
			if (currentNode == null) {
				newNode.setHybridId(combine(TreeBuilder.PATH_SEPARATOR, hybridIdPrefix, newNode.getScopeUniqueName()));
				currentNode = newNode;
			} else {
				newNode.setHybridId(buildPath(hybridIdPrefix, currentNode, newNode));
				currentNode = currentNode.add(newNode);
			}
		}
		
		return currentNode;
	}
	
	private static String buildPath(String prefix, AbstractNode parent, AbstractNode child){
		
		if(prefix != null && prefix.length() > 0){
			return combine(TreeBuilder.PATH_SEPARATOR, prefix, parent.getPath(), child.getScopeUniqueName());
		}
		
		return combine(TreeBuilder.PATH_SEPARATOR, parent.getPath(), child.getScopeUniqueName());
	}
	
	/**
	 * @see http://stackoverflow.com/questions/1515437/java-function-for-arrays-like-phps-join/1515548#1515548
	 * */
	public static String combine(String delimeter, String... s) {
		int k = s.length;
		if (k == 0){
			return null;
		}
		StringBuilder out = new StringBuilder();
		out.append(s[0]);
		for (int x = 1; x < k; ++x){
			out.append(delimeter).append(s[x]);
		}
		return out.toString();
	}
}
