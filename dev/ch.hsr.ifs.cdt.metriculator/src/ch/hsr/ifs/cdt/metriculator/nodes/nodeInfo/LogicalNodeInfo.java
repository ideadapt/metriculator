package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public abstract class LogicalNodeInfo extends AbstractNodeInfo {
	
	protected boolean isFriend;

	protected LogicalNodeInfo(IASTNode astNode) {
		super(astNode);
		isFriend = false;
	}
	
	public boolean isFriend() {
		return isFriend;
	}
	

}
