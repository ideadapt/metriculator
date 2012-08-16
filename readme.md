[![Build Status](https://secure.travis-ci.org/ideadapt/metriculator.png?branch=master)](http://travis-ci.org/ideadapt/metriculator)

Installation
============
* Download Eclipse CDT (or install C++ Development Tools as plug-in) from eclipse.org
* Install the following plug-ins via update site in eclipse:
  
  _zest_: https://hudson.eclipse.org/hudson/job/gef-zest-integration/ws/org.eclipse.zest.repository/target/repository/
   Zest is not supported in Eclipse Juno. Zest is only required if metriculator will be installed with the tag cloud feature.

  _metriculator_: http://metriculator.ideadapt.net


metriculator is also available from Eclipse marketplace: http://marketplace.eclipse.org/content/metriculator
To install from marketplace simply drag and drop the following _install_ button into your running Eclipse window.

<a href='http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=269008' title='Drag and drop into a running Eclipse Indigo workspace to install metriculator'> 
	<img src='http://marketplace.eclipse.org/misc/installbutton.png'/>
</a>

Contributer Notes
=================

Metriculator allows you to add new C++ metrics easily. There are already some metrics implemented which might help you to implement additional metrics. The following steps describe how you can add new metrics fast an simple. This manual assumes that you are working with Eclipse and have installed the Plug-in Development Environment (PDE) as well as the C/C++ Development Tooling (CDT) plug-in.

Set up metriculator Eclipse Workspace
-------------------------------------
1. Checkout sources from the Git at GitHub repository
2. In Eclipse Import Existing Projects into Workspace, select the repository checked
out from point 1.
3. Set the missing baselines to ignore in Eclipse > Window > Preferences > Plug-in
Development > API Baselines.
4. Open the target definition file in the package metriculator and set it as target platform.
5. Update all the locations.

Adding a New Metric
-------------------
1. Add a new checker with a problem in the plugin.xml.
2. Create a new metric class which inherits from _AbstractMetric_.
 - If your new metric requires a non default metric value aggregation, override the _aggregate_ method.
3. Create a new metric checker class that inherits from _AbstractMetricChecker_.
 - Define a problem ID.
 - Create the name, description and preferences strings.
 - Add the name, description and preferences strings to the MetricLabels.properties file in the package resources.
 - Add the name, description and preferences strings to _MetricLabels_.
 - Create a new instance of the metric in this checker.
 - Register the new metric at the MetriculatorPluginActivator singleton instance.
 - Implement the reportProblemsFor method.
 - Implement the processTranslationUnit method. See chapter 3 for further information about visitors.
 
Pull Requests
-------------------------------------
If you plan to do "pull requests" you can watch if your potential contribution would build successfully at <a href='http://travis-ci.org/#!/ideadapt/metriculator/pull_requests' title='Travis Build'>Travis-CI</a>
