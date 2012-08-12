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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TreeColumn;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

/**
 * @see http://bingjava.appspot.com/snippet.jsp?id=2207
 * @author Ueli Kunz
 * */
class TreeColumnViewerSorter extends ViewerComparator {
	public static final int ASC  = 1;
	public static final int NONE = 0;
	public static final int DESC = -1;

	private int direction = 0;
	protected TreeColumn column;
	protected StructuredViewer viewer;

	public TreeColumnViewerSorter(TreeViewer viewer, TreeColumn column) {
		this.column = column;
		this.viewer = viewer;

		this.column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				ViewerComparator comparator = TreeColumnViewerSorter.this.viewer.getComparator();
				if(comparator != null && comparator == TreeColumnViewerSorter.this) {
					int tdirection = direction;

					if(tdirection == ASC) {
						setSorter(TreeColumnViewerSorter.this, DESC);
					} else if(tdirection == DESC) {
						setSorter(TreeColumnViewerSorter.this, NONE);
					}
				}else {
					setSorter(TreeColumnViewerSorter.this, ASC);
				}
			}
		});
	}

	public void setSorter(TreeColumnViewerSorter sorter, int direction) {
		if(direction == NONE) {
			setNoneSorter();
		} else {
			setSorterFor(sorter, direction);
		}
	}

	private void setNoneSorter() {
		column.getParent().setSortColumn(null);
		column.getParent().setSortDirection(SWT.NONE);
		viewer.setComparator(null);
	}
	
	private void setSorterFor(TreeColumnViewerSorter sorter, int direction) {
		column.getParent().setSortColumn(column);
		sorter.direction = direction;
		
		if(direction == ASC) {
			column.getParent().setSortDirection(SWT.DOWN);
		} else {
			column.getParent().setSortDirection(SWT.UP);
		}
		
		if(viewer.getComparator() == sorter) {
			viewer.refresh();
		} else {
			viewer.setComparator(sorter);
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return direction * doCompare(viewer, e1, e2);
	}

	protected int doCompare(Viewer viewer, Object e1, Object e2){
		AbstractNode n1 = ((AbstractNode)e1);
		AbstractNode n2 = ((AbstractNode)e2);
		AbstractMetric metric = MetricColumn.getMetric(column);
		int val1 = n1.getValueOf(metric).aggregatedValue;
		int val2 = n2.getValueOf(metric).aggregatedValue;
		
		return (val1 < val2) ? 1 : val1 == val2 ? 0 : -1;		
	}
}