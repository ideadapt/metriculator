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

package ch.hsr.ifs.cdt.metriculator.model;

import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class FlatTreeBuilder extends AbstractTreeBuilder {

	public FlatTreeBuilder(AbstractNode root) {
		this.root = root;
	}

	public static FlatTreeBuilder buildFrom(AbstractTreeBuilder treeBuilder){
		PreOrderTreeVisitor visitor = new PreOrderFlatTreeVisitor();

		visitor.visit(treeBuilder.root);
		return new FlatTreeBuilder(visitor.rootNode);
	}
}
