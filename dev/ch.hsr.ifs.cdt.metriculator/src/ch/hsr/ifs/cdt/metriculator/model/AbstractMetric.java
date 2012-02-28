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
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeValue;

abstract public class AbstractMetric {
	
	protected AbstractMetricChecker checker;
	private String name;
	private String description;
	public boolean useCachedValue = false;
	
	protected AbstractMetric(AbstractMetricChecker checker, String name, String description){
		this.checker     = checker;
		this.name        = name;
		this.description = description;
	}

	public String getKey(){
		return getClass().getName();
	}
	
	public int aggregate(AbstractNode node){
		CompositeValue metricValue  = node.getValueOrDefaultOf(getKey());
		metricValue.aggregatedValue = 0;

		for(AbstractNode child : node.getChildren()) {
			metricValue.aggregatedValue += aggregate(child);
		}

		metricValue.aggregatedValue += metricValue.nodeValue;

		return metricValue.aggregatedValue;		
	}
	
	public AbstractMetricChecker getChecker(){
		return checker;
	}

	public static String getKeyFor(Class<? extends AbstractMetric> metric) {
		return metric.getName();
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
