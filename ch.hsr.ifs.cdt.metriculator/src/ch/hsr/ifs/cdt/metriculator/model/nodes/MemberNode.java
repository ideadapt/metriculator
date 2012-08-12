package ch.hsr.ifs.cdt.metriculator.model.nodes;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;

public abstract class MemberNode extends LogicNode {

	protected boolean isMember         	= false;
	protected String logicalName       	= "";
	protected String logicalOwnerName 	= "";
	protected IIndexBinding indexBinding;

	protected MemberNode(String scopeUniqueName) {
		super(scopeUniqueName);
	}

	protected MemberNode(String scopeUniqueName, IASTNode astNode) {
		super(scopeUniqueName, astNode);

		if(prepareBinding(astNode)){
			prepareOwnership(indexBinding.getOwner(), astNode.getTranslationUnit());
		}
		prepareIsFriend(astNode);		
	}

	protected abstract void prepareIsFriend(IASTNode astNode);
	
	protected abstract IASTName getASTName(IASTNode astNode);
	
	protected boolean prepareBinding(IASTNode astNode) {
		IASTName name = getASTName(astNode);
		IIndex index = astNode.getTranslationUnit().getIndex();
		indexBinding = index.adaptBinding(name.resolveBinding());	
		return indexBinding != null;
	}

	public IIndexBinding getIndexBinding() {
		return indexBinding;
	}

	public void clearBindings() {
		indexBinding = null;
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

	protected void prepareOwnership(IIndexBinding owner, IASTTranslationUnit tu) {
		logicalName = indexBinding.toString();
		if(owner != null){
			logicalOwnerName = buildLogicalOwnerName(owner, tu);
			if(owner instanceof ICompositeType){
				isMember = true;
				indexBinding = null;
			}
		}
	}

	// TODO introduce StringBuilder
	protected String buildLogicalOwnerName(IBinding owner, IASTTranslationUnit tu) {

		if(owner == null){
			return "";
		}

		IBinding newOwner = null;
		IASTNode node = null;
		IASTName[] declarationsInAST = tu.getDeclarationsInAST(owner);
		
		if(declarationsInAST.length > 0){
			node = declarationsInAST[0].getParent();
			
			newOwner = CPPVisitor.findNameOwner(declarationsInAST[0], true);

			if(newOwner == null){
				if(isAnonymousNamespace(node)){
					return owner.getName().toString() + node.hashCode();
				}else{
					return owner.getName().toString();
				}
			}
		}


		return buildLogicalOwnerName(newOwner, tu) + TreeBuilder.PATH_SEPARATOR + owner.getName() + (isAnonymousNamespace(node) ? node.hashCode() : "");
	}

	private boolean isAnonymousNamespace(IASTNode node) {
		return node instanceof ICPPASTNamespaceDefinition && ((ICPPASTNamespaceDefinition) node).getName().toString().isEmpty();
	}
	

}
