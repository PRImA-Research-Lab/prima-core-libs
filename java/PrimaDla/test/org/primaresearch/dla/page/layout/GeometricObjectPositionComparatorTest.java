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
package org.primaresearch.dla.page.layout;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.maths.geometry.Polygon;

public class GeometricObjectPositionComparatorTest {

	@Test
	public void testCompare() {
		Polygon polygon1 = new Polygon();
		polygon1.addPoint(10, 10);
		polygon1.addPoint(200, 10);
		polygon1.addPoint(200, 100);
		polygon1.addPoint(10, 100);
		GeometricObjectImpl obj1 = new GeometricObjectImpl(polygon1);
		
		Polygon polygon2 = new Polygon();
		polygon2.addPoint(150, 80);
		polygon2.addPoint(300, 80);
		polygon2.addPoint(300, 150);
		polygon2.addPoint(150, 150);
		GeometricObjectImpl obj2 = new GeometricObjectImpl(polygon2);

		Polygon polygon3 = new Polygon();
		polygon3.addPoint(400, 30);
		polygon3.addPoint(500, 30);
		polygon3.addPoint(500, 60);
		polygon3.addPoint(400, 60);
		GeometricObjectImpl obj3 = new GeometricObjectImpl(polygon3);
		
		List<GeometricObject> list = new ArrayList<GeometricObject>();
		list.add(obj2);
		list.add(obj1);
		list.add(obj3);
		
		//Sort by x
		Collections.sort(list, GeometricObjectPositionComparator.getInstance(true));
		assertTrue(list.get(0) == obj1);
		assertTrue(list.get(1) == obj2);
		assertTrue(list.get(2) == obj3);

		//Sort by y
		Collections.sort(list, GeometricObjectPositionComparator.getInstance(false));
		assertTrue(list.get(0) == obj1);
		assertTrue(list.get(1) == obj3);
		assertTrue(list.get(2) == obj2);
	}

}
