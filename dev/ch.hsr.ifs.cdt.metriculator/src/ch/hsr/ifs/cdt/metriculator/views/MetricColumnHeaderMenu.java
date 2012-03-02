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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Ueli Kunz
 * */
public final class MetricColumnHeaderMenu {

	private static final String DATAKEY_CURR_COLUMN    = "current_column";
	public static final String DATAKEY_MENUITEM_COLUMN = "menuitem_column";

	public static void setCurrColumn(Menu menu, TableColumn column) {
		menu.setData(DATAKEY_CURR_COLUMN, column);
	}	
	
	public static void setCurrColumn(Menu menu, TreeColumn column) {
		menu.setData(DATAKEY_CURR_COLUMN, column);
	}	
	
	public static Widget getCurrColumn(Menu menu) {
		return (Widget) menu.getData(DATAKEY_CURR_COLUMN);
	}

	public enum ItemType{
		GenerateTagCloud, ToggleMetricColumn
	}
	
	public static MenuManager menuManager = new MenuManager();

	public static Menu create(Shell shell, final Tree treeObj){
		menuManager.isDynamic();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
		final Menu headerMenu = menuManager.createContextMenu(treeObj);
		treeObj.setMenu(headerMenu);
		
		treeObj.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point treeRelativePoint = Display.getDefault().map(null, treeObj, new Point(event.x, event.y));
				Rectangle clientArea    = treeObj.getClientArea();
				boolean inHeaderArea    = clientArea.y <= treeRelativePoint.y && treeRelativePoint.y < (clientArea.y + treeObj.getHeaderHeight());
				boolean inMetricColumn  = inHeaderArea && getColumnAt(treeRelativePoint.x) > 0;
				
				if(inMetricColumn){
					setCurrColumn(headerMenu, treeObj.getColumn(getColumnAt(treeRelativePoint.x)));
				}else{
					event.doit = false;
				}
			}

			private int getColumnAt(int offsetLeft) {
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
		final Menu headerMenu = new Menu(shell, SWT.POP_UP);
		
		tableObj.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Point treeRelativePoint = Display.getDefault().map(null, tableObj, new Point(event.x, event.y));
				Rectangle clientArea    = tableObj.getClientArea();
				boolean inHeaderArea    = clientArea.y <= treeRelativePoint.y && treeRelativePoint.y < (clientArea.y + tableObj.getHeaderHeight());
				boolean inMetricColumn  = inHeaderArea && getColumnAt(treeRelativePoint.x) > 0;
				
				if(inMetricColumn){
					setCurrColumn(headerMenu, tableObj.getColumn(getColumnAt(treeRelativePoint.x)));
					tableObj.setMenu(headerMenu);
				}else{
					event.doit = false;
				}
			}

			private int getColumnAt(int offsetLeft) {
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

	public static void createTagCloudMenuItem(Menu menu, Listener listener) {
		
		MenuItem tagCloudItem = new MenuItem(menu, SWT.PUSH);
		tagCloudItem.setText("Generate TagCloud");
		tagCloudItem.setData(ItemType.GenerateTagCloud);
		tagCloudItem.addListener(SWT.Selection, listener);
		
		new MenuItem(menu, SWT.SEPARATOR);
	}
	
	/**
	 * @deprecated use updateItemSelections(MenuManager menu) instead
	 * */
	public static void updateItemSelections(Menu menu) {
		for(MenuItem item : menu.getItems()){
			
			if(item.getData() != null && item.getData().equals(ItemType.ToggleMetricColumn)){
				Object col = item.getData(DATAKEY_MENUITEM_COLUMN);
				if(col instanceof TreeColumn){
					item.setSelection(MetricColumn.isVisible((TreeColumn)col));
				}
				if(col instanceof TableColumn){
					item.setSelection(MetricColumn.isVisible((TableColumn)col));
				}
			}
		}
	}

	public static void updateItemSelections(MenuManager menu) {
		for(IContributionItem item : menu.getItems()){
			
			if(item instanceof ToggleColumnActionContrItem){
				((ToggleColumnActionContrItem<?>) item).toggleVisibility();
			}
		}
		menuManager.isDirty();
	}
}
