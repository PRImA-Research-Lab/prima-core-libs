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
 * A simple rectangle.
 * 
 * @author Christian Clausner
 *
 */
public class Rect implements Serializable {

	private static final long serialVersionUID = 1L;
	public int left;
	public int top;
	public int right;
	public int bottom;
	
	public Rect() {
		left = top = right = bottom = 0;
	}
	
	public Rect(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public boolean isPointInside(int x, int y) {
		return x >= left && x <= right && y >= top && y <= bottom;
	}
	
	public int getWidth() {
		return right-left+1;
	}
	
	public int getHeight() {
		return bottom-top+1;
	}
	
	public Point getCenter() {
		return new Point((left+right)/2, (top+bottom)/2);
	}
	
}
