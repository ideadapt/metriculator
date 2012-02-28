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

public class NumberParamsMetricChecker extends AbstractMetricChecker{
	
	public static final String PREF_NBPARAMS_MAXIMUM_PER_FUNCTION = "max_per_function"; //$NON-NLS-1$
	public static final String NBPARAMS_PROBLEM_ID                = "ch.hsr.ifs.cdt.metriculator.nbparams";
	private NumberParamsMetric metric;

	public NumberParamsMetricChecker(){
		super(NBPARAMS_PROBLEM_ID);
		metric = new NumberParamsMetric(this, MetricLabels.NumberParamsMetric_name, MetricLabels.NumberParamsMetric_description);
		MetriculatorPluginActivator.getDefault().registerMetric(metric);
	}
	
	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);

		addPreference(problem, PREF_NBPARAMS_MAXIMUM_PER_FUNCTION, MetricLabels.NBPARAMS_Maximum_Per_Function, "4"); //$NON-NLS-1$
	}
	
	@Override
	protected void processTanslationUnit(IASTTranslationUnit tu) {
		NumberParamsScopedASTVisitor visitor = new NumberParamsScopedASTVisitor(currentScopeNode, builder);
		visitor.add(this);
		tu.accept(visitor);
	}

	@Override
	protected void reportProblemsFor(AbstractNode node){
		if(node instanceof FunctionNode){
			Integer maxNbParamsPerFunction = getPreferenceAsInteger(NBPARAMS_PROBLEM_ID, PREF_NBPARAMS_MAXIMUM_PER_FUNCTION, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxNbParamsPerFunction){
				reportProblem(NBPARAMS_PROBLEM_ID, node, maxNbParamsPerFunction);
			}				
		}
	}
}
