package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;

public class FuncDefNodeInfo extends MemberNodeInfo {

	public FuncDefNodeInfo(ICPPASTFunctionDefinition astNode) {
		super(astNode);
		isFriend = astNode.getDeclSpecifier().getRawSignature().contains("friend");
	}

	@Override
	protected boolean prepareBinding(IASTNode astNode) {
		IASTName name = ((ICPPASTFunctionDefinition)astNode).getDeclarator().getName();
		binding  = name.resolveBinding();
		IIndex index = astNode.getTranslationUnit().getIndex();
		indexBinding = index.adaptBinding(binding);
		return binding != null || indexBinding != null;
	}

}
