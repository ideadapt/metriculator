package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;

public abstract class MemberNodeInfo extends LogicalNodeInfo {

	protected boolean isMember;
	protected IBinding binding;
	protected String logicalOwnerName;
	protected String logicalName;

	protected MemberNodeInfo(IASTNode astNode) {
		super(astNode);
		isMember = false;
		prepareBinding(astNode);
		prepareOwnership(binding.getOwner(), astNode.getTranslationUnit());

	}
	
	public boolean isMember() {
		return isMember;
	}
	
	public String getLogicalOwnerName() {
		return logicalOwnerName;
	}


	public String getLogicalName() {
		return logicalName;
	}

	abstract void prepareBinding(IASTNode astNode);
	
	private void prepareOwnership(IBinding owner, IASTTranslationUnit tu) {
		logicalName = binding.toString();
		if(owner != null){
			logicalOwnerName = buildLogicalOwnerName(binding.getOwner(), tu);
			if(owner instanceof ICompositeType){
				isMember = true;
				binding = null;
			}
		}
	}

	private String buildLogicalOwnerName(IBinding owner, IASTTranslationUnit tu) {
	
		IASTNode node = null;
	
		if(tu.getDeclarationsInAST(owner).length > 0){
			node = tu.getDeclarationsInAST(owner)[0].getParent();
	
			if(owner.getOwner() == null){
				if(node instanceof ICPPASTNamespaceDefinition && ((ICPPASTNamespaceDefinition) node).getName().toString().isEmpty()){
					return owner.getName().toString() + node.hashCode();
				}else{
					return owner.getName().toString();
				}
			}
		}
	
		return buildLogicalOwnerName(owner.getOwner(), tu) + TreeBuilder.PATH_SEPARATOR + owner.getName() + ((node instanceof ICPPASTNamespaceDefinition && ((ICPPASTNamespaceDefinition) node).getName().toString().isEmpty()) ? node.hashCode() : "");
	}

}
