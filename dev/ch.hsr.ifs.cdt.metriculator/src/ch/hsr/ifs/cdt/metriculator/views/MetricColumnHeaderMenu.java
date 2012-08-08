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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Ueli Kunz
 * */
public final class MetricColumnHeaderMenu {

	private static final String DATAKEY_CURR_COLUMN    = "current_column";

	/**
	 * @column is either of type TableColumn or TreeColumn
	 * */
	public static void setCurrColumn(Menu menu, Item column) {
		menu.setData(DATAKEY_CURR_COLUMN, column);
	}	
	
	public static Item getCurrColumn(Menu menu) {
		return (Item) menu.getData(DATAKEY_CURR_COLUMN);
	}
	
	public static MenuManager tableMenuManager = new MenuManager();
	public static MenuManager treeMenuManager  = new MenuManager();
	
	public static Menu create(Shell shell, final Tree treeObj){
		treeMenuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));	
		treeMenuManager.add(new Separator());
		final Menu headerMenu = treeMenuManager.createContextMenu(treeObj);
		treeObj.setMenu(headerMenu);
		
		treeObj.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point treeRelativePoint = Display.getDefault().map(null, treeObj, new Point(event.x, event.y));
				Rectangle clientArea    = treeObj.getClientArea();
				boolean inHeaderArea    = clientArea.y <= treeRelativePoint.y && treeRelativePoint.y < (clientArea.y + treeObj.getHeaderHeight());
				int selectedColIndex    = getColumnIndexAt(treeRelativePoint.x);
				boolean inMetricColumn  = inHeaderArea && selectedColIndex > 0 && !MetricColumn.isFiller(treeObj.getColumn(selectedColIndex));

				if(inMetricColumn){
					setCurrColumn(headerMenu, treeObj.getColumn(selectedColIndex));
				}else{
					event.doit = false;
				}
			}

			private int getColumnIndexAt(int offsetLeft) {
				int colWidthsTotal = 0;
				int colIndex = -1;
				for(TreeColumn col : treeObj.getColumns()){
					colWidthsTotal += col.getWidth();
					++colIndex;
					if(colWidthsTotal > offsetLeft){
						return colIndex;
					}
				}
				return -1;
			}
		});
		
		treeObj.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				headerMenu.dispose();
			}
		});	
		
		return headerMenu;
	}
	
	public static Menu create(Shell shell, final Table tableObj){
		tableMenuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
		final Menu headerMenu = tableMenuManager.createContextMenu(tableObj);
		tableObj.setMenu(headerMenu);
		
		tableObj.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point treeRelativePoint = Display.getDefault().map(null, tableObj, new Point(event.x, event.y));
				Rectangle clientArea    = tableObj.getClientArea();
				boolean inHeaderArea    = clientArea.y <= treeRelativePoint.y && treeRelativePoint.y < (clientArea.y + tableObj.getHeaderHeight());
				int selectedColIndex    = getColumnIndexAt(treeRelativePoint.x);
				boolean inMetricColumn  = inHeaderArea && selectedColIndex > 0 && !MetricColumn.isFiller(tableObj.getColumn(selectedColIndex));
				
				if(inMetricColumn){
					setCurrColumn(headerMenu, tableObj.getColumn(selectedColIndex));
				}else{
					event.doit = false;
				}
			}

			private int getColumnIndexAt(int offsetLeft) {
				int colWidthsTotal = 0;
				int colIndex = -1;
				for(TableColumn col : tableObj.getColumns()){
					colWidthsTotal += col.getWidth();
					++colIndex;
					if(colWidthsTotal > offsetLeft){
						return colIndex;
					}
				}
				return -1;
			}
		});
		
		tableObj.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				headerMenu.dispose();
			}
		});
		
		return headerMenu;
	}

	public static void updateItemSelections(MenuManager menu) {
		for(IContributionItem item : menu.getItems()){

			if(item instanceof ToggleColumnActionItem){
				((ToggleColumnActionItem<?>) item).toggleVisibility();
			}
		}
		menu.isDirty();
	}
}
