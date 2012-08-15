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

package ch.hsr.ifs.cdt.metriculator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.hsr.ifs.cdt.metriculator.model.test.NodeCompositeTest;
import ch.hsr.ifs.cdt.metriculator.model.test.NodeFilterTest;
import ch.hsr.ifs.cdt.metriculator.model.test.TreeBuilderIndexerTest;
import ch.hsr.ifs.cdt.metriculator.model.test.TreeBuilderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	NodeCompositeTest.class, 
	TreeBuilderTest.class,
	TreeBuilderIndexerTest.class,
	NodeFilterTest.class,
	LSLOCMetricCheckerTest.class, 
	McCabeMetricCheckerTest.class, 
	NumberParamsCheckerTest.class,
	NumberMembersCheckerTest.class,
	EfferentCouplingMetricCheckerTest.class
	 
})

public class AllTests {
}
