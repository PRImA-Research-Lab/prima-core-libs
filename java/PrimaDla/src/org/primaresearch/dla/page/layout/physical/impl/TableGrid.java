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
package org.primaresearch.dla.page.layout.physical.impl;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.maths.geometry.Polygon;

/**
 * Single grid (matrix of points) that can be used for tables
 * 
 * @author Christian Clausner
 *
 */
public class TableGrid {
	
	private List<TableGridRow> rows = new ArrayList<TableGridRow>();
	
	public List<TableGridRow> getRows() {
		return rows;
	}

	/**
	 * Single row of table grid with points at intersections
	 * 
	 * @author Christian Clausner
	 *
	 */
	public static final class TableGridRow implements GeometricObject{

		private Polygon gridPoints = null;  
		
		@Override
		public Polygon getCoords() {
			return gridPoints;
		}

		@Override
		public void setCoords(Polygon coords) {
			gridPoints = coords;			
		}			
	}

}


