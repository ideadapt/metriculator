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

package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.hsr.ifs.cdt.metriculator.model.NodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

/**
 * @author Ueli Kunz
 * */
class NodeViewerFilter extends ViewerFilter{
	private NodeFilter currFilter = NodeFilter.none();

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		AbstractNode node = (AbstractNode) element;
		
		return currFilter.canPassThrough(node);
	}
	
	public boolean isAnyFilterActive(){
		return !(currFilter instanceof NodeFilter.NoneFilter);
	}
	
	public NodeFilter getNodeFilter() {
		return currFilter;
	}

	public void setNodeFilter(NodeFilter filter) {
		currFilter = filter;
	}
}