package ch.hsr.ifs.cdt.metriculator.nodes.nodeInfo;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class FileSystemNodeInfo extends AbstractNodeInfo {

	private boolean isHeaderUnit = false;
	
	public FileSystemNodeInfo(IASTTranslationUnit astNode){
		super(astNode);
		// junit tests provide no astNode
		if(astNode != null){
			isHeaderUnit = astNode.isHeaderUnit();
		}
	}
	
	public boolean isHeaderUnit() {
		return isHeaderUnit;
	}

}
