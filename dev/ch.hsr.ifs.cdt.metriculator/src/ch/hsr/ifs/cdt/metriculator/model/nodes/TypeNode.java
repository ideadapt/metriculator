package ch.hsr.ifs.cdt.metriculator.model.nodes;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import ch.hsr.ifs.cdt.metriculator.resources.Icon;

public abstract class TypeNode extends MemberNode {

	protected int typeKey;
	
	protected TypeNode(String scopeUniqueName, IASTNode astNode) {
		super(scopeUniqueName, astNode);
	}

	protected TypeNode(String scopeUniqueName) {
		super(scopeUniqueName);
	}
	
	public int getTypeKey() {
		return typeKey;
	}
	
	@Override
	public String getIconPath() {
		int key = typeKey;

		switch(key){
			case ICPPASTCompositeTypeSpecifier.k_struct:
				return Icon.Size16.STRUCT;
			case ICPPASTCompositeTypeSpecifier.k_class:
				return Icon.Size16.CLASS;
			case ICPPASTCompositeTypeSpecifier.k_union:
				return Icon.Size16.UNION;
			default:
				return Icon.Size16.CLASS;
		}
	}	
}
