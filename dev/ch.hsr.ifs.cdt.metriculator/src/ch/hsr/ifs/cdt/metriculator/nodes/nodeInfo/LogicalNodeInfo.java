package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public abstract class LogicalNodeInfo extends AbstractNodeInfo {
	
	protected boolean isFriend;

	protected LogicalNodeInfo(IASTNode astNode) {
		super(astNode);
		isFriend = false;
		prepareHashCode(astNode);
	}
	
	public boolean isFriend() {
		return isFriend;
	}
	
	@Override
	protected void prepareHashCode(IASTNode astNode) {
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
	}

	@Override
	public String getAstNodeHashCode() {
		return astNodeHashCode;
	}

}
