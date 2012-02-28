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

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;

public abstract class AbstractNode implements Cloneable {

	private static String emptyString = "";
	protected String scopeName;
	protected AbstractNode parent;
	private String hybridId;
	private NodeInfo nodeInfo;

	public String cachedPath;

	private HashMap<String, AbstractNode> children        = new HashMap<String, AbstractNode>();
	private ArrayList<AbstractNode>		  orderedChildren = new ArrayList<AbstractNode>();
	private HashMap<String, CompositeValue> metricValues  = new HashMap<String, CompositeValue>();
	
	protected AbstractNode(String scopeUniqueName) {
		this.scopeName = scopeUniqueName;
		nodeInfo = new NodeInfo();
	}

	public AbstractNode(IASTNode astNode) {
		nodeInfo = new NodeInfo(astNode);
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
		if(this instanceof ILogicNode){
			return new StringBuilder(scopeName).append(nodeInfo.getASTNodeHash() == null ? "" : nodeInfo.getASTNodeHash()).toString();
		}else{
			return scopeName;
		}
	}

	protected void setScopeName(String scopeName) {
		// normalize string, remove new lines and extra whitespace
		this.scopeName = scopeName.replaceAll("[\n\r\t]", emptyString).replaceAll("\\s{2,}", emptyString);
	}
	
	public String getScopeName(){
		return scopeName;
	}
	
	private String getPath(StringBuilder pathBuilder) {

		if(pathBuilder.length() == 0){
			pathBuilder.insert(0, getScopeUniqueName());
		}else{
			pathBuilder.insert(0, TreeBuilder.PATH_SEPARATOR).insert(0, getScopeUniqueName());
		}

		if (parent != null) {
			return parent.getPath(pathBuilder);
		}		
		return pathBuilder.toString();
	}

	/**
	 * @return String path from the root node down to this one. Each nodes scopeUniqueName is separated by a period.
	 * <code>e.g. Workspace1.Project0.FileX.ClassA</code>
	 * */
	public String getPath() {
		if(cachedPath == null){
			cachedPath = getPath(new StringBuilder(emptyString));
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

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}
	
	protected void setAstNode(NodeInfo astNode) {
		this.nodeInfo = astNode;
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
	 * Clones all primitive member variables, metric values. The new node has no children and has the same parent as the original node.
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
}
