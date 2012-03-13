/******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik 
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 * 	Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/

package ch.hsr.ifs.cdt.metriculator.model.nodes;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.cdt.metriculator.model.TreeBuilder;


public class NodeInfo {

	private boolean isFunctionDeclarator = false;
	private boolean isFunctionDefinition = false;
	private boolean isElaboratedTypeSpecifier = false;
	private boolean isCompositeTypeSpecifier = false;
	private boolean isHeaderUnit = false;
	private IBinding indexBinding;
	private IBinding typeBinding;
	private String filePath;
	private int nodeOffSet;
	private int nodeLength;
	private int typeKey;
	protected String astNodeHashCode = "";
	private int endingLineNumber;
	private int startingLineNumber;
	private int nodeOffSetStart;
	private int nodeOffsetEnd;
	private boolean isEclosedInMacroExpansion;
	private boolean isFriend;
	private boolean isMember;
	private String logicalOwnerName;
	private String logicalName;

	public NodeInfo(){
	}

	public NodeInfo(IASTNode astNode) {
		// junit tests provide no astNode
		if(astNode != null){
			astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		}
	}

	public NodeInfo(ICPPASTCompositeTypeSpecifier astNode){
		isCompositeTypeSpecifier = true;
		typeKey = astNode.getKey();
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		prepareFilePath(astNode);
		prepareNodeLocations(astNode);
		prepareBindingFor(astNode.getTranslationUnit(), astNode.getName());
		prepareProblemLocation(astNode);
		isFriend = ((ICPPASTDeclSpecifier)astNode).isFriend();
	}

	public NodeInfo(ICPPASTElaboratedTypeSpecifier astNode) {
		isElaboratedTypeSpecifier = true;
		typeKey = astNode.getKind();
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		prepareFilePath(astNode);
		prepareNodeLocations(astNode);
		prepareBindingFor(astNode.getTranslationUnit(), astNode.getName());
		prepareProblemLocation(astNode);
		isFriend = ((ICPPASTDeclSpecifier)astNode).isFriend();
	}

	public NodeInfo(IASTTranslationUnit astNode){
		// junit tests provide no astNode
		if(astNode != null){
			isHeaderUnit = astNode.isHeaderUnit();
			astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
			prepareFilePath(astNode);
			prepareNodeLocations(astNode);
			prepareProblemLocation(astNode);
		}
	}

	public NodeInfo(ICPPASTFunctionDefinition astNode){
		prepareFilePath(astNode);
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		isFunctionDefinition = true;
		prepareBindingFor(astNode.getTranslationUnit(), astNode.getDeclarator());
		prepareNodeLocations(astNode);
		prepareProblemLocation(astNode);
		isFriend = astNode.getDeclSpecifier().getRawSignature().contains("friend");
	}

	public NodeInfo(ICPPASTFunctionDeclarator astNode){
		prepareFilePath(astNode);
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		isFunctionDeclarator = true;
		prepareBindingFor(astNode.getTranslationUnit(), astNode);
		prepareNodeLocations(astNode);
		prepareProblemLocation(astNode);
		isFriend = astNode.getRawSignature().contains("friend");
	}

	public NodeInfo(ICPPASTNamespaceDefinition astNode){
		prepareFilePath(astNode);
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		prepareNodeLocations(astNode);
	}

	public boolean isFunctionDeclarator() {
		return isFunctionDeclarator;
	}

	public boolean isFunctionDefinition() {
		return isFunctionDefinition;
	}

	public IBinding getBinding(){
		return indexBinding;
	}

	public String getFilePath(){
		return filePath;
	}

	public int getNodeOffset(){
		return nodeOffSet;
	}

	public int getNodeLength(){
		return nodeLength;
	}

	public boolean isElaboratedTypeSpecifier() {
		return isElaboratedTypeSpecifier;
	}

	public boolean isCompositeTypeSpecifier() {
		return isCompositeTypeSpecifier;
	}

	public boolean isFriend(){
		return isFriend;
	}

	public int getTypeKey() {
		return typeKey;
	}

	public String getLogicalOwnerName() {
		return logicalOwnerName;
	}


	public String getLogicalName() {
		return logicalName;
	}

	public boolean isMember() {
		return isMember;
	}

	private void prepareFilePath(IASTNode node){
		filePath = node.getTranslationUnit().getFilePath();
	}

	private void prepareBindingFor(IASTTranslationUnit tu, IASTFunctionDeclarator declarator) {
		prepareBindingFor(tu, declarator.getName());
	}

	private void prepareBindingFor(IASTTranslationUnit tu, IASTName name) {
		typeBinding  = name.resolveBinding();
		IIndex index = tu.getIndex();
		indexBinding = index.adaptBinding(typeBinding);

		if(indexBinding == null){
			indexBinding = typeBinding;
		}
		prepareOwnership(indexBinding.getOwner(), tu);
	}

	private void prepareOwnership(IBinding owner, IASTTranslationUnit tu) {
		logicalName = indexBinding.toString();
		if(owner != null){
			logicalOwnerName = buildLogicalOwnerName(indexBinding.getOwner(), tu);
			if(owner instanceof ICompositeType){
				isMember = true;
				indexBinding = null;
				typeBinding = null;
			}
		}
	}

	private String buildLogicalOwnerName(IBinding owner, IASTTranslationUnit tu) {

		if(owner == null){
			return "";
		}
		
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


	public static IBinding getBindingFor(IASTName name, IASTTranslationUnit tu){
		IBinding typeBinding, indexBinding;

		typeBinding  = name.resolveBinding();
		IIndex index = tu.getIndex();
		indexBinding = index.adaptBinding(typeBinding);

		return indexBinding == null ? typeBinding : indexBinding;
	}

	private void prepareNodeLocations(IASTNode astNode){
		nodeOffSet = astNode.getNodeLocations()[0].getNodeOffset();
		nodeLength = astNode.getNodeLocations()[0].getNodeLength();
	}

	public boolean isHeaderUnit() {
		return isHeaderUnit;
	}

	public IBinding getTypeBinding(){
		return typeBinding;
	}

	public String getASTNodeHash() {
		return astNodeHashCode;
	}

	public IProblemLocation createAndGetProblemLocation(IFile file) {
		IProblemLocationFactory locFactory = CodanRuntime.getInstance().getProblemLocationFactory();
		if(isEclosedInMacroExpansion || startingLineNumber == endingLineNumber){
			return locFactory.createProblemLocation(file, nodeOffSetStart, nodeOffsetEnd, startingLineNumber);
		}
		return locFactory.createProblemLocation(file, startingLineNumber);
	}

	private void prepareProblemLocation(IASTNode astNode){
		IASTFileLocation astLocation       = astNode.getFileLocation();

		startingLineNumber = astLocation.getStartingLineNumber();

		if (isEnclosedInMacroExpansion(astNode) && astNode instanceof IASTName) {
			isEclosedInMacroExpansion = true;
			IASTImageLocation imageLocation = ((IASTName) astNode).getImageLocation();

			if (imageLocation != null) {
				nodeOffSetStart = imageLocation.getNodeOffset();
				nodeOffsetEnd   = nodeOffSetStart + imageLocation.getNodeLength();
				return;
			}
		}

		endingLineNumber = astLocation.getEndingLineNumber();
		if (startingLineNumber == endingLineNumber) {
			nodeOffSetStart = astLocation.getNodeOffset();
			nodeOffsetEnd = nodeOffSetStart + astLocation.getNodeLength();
			return;
		}

	}

	private static boolean isEnclosedInMacroExpansion(IASTNode node) {
		IASTNodeLocation[] nodeLocations = node.getNodeLocations();
		return nodeLocations.length == 1 && nodeLocations[0] instanceof IASTMacroExpansionLocation;
	}

	public void clearBindings() {
		indexBinding = null;
		typeBinding = null;	
	}
}
