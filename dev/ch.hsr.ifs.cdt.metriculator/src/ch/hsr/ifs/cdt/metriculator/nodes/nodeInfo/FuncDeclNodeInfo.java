package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.index.IIndex;

public class FuncDeclNodeInfo extends MemberNodeInfo {

	public FuncDeclNodeInfo(ICPPASTFunctionDeclarator astNode) {
		super(astNode);
		isFriend = astNode.getRawSignature().contains("friend");
	}

	@Override
	protected boolean prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTFunctionDeclarator) astNode).getName();
		binding  = name.resolveBinding();
		IIndex index = astNode.getTranslationUnit().getIndex();
		indexBinding = index.adaptBinding(binding);
		return binding != null || indexBinding != null;
	}

}
