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

public final class CompositeValue {

	public int nodeValue = 0;
	/*
	 * sum of the nodeValues of all descendant nodes (or sum of aggregatedValues of all children)
	 * */
	public int aggregatedValue = 0;
	
	public static CompositeValue copy(CompositeValue c){
		return new CompositeValue(c.nodeValue, c.aggregatedValue);
	}
	
	private CompositeValue(int nodeValue, int aggregatedValue){
		this.nodeValue = nodeValue;
		this.aggregatedValue = aggregatedValue;
	}
	
	public CompositeValue(){
	}
}
