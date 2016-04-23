/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.maths.geometry;

import java.io.Serializable;

/**
 * Simple two-dimensional point.
 * 
 * @author Christian Clausner
 *
 */
public class Point implements Serializable {

	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	
	/**
	 * Constructor with initialisation to 0,0.
	 */
	public Point() {
		this(0,0);
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Calculates the distance between this point and a given other point.
	 */
	public double calculateDistance(int xOther, int yOther) {
		return Math.sqrt(Math.pow((double)(xOther - x),2) + Math.pow((double)(yOther - y),2));
	}
	
	/**
	 * Calculates the distance between this point and a given line.
	 * @param pointOnLine Nearest point on line (output, optional) 
	 * @return Distance
	 */
	public double calculateDistance(int lineX1, int lineY1, int lineX2, int lineY2, Point pointOnLine) {
		double LineMag;
		double U;
	 
		int XDiff = lineX2 - lineX1;
		int YDiff = lineY2 - lineY1;

		LineMag = Math.sqrt((double) (XDiff * XDiff + YDiff * YDiff));
	 
		U = (((x - lineX1) * XDiff) +
		     ((y - lineY1) * YDiff)) /
	             (LineMag * LineMag);
	 
		if(U < 0.0)
			return calculateDistance(lineX1, lineY1);
		else if(U > 1.0)
			return calculateDistance(lineX2, lineY2);
	 
		int IntX = (int) ((lineX1 + U * XDiff)+0.5); //CC 24.05.2010 - added +0.5 to round
		int IntY = (int) ((lineY1 + U * YDiff)+0.5);

		//Return also the intersection point
		if (pointOnLine != null)
		{
			pointOnLine.x = IntX;
			pointOnLine.y = IntY;
		}
	 
		XDiff = x - IntX;
		YDiff = y - IntY;

		return Math.sqrt((double) (XDiff * XDiff + YDiff * YDiff));
	}
}
