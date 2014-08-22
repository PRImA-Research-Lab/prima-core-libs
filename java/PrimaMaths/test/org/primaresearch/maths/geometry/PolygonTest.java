/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.maths.geometry.Polygon;

public class PolygonTest {

	@Test
	public void testIsPointInside() {
		//Triangle
		Polygon poly = new Polygon();
		poly.addPoint(100, 100);
		poly.addPoint(200, 200);
		poly.addPoint(0, 200);
		
		//Points inside?
		assertTrue(poly.isPointInside(100, 101));
		assertFalse(poly.isPointInside(100, 99));
		
		//Bounding box
		Rect bb = poly.getBoundingBox();
		assertNotNull(bb);
		assertTrue(bb.left == 0);
		assertTrue(bb.right == 200);
		assertTrue(bb.top == 100);
		assertTrue(bb.bottom == 200);
	}

}
