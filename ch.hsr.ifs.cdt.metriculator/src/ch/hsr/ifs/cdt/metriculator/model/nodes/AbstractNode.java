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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.INodeVisitorAccepter;
import ch.hsr.ifs.cdt.metriculator.model.AbstractTreeBuilder;

public abstract class AbstractNode implements Cloneable, INodeVisitorAccepter {

	private final static String EMPTY_STRING = ""; //$NON-NLS-1$
	protected String scopeName;
	protected AbstractNode parent;
	private String hybridId;
	private EditorInfo editorInfo;

	private String cachedPath;

	private HashMap<String, AbstractNode> children        = new HashMap<String, AbstractNode>();
	private ArrayList<AbstractNode>		  orderedChildren = new ArrayList<AbstractNode>();
	private HashMap<String, CompositeValue> metricValues  = new HashMap<String, CompositeValue>();
	
	protected AbstractNode(String scopeUniqueName) {
		setScopeName(scopeUniqueName);
	}
	
	public EditorInfo getEditorInfo(){
		return editorInfo;
	}

	protected AbstractNode(String scopeUniqueName, IASTNode astNode) {
		this(scopeUniqueName);
		if(astNode != null){
			editorInfo = new EditorInfo(astNode);
		}
	}

	public Collection<AbstractNode> getChildren() {
		return Collections.unmodifiableCollection(orderedChildren);
	}

	public AbstractNode getParent() {
		return parent;
	}

	/**
	 * @param childNode to add
	 * @return AbstractNode childNode
	 */
	public AbstractNode add(AbstractNode childNode) {

		childNode.parent = this;
		children.put(childNode.getHybridId(), childNode);
		orderedChildren.add(childNode);

		childNode.resetPath();
		
		return childNode;
	}
	
	public void resetPath() {
		cachedPath = null;
	}

	/**
	 * @return child with same hybridId (guid). Returns null if not available.
	 */
	public AbstractNode getChildBy(String hybridId) {
		return children.get(hybridId);
	}

	/**
	 * @return children of type type. Returns empty collection if none found.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractNode> Collection<T> getChildren(Class<T> type) {
		Collection<T> nodes = new ArrayList<T>();
		for (AbstractNode n : children.values()) {
			if (type.isInstance(n)) {
				nodes.add((T) n);
			}
		}
		return nodes;
	}

	public CompositeValue getValueOrDefaultOf(String metricKey) {
		CompositeValue metricValue = metricValues.get(metricKey);
		if (metricValue == null) {
			metricValue = new CompositeValue();
			metricValues.put(metricKey, metricValue);
		}
		return metricValue;
	}
	
	/**
	 * Aggregate the node values of all descendant nodes and itself.
	 * */
	public CompositeValue getValueOf(AbstractMetric metric) {

		if(!metric.useCachedValue){
			metric.aggregate(this);
		}

		return metricValues.get(metric.getKey());
	}

	public String getScopeUniqueName() {
		return scopeName;
	}

	/**
	 * normalize string, remove new lines and extra whitespace
	 * */
	protected void setScopeName(String scopeName) {
		this.scopeName = scopeName.replaceAll("[\n\r\t]", EMPTY_STRING).replaceAll("\\s{2,}", EMPTY_STRING);
	}
	
	public String getScopeName(){
		return scopeName;
	}
	
	private String getPath(StringBuilder pathBuilder) {

		if(pathBuilder.length() == 0){
			pathBuilder.insert(0, getScopeUniqueName());
		}else{
			pathBuilder.insert(0, AbstractTreeBuilder.PATH_SEPARATOR).insert(0, getScopeUniqueName());
		}

		if (parent != null) {
			return parent.getPath(pathBuilder);
		}		
		return pathBuilder.toString();
	}

	/**
	 * @return String path from the root node down to this one. The concatenated scopeUniqueNames are separated by period.
	 * <code>e.g. Workspace1.Project0.FileX.ClassA</code>
	 * */
	public String getPath() {
		if(cachedPath == null){
			cachedPath = getPath(new StringBuilder(EMPTY_STRING));
		}
		return cachedPath;
	}

	public int getAggregatedValueOf(AbstractMetric abstractMetric){
		return getValueOf(abstractMetric).aggregatedValue;
	}
	
	/**
	 * @param key provided by AbstractMetric instance
	 * */
	public int getNodeValue(String key){
		return getValueOrDefaultOf(key).nodeValue;
	}
	
	/**
	 * @param key provided by AbstractMetric instance
	 * */
	public void setNodeValue(String key, int value){
		getValueOrDefaultOf(key).nodeValue = value;
	}

	@Override
	public String toString() {
		return getScopeName();
	}

	public AbstractNode getRoot() {
		if(parent == null){
			return this;
		}
		
		return getParent().getRoot();
	}

	public String getHybridId() {
		return hybridId;
	}

	public void setHybridId(String hybridId) {
		this.hybridId = hybridId;
		resetPath();
	}

	public void removeFromParent() {
		parent.removeChild(this);
		
		resetPath();
	}

	public void removeChild(AbstractNode node) {
		children.remove(node.hybridId);
		orderedChildren.remove(node);
		
		node.resetPath();
	}
	
	/**
	 * Clones all primitive member variables and metric values. 
	 * The new node has no children and has the same parent as the original node.
	 * */
	public AbstractNode shallowClone() {
		try { 
			AbstractNode clone = (AbstractNode) super.clone();
			
			clone.parent          = parent;
			clone.children        = new HashMap<String, AbstractNode>();
			clone.orderedChildren = new ArrayList<AbstractNode>();
			clone.metricValues    = new HashMap<String, CompositeValue>();
			
			for(String k : metricValues.keySet()){
				clone.metricValues.put(k, CompositeValue.copy(metricValues.get(k)));
			}
			
			return clone;
		} 
		catch (CloneNotSupportedException e) { 
			throw new InternalError(e.getMessage()); 
		}
	}

	public void addNodeValuesFrom(AbstractNode copy) {
		for(String metricKey : metricValues.keySet()){
			metricValues.get(metricKey).nodeValue += copy.metricValues.get(metricKey).nodeValue;
		}
	}
	
	public abstract String getIconPath();
	
	public static class EditorInfo{
		
		private String filePath = "";
		private int nodeOffSet;
		private int nodeLength;
		private int endingLineNumber;
		private int startingLineNumber;
		private int nodeOffSetStart;
		private int nodeOffsetEnd;
		private boolean isEclosedInMacroExpansion;
		
		public EditorInfo(IASTNode astNode){
			prepareFilePath(astNode);
			prepareNodeLocations(astNode);
			prepareProblemLocation(astNode);
		}		

		public String getFilePath() {
			return filePath;
		}

		public int getNodeOffset() {
			return nodeOffSet;
		}

		public int getNodeLength() {
			return nodeLength;
		}
		
		public IProblemLocation createAndGetProblemLocation(IFile file) {
			IProblemLocationFactory locFactory = CodanRuntime.getInstance().getProblemLocationFactory();
			if(isEclosedInMacroExpansion || startingLineNumber == endingLineNumber){
				return locFactory.createProblemLocation(file, nodeOffSetStart, nodeOffsetEnd, startingLineNumber);
			}
			return locFactory.createProblemLocation(file, startingLineNumber);
		}	

		private void prepareFilePath(IASTNode astNode) {
			filePath = astNode.getTranslationUnit().getFilePath();
		}

		private void prepareNodeLocations(IASTNode astNode) {
			nodeOffSet = astNode.getNodeLocations()[0].getNodeOffset();
			nodeLength = astNode.getNodeLocations()[0].getNodeLength();
		}

		private void prepareProblemLocation(IASTNode astNode) {
			IASTFileLocation astLocation       = astNode.getFileLocation();

			startingLineNumber = astLocation.getStartingLineNumber();

			if (isEnclosedInMacroExpansion(astNode) && astNode instanceof IASTName) {
				isEclosedInMacroExpansion = true;
				IASTImageLocation imageLocation = ((IASTName) astNode).getImageLocation();

				if (imageLocation != null) {
					nodeOffSetStart = imageLocation.getNodeOffset();
					nodeOffsetEnd   = nodeOffSetStart + imageLocation.getNodeLength();
					return;
				}
			}

			endingLineNumber = astLocation.getEndingLineNumber();
			if (startingLineNumber == endingLineNumber) {
				nodeOffSetStart = astLocation.getNodeOffset();
				nodeOffsetEnd = nodeOffSetStart + astLocation.getNodeLength();
				return;
			}
		}

		private static boolean isEnclosedInMacroExpansion(IASTNode node) {
			IASTNodeLocation[] nodeLocations = node.getNodeLocations();
			return nodeLocations.length == 1 && nodeLocations[0] instanceof IASTMacroExpansionLocation;
		}		
	}
}
