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

package ch.hsr.ifs.cdt.metriculator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;

import org.eclipse.cdt.codan.internal.ui.CodanUIMessages;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.FlatTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.HybridTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.LogicTreeBuilder;
import ch.hsr.ifs.cdt.metriculator.views.MetriculatorView;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class MetriculatorPluginActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.hsr.ifs.cdt.metriculator"; //$NON-NLS-1$
	
	// The shared instance
	private static MetriculatorPluginActivator plugin;

	private JobObservable observable = new JobObservable();
	HashMap<Class<AbstractMetric>, AbstractMetric> metrics = new HashMap<Class<AbstractMetric>, AbstractMetric>();

	private HybridTreeBuilder hybridTreeBuilder;
	private FlatTreeBuilder flatTreeBuilder;
	private LogicTreeBuilder logicTreeBuilder;

	public Observable getObservable() {
		return observable;
	}

	/**
	 * The constructor
	 */
	public MetriculatorPluginActivator() {
		this.resetTreeBuilders();
		this.addCommandExecutionListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static MetriculatorPluginActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void resetTreeBuilders() {
		hybridTreeBuilder = new HybridTreeBuilder("Workspace");
		flatTreeBuilder = null;
		logicTreeBuilder = null;
	}
	
	public HybridTreeBuilder getHybridTreeBuilder(){
		return hybridTreeBuilder;
	}
	
	public FlatTreeBuilder getFlatTreeBuilder(){
		
		if(flatTreeBuilder == null){
			flatTreeBuilder = FlatTreeBuilder.buildFrom(hybridTreeBuilder);
		}
		
		return flatTreeBuilder;
	}
	
	public LogicTreeBuilder getLogicTreeBuilder(){
		if(logicTreeBuilder == null){
			logicTreeBuilder = LogicTreeBuilder.buildFrom(hybridTreeBuilder);
		}
		
		return logicTreeBuilder;
	}
	
	private void showMetriculatorView() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try {

					page.showView(MetriculatorView.VIEW_ID);
					page.addPartListener(new IPartListener2() {

						@Override
						public void partClosed(IWorkbenchPartReference partRef) {
							if(partRef.getId().equals(MetriculatorView.VIEW_ID)){
								resetTreeBuilders();
								resetMetricCaches();
								System.gc();
							}
						}

						@Override
						public void partVisible(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partOpened(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partInputChanged(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partHidden(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partDeactivated(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partBroughtToTop(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partActivated(IWorkbenchPartReference partRef) {
						}
					});
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addCommandExecutionListener(){
		
		Job.getJobManager().addJobChangeListener(new IJobChangeListener() {
			
			@Override
			public void sleeping(IJobChangeEvent event) {
			}
			
			@Override
			public void scheduled(IJobChangeEvent event) {
			}
			
			@Override
			public void running(IJobChangeEvent event) {
			}
			
			@Override
			public void done(IJobChangeEvent event) {
				if(event.getJob().getName().equals(CodanUIMessages.Job_TitleRunningAnalysis)){
					
					aggregateMetricValues();
					
					if(isAnyMetricProblemEnabled()){
						showMetriculatorView();
					}
					
					observable.setChangedAndNotifyJobDone();
				}
			}
			
			@Override
			public void awake(IJobChangeEvent event) {
			}			

			public void aboutToRun(IJobChangeEvent event) {
				if(event.getJob().getName().equals(CodanUIMessages.Job_TitleRunningAnalysis)){
					resetTreeBuilders();
					resetMetricCaches();
					System.gc();
					observable.setChangedAndNotifyJobAboutToRun();
				}
			}
		});
	}
	
	private void aggregateMetricValues() {
		for(AbstractMetric m : getMetrics()){
			hybridTreeBuilder.root.getValueOf(m);
			m.useCachedValue = true;
		}
	}
	
	private void resetMetricCaches() {
		for(AbstractMetric m : getMetrics()){
			m.useCachedValue = false;
		}
	}
	
	private boolean isAnyMetricProblemEnabled() {
		for(AbstractMetric m : getMetrics()){
			if(m.getChecker().hasEnabledProblems()){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void registerMetric(AbstractMetric metric) {

		if(!metrics.containsKey(metric.getClass())){
			metrics.put((Class<AbstractMetric>) metric.getClass(), metric);
		}
	}

	public Collection<AbstractMetric> getMetrics() {
		return Collections.unmodifiableCollection(metrics.values());
	}
}
