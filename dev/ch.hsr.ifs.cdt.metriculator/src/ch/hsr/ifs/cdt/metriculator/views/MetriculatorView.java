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

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.cdt.codan.core.model.CodanSeverity;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.ui.CodanEditorUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import ch.hsr.ifs.cdt.metriculator.JobObservable.JobState;
import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.NodeFilter;
import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.TypeDefNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FileNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FunctionNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.NamespaceNode;
import ch.hsr.ifs.cdt.metriculator.resources.Icon;
import ch.hsr.ifs.cdt.metriculator.views.ToggleColumnActionItem.ToggleTableColumnActionContrItem;
import ch.hsr.ifs.cdt.metriculator.views.ToggleColumnActionItem.ToggleTreeColumnActionContrItem;

/**
 * @author Jules Weder, Ueli Kunz
 * */
public class MetriculatorView extends ViewPart implements Observer, ITagCloudDataProvider {

	private static final int INITIAL_SORT_ORDER           = TreeColumnViewerSorter.NONE;
	private static final int SCOPE_COLUMN_DEFAULT_WIDTH   = 160;
	private static final String SCOPE_COLUMN_TITLE        = "Scope";
	public static final String VIEW_ID                    = "ch.hsr.ifs.cdt.metriculator.views.MetriculatorView";
	public static final String TABLE_COLUMN_HEADER_MENU_ID = MetriculatorView.VIEW_ID+".menuTableColumnHeader";
	
	private HashMap<AbstractMetric, ToggleColumnActionItem<TreeColumn>> metricsTreeColumnActions     = new HashMap<AbstractMetric, ToggleColumnActionItem<TreeColumn>>();
	private HashMap<AbstractMetric, ToggleColumnActionItem<TableColumn>> metricsTableColumnActions   = new HashMap<AbstractMetric, ToggleColumnActionItem<TableColumn>>();
	private TreeViewer treeViewer;
	private TableViewer tableViewer;
	private NodeViewerFilter viewerFilter;
	private IAction actionHybridView;
	private IAction actionExpandAll;
	private IAction actionCollapseAll;
	private IAction actionFilterComposite;
	private IAction actionLogicalView;
	private IAction actionFilterFunction;
	private IAction actionFilterNamespace;
	private IAction actionFilterFile;
	private TreeBuilder currTreeBuilder;
	private ViewMode viewMode;
	private Menu treeHeaderMenu;
	private Menu tableHeaderMenu;
	private Composite treeComposite;
	private Composite tableComposite;
	private Composite parentComposite;
	private StructuredViewer activeViewer;
	private StackLayout stackLayout;
	
	public enum ViewMode {
		Hybrid,
		Logical, 
		Filtered
	}
	
	public MetriculatorView() {
		MetriculatorPluginActivator.getDefault().getObservable().addObserver(this);
	}
	
	@Override
	public void dispose() {
		MetriculatorPluginActivator.getDefault().getObservable().deleteObserver(this);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);
		parentComposite = parent;
		
		createTreeComponents();
		
		createTableComponents();
		
		createActions();
		addActionsToBars();
		
		applyViewMode(ViewMode.Hybrid, null);
		
		updateViewerData();
	}

	private void createTableComponents() {
		tableComposite = new Composite(parentComposite, parentComposite.getStyle());
		tableComposite.setLayout(new FillLayout());

		Table tableObject = new Table(tableComposite,  SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableObject.setHeaderVisible(true);
		tableObject.setLinesVisible(true);
		
		tableViewer = new TableViewer(tableObject);
		tableViewer.addFilter(viewerFilter);
		tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new MetriculatorCellLabelProvider());
		
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
		addViewerOpenListener(tableViewer);
		createTableHeaderMenu();
		createTableLabelColumn();
		createAndUpdateMetricTableColumns();
	}

	private void createTreeComponents() {
		treeComposite = new Composite(parentComposite, parentComposite.getStyle());
		treeComposite.setLayout(new FillLayout());
		
		Tree treeObject = new Tree(treeComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
		treeObject.setHeaderVisible(true);
		treeObject.setLinesVisible(true);
		
		viewerFilter = new NodeViewerFilter();
		treeViewer   = new TreeViewer(treeObject);
		treeViewer.addFilter(viewerFilter);
		treeViewer.setUseHashlookup(true);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new MetriculatorCellLabelProvider());
		
		ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);
		addViewerOpenListener(treeViewer);
		createTreeHeaderMenu();
		createTreeLabelColumn();
		createAndUpdateMetricTreeColumns();
	}

	private void bringToFront(Composite container, StructuredViewer viewer){
		stackLayout.topControl = container;
		activeViewer = viewer;
		Point p = parentComposite.getSize();
		parentComposite.setSize(new Point(p.x+1, p.y));
		parentComposite.setSize(p);
	}
	
	private void createTableLabelColumn() {
		TableColumn colNodes = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		colNodes.setText(SCOPE_COLUMN_TITLE);
		colNodes.setWidth(SCOPE_COLUMN_DEFAULT_WIDTH);
		colNodes.setMoveable(false);
		colNodes.setResizable(true);
	}

	private void createTreeLabelColumn() {
		TreeColumn colNodes = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		colNodes.setAlignment(SWT.LEFT);
		colNodes.setText(SCOPE_COLUMN_TITLE);
		colNodes.setWidth(SCOPE_COLUMN_DEFAULT_WIDTH);
		colNodes.setMoveable(false);
		colNodes.setResizable(true);
		// set inital sorter
		TreeColumnViewerSorter scopeSorter = new TreeColumnViewerSorter(treeViewer, colNodes) {
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				AbstractNode p1 = (AbstractNode) e1;
				AbstractNode p2 = (AbstractNode) e2;
				return p1.toString().compareToIgnoreCase(p2.toString());
			}
		};
		scopeSorter.setSorter(scopeSorter, INITIAL_SORT_ORDER);
	}

	private void createTreeHeaderMenu() {
		treeHeaderMenu = MetricColumnHeaderMenu.create(parentComposite.getShell(), treeViewer.getTree());
	}
	
	private void createTableHeaderMenu() {
		tableHeaderMenu = MetricColumnHeaderMenu.create(parentComposite.getShell(), tableViewer.getTable());
		getSite().registerContextMenu(TABLE_COLUMN_HEADER_MENU_ID, MetricColumnHeaderMenu.tableMenuManager, tableViewer);
	}
	
	private void createAndUpdateMetricTreeColumns() {
		for(AbstractMetric metric : MetriculatorPluginActivator.getDefault().getMetrics()){
			
			ToggleColumnActionItem<TreeColumn> actionItem = metricsTreeColumnActions.get(metric);
			
			if (actionItem == null) {

				TreeColumn col = MetricColumn.createFor(metric, treeViewer);
				actionItem = createMetricMenuItemFor(col);
				metricsTreeColumnActions.put(metric, actionItem);
			}
			actionItem.toggleVisibility();
		}
	}
	
	private void createAndUpdateMetricTableColumns() {
		
		for(AbstractMetric metric : MetriculatorPluginActivator.getDefault().getMetrics()){
			
			ToggleColumnActionItem<TableColumn> actionItem = metricsTableColumnActions.get(metric);
			
			if (actionItem == null) {

				TableColumn col = MetricColumn.createFor(metric, tableViewer);
				actionItem = createMetricMenuItemFor(col);
				metricsTableColumnActions.put(metric, actionItem);
			}
			actionItem.toggleVisibility();
		}		
	}	
	
	private ToggleColumnActionItem<TreeColumn> createMetricMenuItemFor(final TreeColumn column) {
		
		ToggleTreeColumnActionContrItem actionItem = new ToggleTreeColumnActionContrItem(column);
		MetricColumnHeaderMenu.treeMenuManager.add(actionItem);
		return actionItem;
	}
	
	private ToggleColumnActionItem<TableColumn> createMetricMenuItemFor(final TableColumn column) {
		
		ToggleTableColumnActionContrItem actionItem = new ToggleTableColumnActionContrItem(column);
		MetricColumnHeaderMenu.tableMenuManager.add(actionItem);
		return actionItem;
	}
	
	@Override
	public AbstractMetric getMetric(){
		TableColumn selectedCol = (TableColumn) MetricColumnHeaderMenu.getCurrColumn(tableHeaderMenu);
		if(selectedCol != null){
			return MetricColumn.getMetric(selectedCol);
		}
		return null;
	}
	
	@Override
	public Collection<AbstractNode> getNodes(){
		Collection<AbstractNode> filteredNodes = viewerFilter.getNodeFilter().takeFrom(currTreeBuilder.root.getChildren());
		return filteredNodes;
	}
	
	private void createActions() {
		createActionHybridView();
		createActionLogicalView();
		createActionExpandAll();
		createActionCollapseAll();
		createActionFilterComposite();
		createActionFilterFunction();
		createActionFilterNamespace();
		createActionFilterFile();
	}

	private void applyViewMode(ViewMode view, NodeFilter filter) {

		viewMode = view;
		actionLogicalView.setChecked(false);
		actionHybridView.setChecked(false);

		switch(view){
			case Logical:
				actionLogicalView.setChecked(true);
				currTreeBuilder = MetriculatorPluginActivator.getDefault().getLogicTreeBuilder();
				applyFilterMode(NodeFilter.none());
				bringToFront(treeComposite, treeViewer);
				break;
				
			case Hybrid:
				actionHybridView.setChecked(true);
				currTreeBuilder = MetriculatorPluginActivator.getDefault().getHybridTreeBuilder();
				applyFilterMode(NodeFilter.none());
				bringToFront(treeComposite, treeViewer);
				break;
				
			case Filtered:
				if(filter == null){
					filter = NodeFilter.none();
				}
				applyFilterMode(filter);
				bringToFront(tableComposite, tableViewer);
				break;
				
			default:
				break;
		}
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	private void applyFilterMode(NodeFilter filter) {

		viewerFilter.setNodeFilter(filter);
		
		actionFilterComposite.setChecked(filter instanceof NodeFilter.CompositeNodeFilter);
		actionFilterFile.setChecked(filter instanceof NodeFilter.FileNodeFilter);
		actionFilterFunction.setChecked(filter instanceof NodeFilter.FunctionNodeFilter);
		actionFilterNamespace.setChecked(filter instanceof NodeFilter.NamespaceNodeFilter);

		if (!(filter instanceof NodeFilter.NoneFilter)) {
			currTreeBuilder = MetriculatorPluginActivator.getDefault().getFlatTreeBuilder();
		}
	}

	private void createActionFilterFile() {
		actionFilterFile = new Action("only show file nodes", IAction.AS_CHECK_BOX)
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Filtered, NodeFilter.file());
				}else{
					applyViewMode(ViewMode.Hybrid, NodeFilter.none());
				}
				updateViewerData();
			}
		};
		actionFilterFile.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.FILE));
	}

	private void createActionFilterNamespace() {
		actionFilterNamespace = new Action("only show namespace nodes", IAction.AS_CHECK_BOX)
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Filtered, NodeFilter.namespace());
				}else{
					applyViewMode(ViewMode.Hybrid, NodeFilter.none());
				}
				updateViewerData();
			}
		};
		actionFilterNamespace.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.NAMESPACE));
	}

	private void createActionExpandAll() {
		actionExpandAll = new Action("expand all nodes")
		{
			public void run() {
				treeViewer.expandAll();
			}
		};
		actionExpandAll.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.EXPAND_ALL));
	}

	private void createActionCollapseAll() {
		actionCollapseAll = new Action(
				"collapse All Nodes",
				MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.COLLAPSE_ALL))
		{
			public void run() {
				treeViewer.collapseAll();
			}
		};
	}

	private void createActionHybridView() {
		actionHybridView = new Action("change to hybrid view mode", IAction.AS_RADIO_BUTTON) 
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Hybrid, NodeFilter.none());
					updateViewerData();
				}else{
					setChecked(true);
				}
			}
		};
		actionHybridView.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.FILESYSTEM));
	}
	
	private void createActionLogicalView() {
		actionLogicalView = new Action("change to logical view mode", IAction.AS_RADIO_BUTTON) 
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Logical, NodeFilter.none());
					updateViewerData();
				}else{
					setChecked(true);
				}
			}
		};
		actionLogicalView.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.VIEWMODE_LOGICAL));
	}

	private void createActionFilterComposite() {
		actionFilterComposite = new Action("only show composite nodes (class / structs)", IAction.AS_CHECK_BOX)
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Filtered, NodeFilter.composite());
				}else{
					applyViewMode(ViewMode.Hybrid, NodeFilter.none());
				}
				updateViewerData();
			}
		};
		actionFilterComposite.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.CLASS));
	}

	private void createActionFilterFunction() {
		actionFilterFunction = new Action("only show function nodes", IAction.AS_CHECK_BOX)
		{
			public void run() {
				if(isChecked()){
					applyViewMode(ViewMode.Filtered, NodeFilter.function());
				}else{
					applyViewMode(ViewMode.Hybrid, NodeFilter.none());
				}
				updateViewerData();
			}
		};			
		actionFilterFunction.setImageDescriptor(MetriculatorPluginActivator.getDefault().getImageDescriptor(Icon.Size16.FUNCTION));
	}

	private void addActionsToBars() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(new Separator());
		toolBarManager.add(actionHybridView);
		toolBarManager.add(actionLogicalView);
		toolBarManager.add(new Separator());
		toolBarManager.add(actionFilterFile);
		toolBarManager.add(actionFilterNamespace);
		toolBarManager.add(actionFilterComposite);
		toolBarManager.add(actionFilterFunction);
		toolBarManager.add(new Separator());
		toolBarManager.add(actionExpandAll);
		toolBarManager.add(actionCollapseAll);
	}

	private void addViewerOpenListener(StructuredViewer viewer) {
		viewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				openSelectedNode(event.getSelection());
			}
		});
	}

	private void openSelectedNode(ISelection selection) {
		AbstractNode node             = getFirstNodeFrom(selection);
		boolean selectionSupportsOpen = node instanceof NamespaceNode || node instanceof FunctionNode || node instanceof TypeDefNode || node instanceof FileNode;

		if (selectionSupportsOpen) {

			String filepath    = node.getFilePath();
			IEditorPart editor = null;

			try {
				editor = CodanEditorUtility.openInEditor(filepath, null);

				if (editor instanceof ITextEditor && !(node instanceof FileNode)) {
					ITextEditor textEditor = (ITextEditor) editor;
					textEditor.selectAndReveal(
							node.getNodeOffset(),
							node.getNodeLength());
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	private AbstractNode getFirstNodeFrom(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			return (AbstractNode) ssel.iterator().next();
		}
		return null;
	}

	@Override
	public void setFocus() {
		if(activeViewer instanceof TreeViewer){
			treeViewer.getControl().setFocus();
		} else 
		if(activeViewer instanceof TableViewer){
			tableViewer.getControl().setFocus();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		
		if(((JobState)arg) == JobState.JOB_DONE){
		
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					applyCodanFinishedState();
				}
			});
		}
		
		if(((JobState)arg) == JobState.JOB_ABOUT_TO_RUN){
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					applyCodanStartedState();
				}
			});
		}
	}
	
	class TableContentProvider implements IStructuredContentProvider{

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return currTreeBuilder.root.getChildren().toArray();
		}
	}

	class MetriculatorCellLabelProvider extends CellLabelProvider{

		@Override
		public void update(ViewerCell cell) {
			switch (cell.getColumnIndex()) {
				case 0:
					cell.setText(cell.getElement().toString());
					cell.setImage(MetriculatorPluginActivator.getDefault().getImageDescriptor(((AbstractNode)cell.getElement()).getIconPath()).createImage());
					break;
				default:
					AbstractMetric metric;
					if(activeViewer instanceof TreeViewer){
						metric = MetricColumn.getMetric(treeViewer.getTree().getColumn(cell.getColumnIndex()));
					}else{
						metric = MetricColumn.getMetric(tableViewer.getTable().getColumn(cell.getColumnIndex()));
					}
					applyProblemsOf(metric, cell);
					int metricValue = ((AbstractNode) cell.getElement()).getAggregatedValueOf(metric);
					cell.setText(NumberFormat.getInstance().format(metricValue));
					break;
			}
		}

		private void applyProblemsOf(AbstractMetric metric, ViewerCell toCell) {
			Collection<IProblem> problems = metric.getChecker().getProblemsFor((AbstractNode) toCell.getElement());
			
			if(problems != null){
				for(IProblem p : problems){
					if(p.getSeverity() == CodanSeverity.Warning){
						toCell.setBackground(AwtSwtColorConverter.LIGHT_ORANGE);
					}else if(p.getSeverity() == CodanSeverity.Error){
						toCell.setBackground(AwtSwtColorConverter.LIGHT_RED);
					}else if(p.getSeverity() == CodanSeverity.Info){
						toCell.setBackground(AwtSwtColorConverter.fromAwt(Color.WHITE));
					}
				}
			}else{
				toCell.setBackground(AwtSwtColorConverter.fromAwt(Color.WHITE));
			}
		}

		@Override
		public String getToolTipText(Object element) {
			if(getViewMode() != ViewMode.Logical && element instanceof AbstractNode){
				return String.format("'%s' , children: %s", 
						((AbstractNode)element).getPath(), 
						((AbstractNode) element).getChildren().size());
			}
			return null;
		}

		@Override
		public int getToolTipDisplayDelayTime(Object object) {
			return 100;
		}		
	}

	private void applyCodanFinishedState(){
		createAndUpdateMetricTreeColumns();
		createAndUpdateMetricTableColumns();
		applyViewMode(ViewMode.Hybrid, null);
		updateViewerData();
		MetricColumnHeaderMenu.updateItemSelections(MetricColumnHeaderMenu.treeMenuManager);
		MetricColumnHeaderMenu.updateItemSelections(MetricColumnHeaderMenu.tableMenuManager);
	}
	
	private void applyCodanStartedState(){
		treeViewer.setInput(null);
		tableViewer.setInput(null);
	}

	private void updateViewerData() {
		activeViewer.setInput(currTreeBuilder.root);
	}
}