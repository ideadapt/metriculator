package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

public class NamespaceNodeInfo extends LogicalNodeInfo {

	public NamespaceNodeInfo(ICPPASTNamespaceDefinition astNode) {
		super(astNode);
	}

}
