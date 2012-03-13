package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.index.IIndex;

import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;

public abstract class MemberNodeInfo extends LogicalNodeInfo {

	protected boolean isMember;
	protected IBinding binding;
	protected IBinding indexBinding;
	protected String logicalOwnerName;
	protected String logicalName;

	protected MemberNodeInfo(IASTNode astNode) {
		super(astNode);
		isMember = false;
		if(prepareBinding(astNode)){
			prepareOwnership(binding.getOwner(), astNode.getTranslationUnit());
		}
	}

	@Override
	public boolean isMember() {
		return isMember;
	}

	@Override
	public String getLogicalOwnerName() {
		return logicalOwnerName;
	}

	@Override
	public String getLogicalName() {
		return logicalName;
	}

	@Override
	public IBinding getBinding() {
		return binding;
	}
	
	@Override
	public IBinding getIndexBinding() {
		return indexBinding;
	}

	@Override
	public void clearBindings() {
		binding = null;
	}

	abstract boolean prepareBinding(IASTNode astNode);

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

	public static IBinding getBindingFor(IASTName name, IASTTranslationUnit tu) {
		IBinding typeBinding, indexBinding;

		typeBinding  = name.resolveBinding();
		IIndex index = tu.getIndex();
		indexBinding = index.adaptBinding(typeBinding);

		return indexBinding == null ? typeBinding : indexBinding;
	}
	
	

}
