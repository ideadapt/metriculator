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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;

public abstract class NodeFilter{

	public abstract boolean canPassThrough(AbstractNode node);
	
	public Collection<AbstractNode> takeFrom(Collection<AbstractNode> nodes){
		
		Collection<AbstractNode> filtered = new ArrayList<AbstractNode>();
		
		for(AbstractNode n : nodes){
			if(canPassThrough(n)){
				filtered.add(n);
			}
		}
		return Collections.unmodifiableCollection(filtered);
	}

	public static class CompositeNodeFilter extends NodeFilter{

		@Override
		public boolean canPassThrough(AbstractNode node) {
			return node instanceof TypeDefNode;
		}
	}
	public static CompositeNodeFilter composite(){
		return new CompositeNodeFilter();
	}
	
	public static class NoneFilter extends NodeFilter{
		
		@Override
		public boolean canPassThrough(AbstractNode node) {
			return true;
		}
	}
	public static NoneFilter none() {
		return new NoneFilter();
	}
	
	public static class NamespaceNodeFilter extends NodeFilter {

		@Override
		public boolean canPassThrough(AbstractNode node) {
			return node instanceof NamespaceNode;
		}
	}
	public static NamespaceNodeFilter namespace(){
		return new NamespaceNodeFilter();
	}

	public static class FunctionNodeFilter extends NodeFilter {

		@Override
		public boolean canPassThrough(AbstractNode node) {
			return node instanceof FunctionNode;
		}
	}
	public static FunctionNodeFilter function(){
		return new FunctionNodeFilter();
	}

	public static class FileNodeFilter extends NodeFilter {

		@Override
		public boolean canPassThrough(AbstractNode node) {
			return node instanceof FileNode;
		}
	}
	public static FileNodeFilter file(){
		return new FileNodeFilter();
	}
	
}