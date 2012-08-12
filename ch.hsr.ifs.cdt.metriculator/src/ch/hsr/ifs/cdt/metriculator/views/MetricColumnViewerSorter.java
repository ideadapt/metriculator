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

package ch.hsr.ifs.cdt.metriculator.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Ueli Kunz
 * */
public final class MetricColumnViewerSorter {

	public static void registerFor(TreeColumn col, TreeViewer treeViewer) {
		new TreeColumnViewerSorter(treeViewer, col);
	}

	public static void registerFor(TableColumn col, TableViewer tableViewer) {
		new TableColumnViewerSorter(tableViewer, col);
	}
}
