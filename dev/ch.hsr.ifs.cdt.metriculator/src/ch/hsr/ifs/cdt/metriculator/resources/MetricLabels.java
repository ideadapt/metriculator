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

package ch.hsr.ifs.cdt.metriculator.resources;

import org.eclipse.osgi.util.NLS;

/**
 * Messages
 */
public final class MetricLabels extends NLS {

	/*
	 * 1. copy&paste content from MetricLabels.properties
	 * 2. search for ^(.*)=.*$ and replace all by public static String $1;
	 * */
	public static String 	LSLOCMetric_name;
	public static String 	LSLOCMetric_description;
	public static String 	LSLOC_Maximum_Per_File;
	public static String 	LSLOC_Maximum_Per_Function;
	public static String 	NumberMembersMetric_name;
	public static String 	NumberMembersMetric_description;
	public static String 	NBMEMBERS_Maximum_Per_Type;
	public static String 	McCabeMetric_name;
	public static String 	McCabeMetric_description;
	public static String 	MCCABE_Maximum_Per_Function;
	public static String 	NumberParamsMetric_name;
	public static String 	NumberParamsMetric_description;
	public static String	NBPARAMS_Maximum_Per_Function;
	public static String 	EfferentCouplingMetric_name;
	public static String 	EfferentCouplingMetric_description;
	public static String 	EFFERENTCOUPLING_Maximum_Per_Type;
	public static String    REPORT_CHECKER_PROBLEMS;

	static {
		NLS.initializeMessages(MetricLabels.class.getName(), MetricLabels.class);
	}

	// Do not instantiate
	private MetricLabels() {
	}
}
