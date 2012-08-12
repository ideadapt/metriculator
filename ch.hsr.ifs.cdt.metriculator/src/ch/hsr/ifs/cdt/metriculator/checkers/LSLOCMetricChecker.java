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
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetricChecker;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.resources.MetricLabels;

public class LSLOCMetricChecker extends AbstractMetricChecker{
	
	public static final String PREF_LSLOC_MAXIMUM_PER_FILE     = "max_per_file"; //$NON-NLS-1$
	public static final String PREF_LSLOC_MAXIMUM_PER_FUNCTION = "max_per_function"; //$NON-NLS-1$
	public static final String LSLOC_PROBLEM_ID                = "ch.hsr.ifs.cdt.metriculator.lsloc"; //$NON-NLS-1$
	private final String key                                   = AbstractMetric.getKeyFor(LSLOCMetric.class);
	private LSLOCMetric metric;
	
	public LSLOCMetricChecker(){
		super(LSLOC_PROBLEM_ID);
		metric = new LSLOCMetric(this, MetricLabels.LSLOCMetric_name, MetricLabels.LSLOCMetric_description);
		MetriculatorPluginActivator.getDefault().registerMetric(metric);
	}
	
	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);

		addPreference(problem, PREF_LSLOC_MAXIMUM_PER_FILE, MetricLabels.LSLOC_Maximum_Per_File, "100"); //$NON-NLS-1$
		addPreference(problem, PREF_LSLOC_MAXIMUM_PER_FUNCTION, MetricLabels.LSLOC_Maximum_Per_Function, "20"); //$NON-NLS-1$
		
		// TODO: when CDT bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=363207 is fixed, we can use integer field types		
		//		addPreference(
		//				problem,
		//				new BasicProblemPreference(PREF_LSLOC_MAXIMUM_PER_FILE, CheckerPreferenceLabels.LSLOC_Maximum_Per_File, PreferenceType.TYPE_INTEGER),
		//				15);
	}

	@Override
	protected void processTanslationUnit(IASTTranslationUnit tu) {

		LSLOCScopedASTVisitor visitor = new LSLOCScopedASTVisitor(currentScopeNode, builder);
		visitor.add(this);
		tu.accept(visitor);
		
		if(tu.getAllPreprocessorStatements().length > 0){
			currentScopeNode.setNodeValue(key, currentScopeNode.getNodeValue(key) + tu.getAllPreprocessorStatements().length);
		}
		
		reportProblemsFor((FileNode)currentScopeNode);
	}

	protected void reportProblemsFor(AbstractNode node){
		if(node instanceof FunctionNode){
			Integer maxLSLOCPerFunction = getPreferenceAsInteger(LSLOC_PROBLEM_ID, PREF_LSLOC_MAXIMUM_PER_FUNCTION, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxLSLOCPerFunction){
				reportProblem(LSLOC_PROBLEM_ID, node, maxLSLOCPerFunction);
			}				
		}
		else		
		if(node instanceof FileNode){
			Integer maxLSLOCPerFile = getPreferenceAsInteger(LSLOC_PROBLEM_ID, PREF_LSLOC_MAXIMUM_PER_FILE, getFile());
			if(node.getValueOf(metric).aggregatedValue > maxLSLOCPerFile){
				reportProblem(LSLOC_PROBLEM_ID, node, maxLSLOCPerFile);
			}	
		}
	}
}