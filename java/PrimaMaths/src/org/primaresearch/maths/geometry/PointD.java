/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
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
 * Simple two-dimensional point with doubles.
 * 
 * @author Christian Clausner
 *
 */
public class PointD implements Serializable  {

	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	
	/**
	 * Constructor with initialisation to 0,0.
	 */
	public PointD() {
		this(0.0,0.0);
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Calculates the distance between this point and a given other point.
	 */
	public double calculateDistance(double xOther, double yOther) {
		return Math.sqrt(Math.pow((xOther - x),2) + Math.pow((yOther - y),2));
	}
	
	/**
	 * Calculates the distance between this point and a given line.
	 * @param pointOnLine Nearest point on line (output, optional) 
	 * @return Distance
	 */
	public double calculateDistance(double lineX1, double lineY1, double lineX2, double lineY2, PointD pointOnLine) {
		double LineMag;
		double U;
	 
		double XDiff = lineX2 - lineX1;
		double YDiff = lineY2 - lineY1;

		LineMag = Math.sqrt((double) (XDiff * XDiff + YDiff * YDiff));
	 
		U = (((x - lineX1) * XDiff) +
		     ((y - lineY1) * YDiff)) /
	             (LineMag * LineMag);
	 
		if(U < 0.0)
			return calculateDistance(lineX1, lineY1);
		else if(U > 1.0)
			return calculateDistance(lineX2, lineY2);
	 
		double IntX = ((lineX1 + U * XDiff)); //CC 24.05.2010 - added +0.5 to round
		double IntY = ((lineY1 + U * YDiff));

		//Return also the intersection point
		if (pointOnLine != null)
		{
			pointOnLine.x = IntX;
			pointOnLine.y = IntY;
		}
	 
		XDiff = x - IntX;
		YDiff = y - IntY;

		return Math.sqrt( (XDiff * XDiff + YDiff * YDiff));
	}
}
