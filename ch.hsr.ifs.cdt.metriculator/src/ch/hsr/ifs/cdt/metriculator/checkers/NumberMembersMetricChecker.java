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
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;
import ch.hsr.ifs.cdt.metriculator.resources.MetricLabels;

public class NumberMembersMetricChecker extends AbstractMetricChecker{

	public static final String PREF_NBMEMBERS_MAXIMUM_PER_TYPE = "max_per_function"; //$NON-NLS-1$
	public static final String NBMEMBERS_PROBLEM_ID         = "ch.hsr.ifs.cdt.metriculator.classmembers";
	private NumberMembersMetric metric;

	public NumberMembersMetricChecker(){
		super(NBMEMBERS_PROBLEM_ID);
		metric = new NumberMembersMetric(this, MetricLabels.NumberMembersMetric_name, MetricLabels.NumberMembersMetric_description);
		MetriculatorPluginActivator.getDefault().registerMetric(metric);
	}
	
	
	@Override
	protected void processTanslationUnit(IASTTranslationUnit tu) {
		NumberMembersScopedASTVisitor visitor = new NumberMembersScopedASTVisitor(currentScopeNode, builder);
		visitor.add(this);
		tu.accept(visitor);
	}

	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);
		
		addPreference(problem, PREF_NBMEMBERS_MAXIMUM_PER_TYPE, MetricLabels.NBMEMBERS_Maximum_Per_Type, "20"); //$NON-NLS-1$
	}
	
	protected void reportProblemsFor(AbstractNode node){
		if(node instanceof TypeDefNode){
			Integer maxNbMembersPerType = getPreferenceAsInteger(NBMEMBERS_PROBLEM_ID, PREF_NBMEMBERS_MAXIMUM_PER_TYPE, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxNbMembersPerType){
				reportProblem(NBMEMBERS_PROBLEM_ID, node, maxNbMembersPerType);
			}					
		}
	}
}
