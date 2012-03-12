package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;

public class FuncDefNodeInfo extends MemberNodeInfo {

	protected FuncDefNodeInfo(ICPPASTFunctionDefinition astNode) {
		super(astNode);
		isFriend = astNode.getDeclSpecifier().getRawSignature().contains("friend");
	}

	@Override
	void prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTFunctionDeclarator) astNode).getName();
		binding  = name.resolveBinding();
		IIndex index = astNode.getTranslationUnit().getIndex();
		binding = index.adaptBinding(binding);
	}

}
