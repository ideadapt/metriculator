package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

public class NamespaceNodeInfo extends LogicalNodeInfo {

	protected NamespaceNodeInfo(ICPPASTNamespaceDefinition astNode) {
		super(astNode);
		prepareHashCode(astNode);
	}
	
	protected void prepareHashCode(IASTNode astNode) {
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
	}

}
