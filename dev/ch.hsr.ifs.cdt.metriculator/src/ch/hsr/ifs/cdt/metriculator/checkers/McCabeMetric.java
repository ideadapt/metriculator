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

package ch.hsr.ifs.cdt.metriculator.checkers;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.CompositeValue;

public class McCabeMetric extends AbstractMetric {
	
	public McCabeMetric(AbstractMetricChecker checker, String name, String description) {
		super(checker, name, description);
	}
	
	@Override
	public int aggregate(AbstractNode node) {
		CompositeValue metricValue = node.getValueOrDefaultOf(AbstractMetric.getKeyFor(McCabeMetric.class));
		metricValue.aggregatedValue = 0;

		for(AbstractNode child : node.getChildren()){
			metricValue.aggregatedValue += aggregate(child);
		}
		
		metricValue.aggregatedValue = metricValue.nodeValue + metricValue.aggregatedValue - node.getChildren().size() + 1;

		return metricValue.aggregatedValue;
	}
}
