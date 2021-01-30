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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Polygon class holding a list of points and providing some geometric operations.
 * 
 * @author Christian Clausner
 *
 */
public class Polygon implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Point> points = new ArrayList<Point>();
	
	private Rect boundingBox = null;
	
	private Double confidence = null;
	
	public Polygon() {
		
	}
	
	/**
	 * Returns the number of points of this polygon.
	 */
	public int getSize() {
		synchronized (this) {
			return points.size();
		}
	}

	/**
	 * Returns the polygon point at the given index.
	 */
	public Point getPoint(int index) {
		synchronized (this) {
			return points.get(index);
		}
	}

	/**
	 * Adds a point at the end of the polygon.
	 */
	public void addPoint(Point p) {
		synchronized (this) {
			points.add(p);
			setBoundingBoxOutdated();
		}
	}
	
	/**
	 * Adds a point at the end of the polygon.
	 */
	public void addPoint(int x, int y) {
		synchronized (this) {
			points.add(new Point(x,y));
			setBoundingBoxOutdated();
		}
	}
	
	/**
	 * Inserts a polygon point at the given position.
	 * @param insertAfter Index of point before the new point.
	 * @param p New point
	 */
	public void insertPoint(int insertAfter, Point p) {
		synchronized (this) {
			points.add(insertAfter+1, p);
			setBoundingBoxOutdated();
		}
	}
	
	/**
	 * Removes the given point from the polygon.
	 */
	public void removePoint(Point p) {
		synchronized (this) {
			points.remove(p);
			setBoundingBoxOutdated();
		}
	}
	
	/**
	 * Returns the bounding box of the polygon (the user has to make sure that
	 * {@link #setBoundingBoxOutdated() setBoundingBoxOutdated} is called when points are changed manually).
	 * 
	 * @return Rectangle
	 */
	public Rect getBoundingBox() {
		synchronized (this) {
			if (boundingBox == null)
				updateBoundingBox();
			return boundingBox;
		}
	}
	
	private void updateBoundingBox() {
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		
		synchronized (this) {
			Point p;
			for (int i=0; i<points.size(); i++) {
				p = points.get(i);
				if (p.x < left)
					left = p.x;
				if (p.y < top)
					top = p.y;
				if (p.x > right)
					right = p.x;
				if (p.y > bottom)
					bottom = p.y;
			}
			
			this.boundingBox = new Rect(left, top, right, bottom);
		}
	}
	
	/**
	 * Mark the bounding box as 'to be updated'.
	 */
	public void setBoundingBoxOutdated() {
		this.boundingBox = null;
	}
	
	//TODO Check polygon line as well (add parameter)
	/**
	 * Checks if the given point is inside the polygon. Note that the polygon line does not count as inside.
	 */
	public boolean isPointInside(int x, int y) {
		
		synchronized (this) {
			//Check bounding box first
			if (!getBoundingBox().isPointInside(x, y))
				return false;
			
			//Is point inside algorithm:
			// (See http://www.codeproject.com/KB/recipes/geometry.aspx )
	
			int  	j, inside_flag;
			double 	dv0 ;
			int     crossings;
			boolean xflag0, yflag0, yflag1 = false;
			double	 vertex0x, vertex0y, vertex1x = 0, vertex1y = 0;
			Point 	vertex0, vertex1, P;
		 
			vertex0 = points.get(points.size()-1);
			vertex0x = vertex0.x;
			vertex0y = vertex0.y;
		 
		    //Get test bit for above/below Y axis 
		    yflag0 = ( dv0 = vertex0y - y ) >= 0.0;
		 
		    crossings = 0;
			j = 0;
			for (Iterator<Point> it = points.iterator(); it.hasNext(); )
			{
				P = it.next();
		        // cleverness:  bobble between filling endpoints of edges, so
				// that the previous edge's shared endpoint is maintained.
				if ( (j & 0x1) != 0 ) 
				{
					vertex0 = P;
					vertex0x = vertex0.x;
					vertex0y = vertex0.y;
					yflag0 = ( dv0 = vertex0y - y ) >= 0.0 ;
				} 
				else 
				{
					vertex1 = P;
					vertex1x = vertex1.x;
					vertex1y = vertex1.y;
					yflag1 = ( vertex1y >= y ) ;
				}
		 
				// check if points not both above/below X axis - can't hit ray 
				if (yflag0 != yflag1) 
				{
		            // check if points on same side of Y axis 
		            if ( ( xflag0 = ( vertex0x >= x ) ) == ( vertex1x >= x ) ) 
					{
		                if ( xflag0 ) 
							crossings++;
		            } 
					else 
					{
		                // compute intersection of pgon segment with X ray, note
		                // if > point's X.
		                //
		                crossings += (vertex0x - dv0 * (vertex1x-vertex0x)/(vertex1y-vertex0y)) >= x ? 1 : 0;
		            }
		        }
				j++;
		    }
		 
		    // test if crossings is odd
		    // if all we care about is winding number > 0, then just:
		    //       inside_flag = crossings > 0;
		 
			inside_flag = crossings & 0x01;
		 
		    return inside_flag != 0;
		}
	}
	
	/**
	 * Calculates the perimeter of this polygon
	 * @return Length (0.0 if not a polygon)
	 */
	public double calculateLength() {
		if (points.size() <= 1)
			return 0.0;
		
		double length = 0.0;
		Point a = null;
		Point b = points.get(points.size()-1);
		for (int i=0; i<points.size(); i++) {
			a = b;
			b = points.get(i);
			if (a == null || b == null)
				continue;
			length += a.calculateDistance(b.x, b.y);
		}
		return length;
	}
	
	/**
	 * Creates a deep copy of this polygon.
	 */
	public Polygon clone() {
		Polygon copy = new Polygon();
		synchronized (this) {
			for (int i=0; i<points.size(); i++)
				copy.addPoint(points.get(i).x, points.get(i).y);
		}
		return copy;
	}

	/**
	 * E.g. polygon recognition confidence
	 * @return 0.0..1.0
	 */
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * E.g. polygon recognition confidence
	 * @param confidence 0.0..1.0
	 */
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	
	
}
