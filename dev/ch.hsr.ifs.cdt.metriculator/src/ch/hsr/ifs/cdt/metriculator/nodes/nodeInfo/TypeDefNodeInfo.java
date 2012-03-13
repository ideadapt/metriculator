package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;


public class TypeDefNodeInfo extends MemberNodeInfo {

	private int typeKey;

	public TypeDefNodeInfo(ICPPASTCompositeTypeSpecifier astNode) {
		super(astNode);
		isFriend = ((ICPPASTDeclSpecifier)astNode).isFriend();
		typeKey = astNode.getKey();
	}
	
	@Override
	public int getTypeKey() {
		return typeKey;
	}

	@Override
	protected boolean prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTCompositeTypeSpecifier) astNode).getName();
		binding  = name.resolveBinding();
		return binding != null;
	}


}
