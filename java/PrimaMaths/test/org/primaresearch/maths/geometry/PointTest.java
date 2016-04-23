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

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

	@Test
	public void testCalculateDistanceToOtherPoint() {
		Point p = new Point(10, 20);
		double dist = p.calculateDistance(30, 20);
		assertEquals(20.0, dist, 0.1);
	}

	@Test
	public void testCalculateDistanceToLine() {
		Point p = new Point(10, 20);
		Point onLine = new Point();
		double dist = p.calculateDistance(0, 40, 100, 40, onLine);
		assertEquals(20.0, dist, 0.1);
		assertTrue(onLine.x == 10 && onLine.y == 40);
	}

}
