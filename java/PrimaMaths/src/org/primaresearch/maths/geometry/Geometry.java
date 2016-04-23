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

import java.awt.geom.Point2D;

/**
 * Helper class for different calculations
 * 
 * @author Christian Clausner
 *
 */
public class Geometry {

	/**
	 * Calculates the intersection point of the linear functions defined by the 4 given points.
	 *
	 * See: http://paulbourke.net/geometry/lineline2d/
	 * 
	 * 'ax1', 'ay1', 'ax2', 'ay2' (in) - Points that define linear function a.
	 * 'bx1', 'by1', 'bx2', 'by2' (in) - Points that define linear function b.
	 * 
	 * @return Intersection point or <code>null</code>.
	 *
	 */
	public static Point2D.Double getInterceptionPointOfTwoLines(double ax1, double ay1, double ax2, double ay2, 
													double bx1, double by1, double bx2, double by2)	{
		double dividend = ((bx2-bx1)*(ay1-by1) - (by2-by1)*(ax1-bx1));
		double divisor = ((by2-by1)*(ax2-ax1) - (bx2-bx1)*(ay2-ay1));

		if (divisor == 0.0)
			return null; //Parallel lines

		double ua =  dividend / divisor;

		Point2D.Double intersect = new Point2D.Double(ax1 + ua*(ax2-ax1), ay1 + ua*(ay2-ay1));

		return intersect;
	}
}
