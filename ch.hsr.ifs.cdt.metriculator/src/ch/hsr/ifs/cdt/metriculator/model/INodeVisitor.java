package ch.hsr.ifs.cdt.metriculator.model;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.FolderNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.ProjectNode;
import ch.hsr.ifs.cdt.metriculator.model.nodes.WorkspaceNode;

public interface INodeVisitor {
	public void visit(AbstractNode n);
	public void visit(WorkspaceNode n);
	public void visit(ProjectNode n);
	public void visit(FolderNode n);
}
