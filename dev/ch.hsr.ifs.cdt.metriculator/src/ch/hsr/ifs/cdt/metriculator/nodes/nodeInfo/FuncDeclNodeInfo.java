package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.index.IIndex;

public class FuncDeclNodeInfo extends MemberNodeInfo {

	protected FuncDeclNodeInfo(ICPPASTFunctionDeclarator astNode) {
		super(astNode);
		isFriend = astNode.getRawSignature().contains("friend");
	}

	@Override
	void prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTFunctionDeclarator) astNode).getName();
		binding  = name.resolveBinding();
		IIndex index = astNode.getTranslationUnit().getIndex();
		binding = index.adaptBinding(binding);
	}

}
