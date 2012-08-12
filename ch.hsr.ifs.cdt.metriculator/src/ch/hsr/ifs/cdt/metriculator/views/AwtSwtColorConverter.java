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

package ch.hsr.ifs.cdt.metriculator.views;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

/**
 * @author Ueli Kunz
 * */
public class AwtSwtColorConverter {

	public static org.eclipse.swt.graphics.Color fromAwt(java.awt.Color colour) {
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(),
													colour.getRed(),
													colour.getGreen(), 
													colour.getBlue());
	}

	/**
	 * alpha value has no effect. its just there to satisfy awt color class
	 * */
	public static org.eclipse.swt.graphics.Color fromAwt(Color color, int alpha) {
		return fromAwt(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
	}
	
	public static final org.eclipse.swt.graphics.Color LIGHT_ORANGE = fromAwt(new Color(255, 204, 51));
	public static final org.eclipse.swt.graphics.Color LIGHT_RED    = fromAwt(new Color(255, 122, 122));
}
