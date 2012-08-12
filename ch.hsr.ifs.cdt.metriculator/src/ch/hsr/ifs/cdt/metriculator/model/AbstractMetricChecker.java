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
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.ICheckersRegistry;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.codan.core.param.BasicProblemPreference;
import org.eclipse.cdt.codan.core.param.IProblemPreferenceDescriptor.PreferenceType;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.OperationCanceledException;

import ch.hsr.ifs.cdt.metriculator.JobObservable;
import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.resources.MetricLabels;

public abstract class AbstractMetricChecker extends AbstractIndexAstChecker implements Observer, IScopeListener {

	private static final String PREF_REPORT_CHECKER_PROBLEMS = "report_problems";
	private final String PROBLEM_ID;
	private HashMap<String, Collection<IProblem>> reportedProblems = new HashMap<String, Collection<IProblem>>();
	protected AbstractNode currentScopeNode;
	protected HybridTreeBuilder builder;

	protected AbstractMetricChecker(String problemId){
		PROBLEM_ID = problemId;
	}
	
	@Override
	public synchronized boolean processResource(IResource resource)	throws OperationCanceledException {
		builder          = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder();
		currentScopeNode = builder.addChild(builder.root, new ProjectNode(resource.getProject()));

		return super.processResource(resource);
	}

	@Override
	public boolean runInEditor() {
		return false; // do not run this checker 'as you type'. only run on user command.
	}
	
	public void processAst(IASTTranslationUnit ast) {
		
		// ignore c files, #201
		if(ast.getFilePath().lastIndexOf(".c") == ast.getFilePath().length() - 2){
			return;
		}
		
		AbstractNode fileSystemLeaf = TreeBuilder.createTreeFromPath((ProjectNode) currentScopeNode, ast);
		AbstractNode fileSystemTop  = fileSystemLeaf.getRoot(); 

		currentScopeNode = builder.addChild(currentScopeNode, fileSystemTop);
		currentScopeNode = builder.getChildBy(fileSystemLeaf.getHybridId());

		processTanslationUnit(ast);
		
		builder.mergeDeclarationsAndDefinitions(ast);

		currentScopeNode = fileSystemTop.getParent();
	}

	protected abstract void processTanslationUnit(IASTTranslationUnit tu);

	public static <T extends AbstractMetricChecker> T getChecker(Class<T> clazz) {
		for (IChecker checker : CodanRuntime.getInstance().getCheckersRegistry()) {
			if (checker.getClass().equals(clazz)) {
				return clazz.cast(checker);
			}
		}
		return null;
	}

	public static Collection<AbstractMetricChecker> getMetricCheckers(){
		Collection<AbstractMetricChecker> checkers = new ArrayList<AbstractMetricChecker>();
		for(IChecker chk : CodanRuntime.getInstance().getCheckersRegistry()){
			if(chk instanceof AbstractMetricChecker){
				checkers.add((AbstractMetricChecker) chk);	
			}
		}
		return Collections.unmodifiableCollection(checkers);
	}

	public Collection<IProblem> getProblemsFor(AbstractNode node){
		return reportedProblems.get(node.getHybridId());
	}

	public boolean hasEnabledProblems() {
		ICheckersRegistry checkersRegistry = CodanRuntime.getInstance().getCheckersRegistry();
		IProblem[] profileProblems         = checkersRegistry.getWorkspaceProfile().getProblems();

		for(IProblem p : checkersRegistry.getRefProblems(this)){
			// find matching profile problem, 
			// not a nice peace of code!
			for(IProblem profileProblem : profileProblems){
				if(profileProblem.getId().equalsIgnoreCase(p.getId())){
					if(profileProblem.isEnabled()){
						return true;
					}
				}
			}
		}
		return false;
	}

	public void reportProblem(String problemId, AbstractNode abstractNode, Object... messageParameters) {

		if(reportedProblems.get(abstractNode.getHybridId()) == null){
			reportedProblems.put(abstractNode.getHybridId(), new ArrayList<IProblem>());
		}
		reportedProblems.get(abstractNode.getHybridId()).add(getProblemById(problemId, getFile()));
		reportedProblems.put(abstractNode.getHybridId(), reportedProblems.get(abstractNode.getHybridId()));
		
		if(getShouldReportProblems()){
			IProblemLocation loc = abstractNode.getEditorInfo().createAndGetProblemLocation(getFile());
	
			if (loc != null){
				super.reportProblem(problemId, loc, messageParameters);
			}
		}
	}	

	public Integer getPreferenceAsInteger(String problemId, String prefKey, IFile file) {
		Object prefValue = getPreference(getProblemById(problemId, file), prefKey);

		return Integer.valueOf(prefValue.toString());
	}	
	
	public boolean getShouldReportProblems() {
		Object prefValue = getPreference(getProblemById(PROBLEM_ID, getFile()), PREF_REPORT_CHECKER_PROBLEMS);

		return Boolean.valueOf(prefValue.toString());
	}	

	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		super.initPreferences(problem);
		
		addPreference(
				problem,
				new BasicProblemPreference(
						PREF_REPORT_CHECKER_PROBLEMS, 
						MetricLabels.REPORT_CHECKER_PROBLEMS, 
						PreferenceType.TYPE_BOOLEAN),
				false);
		
		MetriculatorPluginActivator.getDefault().getObservable().addObserver(this);
	}	
	
	@Override
	public void update(Observable observable, Object state) {
		if(state instanceof JobObservable.JobState){
			if(((JobObservable.JobState)state) == JobObservable.JobState.JOB_ABOUT_TO_RUN){
				reportedProblems.clear();
				currentScopeNode = null;
				builder = null;
			}
		}
	}
	
	@Override
	public void leaving(AbstractNode node) {
		reportProblemsFor(node);	
	}
	
	@Override
	public void visiting(AbstractNode scopeNode) {
	}
	
	protected abstract void reportProblemsFor(AbstractNode node);
}