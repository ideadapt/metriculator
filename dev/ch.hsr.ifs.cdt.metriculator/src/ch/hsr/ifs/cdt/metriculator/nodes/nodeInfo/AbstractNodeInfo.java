package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.core.resources.IFile;


public abstract class AbstractNodeInfo {

	private String filePath;
	private int nodeOffSet;
	private int nodeLength;
	private int endingLineNumber;
	private int startingLineNumber;
	private int nodeOffSetStart;
	private int nodeOffsetEnd;
	private boolean isEclosedInMacroExpansion;
	protected String astNodeHashCode;

	protected AbstractNodeInfo(IASTNode astNode){
		if(astNode != null){
			prepareHashCode(astNode);
			prepareFilePath(astNode);
			prepareNodeLocations(astNode);
			prepareProblemLocation(astNode);
		}
	}

	protected AbstractNodeInfo() {
		filePath = "";
		astNodeHashCode = "";
	}

	public String getFilePath() {
		return filePath;
	}

	public int getNodeOffset() {
		return nodeOffSet;
	}

	public int getNodeLength() {
		return nodeLength;
	}

	public String getAstNodeHashCode(){
		return astNodeHashCode;
	}
	
	
	//TODO: move to typesnode
	public int getTypeKey() {
		return 0;
	}

	//TODO: move to filenode
	public boolean isHeaderUnit() {
		return false;
	}

	//TODO: move to MemberNode
	public IBinding getBinding() {
		return null;
	}
	
	//TODO: move to MemberNode
	public IBinding getIndexBinding() {
		return null;
	}

	//TODO: move to MemberNode
	public void clearBindings() {

	}

	//TODO: move to MemberNode
	public boolean isMember() {
		return false;
	}

	//TODO: move to MemberNode
	public String getLogicalOwnerName() {
		return null;
	}

	//TODO: move to MemberNode
	public String getLogicalName() {
		return null;
	}

	protected void prepareHashCode(IASTNode astNode) {
		astNodeHashCode = "";
	}

	protected void prepareFilePath(IASTNode astNode) {
		filePath = astNode.getTranslationUnit().getFilePath();
	}

	protected void prepareNodeLocations(IASTNode astNode) {
		nodeOffSet = astNode.getNodeLocations()[0].getNodeOffset();
		nodeLength = astNode.getNodeLocations()[0].getNodeLength();
	}

	public IProblemLocation createAndGetProblemLocation(IFile file) {
		IProblemLocationFactory locFactory = CodanRuntime.getInstance().getProblemLocationFactory();
		if(isEclosedInMacroExpansion || startingLineNumber == endingLineNumber){
			return locFactory.createProblemLocation(file, nodeOffSetStart, nodeOffsetEnd, startingLineNumber);
		}
		return locFactory.createProblemLocation(file, startingLineNumber);
	}

	protected void prepareProblemLocation(IASTNode astNode) {
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



}
