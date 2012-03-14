package ch.hsr.ifs.cdt.metriculator.model.nodes;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public abstract class TypeNode extends MemberNode {

	protected TypeNode(String scopeUniqueName, IASTNode astNode) {
		super(scopeUniqueName, astNode);
	}

	protected TypeNode(String scopeUniqueName) {
		super(scopeUniqueName);
	}
	
//	protected TypeNode(IASTNode astNode){
//		super(astNode);
//		
//		isMember = false;
//		if(prepareBinding(astNode)){
//			prepareOwnership(binding.getOwner(), astNode.getTranslationUnit());
//		}
//		prepareIsFriend(astNode);
//	}

}
