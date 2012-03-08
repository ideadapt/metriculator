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
	protected String astNodeHashCode;
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
		astNodeHashCode = Integer.valueOf(astNode.hashCode()).toString();
		prepareFilePath(astNode);
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
		prepareMembers(indexBinding.getOwner());
	}

	private void prepareMembers(IBinding owner) {
		logicalName = indexBinding.toString();
		if(owner != null){
			logicalOwnerName = buildLogicalOwnerName(indexBinding.getOwner());
			if(owner instanceof ICompositeType){
				isMember = true;
				indexBinding = null;
				typeBinding = null;
			}
		}
	}

	private String buildLogicalOwnerName(IBinding owner) {

		if(owner.getOwner() == null){
			return owner.getName().toString();
		}

		return buildLogicalOwnerName(owner.getOwner()) + TreeBuilder.PATH_SEPARATOR + owner.getName();
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((astNodeHashCode == null) ? 0 : astNodeHashCode.hashCode());
		result = prime * result + endingLineNumber;
		result = prime * result
				+ ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result
				+ ((indexBinding == null) ? 0 : indexBinding.hashCode());
		result = prime * result + (isCompositeTypeSpecifier ? 1231 : 1237);
		result = prime * result + (isEclosedInMacroExpansion ? 1231 : 1237);
		result = prime * result + (isElaboratedTypeSpecifier ? 1231 : 1237);
		result = prime * result + (isFriend ? 1231 : 1237);
		result = prime * result + (isFunctionDeclarator ? 1231 : 1237);
		result = prime * result + (isFunctionDefinition ? 1231 : 1237);
		result = prime * result + (isHeaderUnit ? 1231 : 1237);
		result = prime * result + (isMember ? 1231 : 1237);
		result = prime * result
				+ ((logicalName == null) ? 0 : logicalName.hashCode());
		result = prime
				* result
				+ ((logicalOwnerName == null) ? 0 : logicalOwnerName.hashCode());
		result = prime * result + nodeLength;
		result = prime * result + nodeOffSet;
		result = prime * result + nodeOffSetStart;
		result = prime * result + nodeOffsetEnd;
		result = prime * result + startingLineNumber;
		result = prime * result
				+ ((typeBinding == null) ? 0 : typeBinding.hashCode());
		result = prime * result + typeKey;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (astNodeHashCode == null) {
			if (other.astNodeHashCode != null)
				return false;
		} else if (!astNodeHashCode.equals(other.astNodeHashCode))
			return false;
		if (endingLineNumber != other.endingLineNumber)
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (indexBinding == null) {
			if (other.indexBinding != null)
				return false;
		} else if (!indexBinding.equals(other.indexBinding))
			return false;
		if (isCompositeTypeSpecifier != other.isCompositeTypeSpecifier)
			return false;
		if (isEclosedInMacroExpansion != other.isEclosedInMacroExpansion)
			return false;
		if (isElaboratedTypeSpecifier != other.isElaboratedTypeSpecifier)
			return false;
		if (isFriend != other.isFriend)
			return false;
		if (isFunctionDeclarator != other.isFunctionDeclarator)
			return false;
		if (isFunctionDefinition != other.isFunctionDefinition)
			return false;
		if (isHeaderUnit != other.isHeaderUnit)
			return false;
		if (isMember != other.isMember)
			return false;
		if (logicalName == null) {
			if (other.logicalName != null)
				return false;
		} else if (!logicalName.equals(other.logicalName))
			return false;
		if (logicalOwnerName == null) {
			if (other.logicalOwnerName != null)
				return false;
		} else if (!logicalOwnerName.equals(other.logicalOwnerName))
			return false;
		if (nodeLength != other.nodeLength)
			return false;
		if (nodeOffSet != other.nodeOffSet)
			return false;
		if (nodeOffSetStart != other.nodeOffSetStart)
			return false;
		if (nodeOffsetEnd != other.nodeOffsetEnd)
			return false;
		if (startingLineNumber != other.startingLineNumber)
			return false;
		if (typeBinding == null) {
			if (other.typeBinding != null)
				return false;
		} else if (!typeBinding.equals(other.typeBinding))
			return false;
		if (typeKey != other.typeKey)
			return false;
		return true;
	}

	private void prepareProblemLocation(IASTNode astNode){
		IASTFileLocation astLocation       = astNode.getFileLocation();

		startingLineNumber = astLocation.getStartingLineNumber();

		if (enclosedInMacroExpansion(astNode) && astNode instanceof IASTName) {
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

	private static boolean enclosedInMacroExpansion(IASTNode node) {
		IASTNodeLocation[] nodeLocations = node.getNodeLocations();
		return nodeLocations.length == 1 && nodeLocations[0] instanceof IASTMacroExpansionLocation;
	}

	public void clearBindings() {
		indexBinding = null;
		typeBinding = null;	
	}

}
