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
package org.primaresearch.shared.variable;

/**
 * Value object for Variable holding a boolean.
 *  
 * @author Christian Clausner
 *
 */
public class BooleanValue extends VariableValue {

	private static final long serialVersionUID = 1L;
	
	public boolean val;

	/**
	 * Constructor with initialisation to <code>false</code>
	 */
	public BooleanValue() {
		this.val = false;
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public BooleanValue(boolean val) {
		this.val = val;
	}
	
	@Override
	public String toString() {
		return val ? "true" : "false";
	}
	
	@Override
	public String getType() {
		return "Boolean";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof BooleanValue)
			return this.val == ((BooleanValue)obj).val;
		if (obj instanceof Boolean)
			return this.val == ((Boolean)obj);
		return false;
	}
}
