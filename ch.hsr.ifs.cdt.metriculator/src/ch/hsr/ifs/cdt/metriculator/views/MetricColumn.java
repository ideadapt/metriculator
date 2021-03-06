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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;

/**
 * @author Ueli Kunz
 * */
public final class MetricColumn {

	static final int DEFAULT_WIDTH            = 100;
	static final String DATAKEY_IS_FILLER 	  = "isFiller";
	static final String DATAKEY_COLUMNMETRIC  = "metric";
	
	public static void showColumn(TreeColumn column) {
		column.setWidth(DEFAULT_WIDTH);
		column.setResizable(true);
	}
	
	public static void hideColumn(TreeColumn column) {
		column.setWidth(0);
		column.setResizable(false);
	}
	
	public static void showColumn(TableColumn column) {
		column.setWidth(DEFAULT_WIDTH);
		column.setResizable(true);
	}
	
	public static void hideColumn(TableColumn column) {
		column.setWidth(0);
		column.setResizable(false);
	}
	
	public static TreeColumn createFor(AbstractMetric metric, TreeViewer treeViewer) {

		TreeColumn column = new TreeColumn(treeViewer.getTree(), SWT.RIGHT);
		column.setText(metric.getName());
		column.setToolTipText(metric.getDescription());
		
		setMetric(metric, column);				
		MetricColumnViewerSorter.registerFor(column, treeViewer);
		
		return column;
	}
	
	public static TableColumn createFor(AbstractMetric metric, TableViewer tableViewer) {

		TableColumn column = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
		column.setText(metric.getName());
		column.setToolTipText(metric.getDescription());
		
		setMetric(metric, column);				
		MetricColumnViewerSorter.registerFor(column, tableViewer);
		
		return column;
	}

	public static AbstractMetric getMetric(Widget column) {
		return (AbstractMetric) column.getData(DATAKEY_COLUMNMETRIC);
	}
	
	public static void setMetric(AbstractMetric metric, Widget column) {
		column.setData(DATAKEY_COLUMNMETRIC, metric);
	}

	public static void createFillerColumnOnce(Tree tree) {
		
		if(isWindows()){
			return;
		}
		
		if(hasFillerColumn(tree.getColumns())){
			return;
		}
		
		TreeColumn column = new TreeColumn(tree, SWT.RIGHT);
		column.setMoveable(false);
		column.setResizable(true);
		column.setWidth(10);
		column.setData(DATAKEY_IS_FILLER, true);
	}
	
	private static boolean isWindows() {
		return (System.getProperty("os.name", "unknown").toLowerCase().indexOf("windows") >= 0);
	}
	
	public static void createFillerColumnOnce(Table table) {

		if(isWindows()){
			return;
		}
		
		if(hasFillerColumn(table.getColumns())){
			return;
		}
		
		TableColumn column = new TableColumn(table, SWT.RIGHT);
		column.setMoveable(false);
		column.setResizable(true);
		column.setWidth(10);
		column.setData(DATAKEY_IS_FILLER, true);
	}	

	private static boolean hasFillerColumn(Item... items) {
		for(Item column : items){
			if(isFiller(column)){
				return true;
			}
		}
		return false;
	}

	public static boolean isFiller(Item column) {
		if(column.getData(DATAKEY_IS_FILLER) != null && ((Boolean) column.getData(DATAKEY_IS_FILLER)) == true){
			return true;
		}
		return false;
	}	
}
