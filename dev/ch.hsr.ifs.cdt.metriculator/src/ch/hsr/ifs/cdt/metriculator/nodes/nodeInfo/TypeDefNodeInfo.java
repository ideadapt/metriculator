package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;


public class TypeDefNodeInfo extends MemberNodeInfo {

	private int typeKey;

	protected TypeDefNodeInfo(ICPPASTCompositeTypeSpecifier astNode) {
		super(astNode);
		isFriend = ((ICPPASTDeclSpecifier)astNode).isFriend();
		typeKey = astNode.getKey();
	}
	
	public int getTypeKey() {
		return typeKey;
	}

	@Override
	protected void prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTCompositeTypeSpecifier) astNode).getName();
		binding  = name.resolveBinding();
	}


}
