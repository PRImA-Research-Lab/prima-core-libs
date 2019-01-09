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
package org.primaresearch.dla.page.layout;

import java.util.Comparator;

import org.primaresearch.dla.page.layout.shared.GeometricObject;

/**
 * Comparator to sort geometric objects by bounding box position (left or top).
 * Use the getInstance method to get a static instance of the comparator.
 * 
 * @author Christian Clausner
 */
public class GeometricObjectPositionComparator implements Comparator<GeometricObject> {

	private static GeometricObjectPositionComparator instanceToSortByX = null;
	private static GeometricObjectPositionComparator instanceToSortByY = null;
	
	private boolean sortByX;
	
	/**
	 * Constructor
	 * @param sortByX Set to <code>true</code> to sort objects left-to-right or to <code>false</code> to sort top-to-bottom
	 */
	private GeometricObjectPositionComparator(boolean sortByX) {
		this.sortByX = sortByX;
	}
	
	/**
	 * Creates a comparator
	 * @param sortByX Set to <code>true</code> to sort objects left-to-right or to <code>false</code> to sort top-to-bottom
	 * @return Comparator object
	 */
	public static GeometricObjectPositionComparator getInstance(boolean sortByX) {
		if (sortByX) {
			if (instanceToSortByX == null)
				instanceToSortByX = new GeometricObjectPositionComparator(true);
			return instanceToSortByX;
		}
		else { //sort by y
			if (instanceToSortByY == null)
				instanceToSortByY = new GeometricObjectPositionComparator(false);
			return instanceToSortByY;
		}
	}
	
	
	@Override
	public int compare(GeometricObject obj1, GeometricObject obj2) {
		if (sortByX) {
			int x1 = obj1.getCoords().getBoundingBox().left;
			int x2 = obj2.getCoords().getBoundingBox().left;
			return x1 - x2;
		} else {
			int y1 = obj1.getCoords().getBoundingBox().top;
			int y2 = obj2.getCoords().getBoundingBox().top;
			return y1 - y2;
		}
	}
	
	
}
