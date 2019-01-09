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
package org.primaresearch.shared;

import java.io.Serializable;


/**
 * Simple pair of objects (serializable for usage with GWT)
 * @author Christian Clausner
 *
 * @param <Left> Serializable object
 * @param <Right> Serializable object
 */
public class Pair<Left extends Serializable, Right extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;
	public Left left;
	public Right right;

	public Pair() {
	}
	
	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}
	
}
