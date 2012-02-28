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

package ch.hsr.ifs.cdt.metriculator.test;

import org.eclipse.cdt.codan.core.test.CheckerTestCase;
import org.eclipse.core.runtime.Plugin;

public class MetriculatorCheckerTestCase extends CheckerTestCase {

	@Override
	public boolean isCpp() {
		return true;
	}

	@Override
	protected Plugin getPlugin() {
		return MetriculatorTestActivator.getDefault();
	}
}
