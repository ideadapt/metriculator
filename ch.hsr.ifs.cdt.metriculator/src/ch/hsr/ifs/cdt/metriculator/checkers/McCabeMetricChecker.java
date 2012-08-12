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

public class McCabeMetricChecker extends AbstractMetricChecker{
	
	public static final String PREF_MCCABE_MAXIMUM_PER_FUNCTION = "max_per_function"; //$NON-NLS-1$
	public static final String MCCABE_PROBLEM_ID                = "ch.hsr.ifs.cdt.metriculator.mccabe";
	private McCabeMetric metric;

	public McCabeMetricChecker(){
		super(MCCABE_PROBLEM_ID);
		metric = new McCabeMetric(this, MetricLabels.McCabeMetric_name, MetricLabels.McCabeMetric_description);
		MetriculatorPluginActivator.getDefault().registerMetric(metric);
	}
	
	@Override
	protected void processTanslationUnit(IASTTranslationUnit tu) {
		McCabeScopedASTVisitor visitor = new McCabeScopedASTVisitor(currentScopeNode, builder);
		visitor.add(this);
		tu.accept(visitor);
	}
	
	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);

		addPreference(problem, PREF_MCCABE_MAXIMUM_PER_FUNCTION, MetricLabels.MCCABE_Maximum_Per_Function, "15"); //$NON-NLS-1$
	}

	protected void reportProblemsFor(AbstractNode node){
		if(node instanceof FunctionNode){
			Integer maxMcCabePerFunction = getPreferenceAsInteger(MCCABE_PROBLEM_ID, PREF_MCCABE_MAXIMUM_PER_FUNCTION, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxMcCabePerFunction){
				reportProblem(MCCABE_PROBLEM_ID, node, maxMcCabePerFunction);
			}				
		}
	}
}
