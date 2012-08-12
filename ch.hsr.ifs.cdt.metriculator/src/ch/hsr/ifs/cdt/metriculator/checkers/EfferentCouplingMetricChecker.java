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

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.resources.MetricLabels;

public class EfferentCouplingMetricChecker extends AbstractMetricChecker{
	
	public static final String PREF_EFFERENTCOUPLING_MAXIMUM_PER_TYPE = "max_per_type"; //$NON-NLS-1$
	public static final String EFFERENTCOUPLING_PROBLEM_ID            = "ch.hsr.ifs.cdt.metriculator.efferentcoupling";
	private EfferentCouplingMetric metric;

	public EfferentCouplingMetricChecker(){
		super(EFFERENTCOUPLING_PROBLEM_ID);
		metric = new EfferentCouplingMetric(this, MetricLabels.EfferentCouplingMetric_name, MetricLabels.EfferentCouplingMetric_description);
		MetriculatorPluginActivator.getDefault().registerMetric(metric);
	}
	
	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);

		addPreference(problem, PREF_EFFERENTCOUPLING_MAXIMUM_PER_TYPE, MetricLabels.EFFERENTCOUPLING_Maximum_Per_Type, "4"); //$NON-NLS-1$
		MetriculatorPluginActivator.getDefault().getObservable().addObserver(this);
	}
	
	@Override
	protected void processTanslationUnit(IASTTranslationUnit tu) {
		EfferentCouplingScopedASTVisitor visitor = new EfferentCouplingScopedASTVisitor(currentScopeNode, builder);
		visitor.add(this);
		tu.accept(visitor);
	}

	protected void reportProblemsFor(AbstractNode node){
		if(node instanceof FunctionNode){
			Integer maxCouplingPerType = getPreferenceAsInteger(EFFERENTCOUPLING_PROBLEM_ID, PREF_EFFERENTCOUPLING_MAXIMUM_PER_TYPE, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxCouplingPerType){
				reportProblem(EFFERENTCOUPLING_PROBLEM_ID, node, maxCouplingPerType);
			}				
		}
	}
}
