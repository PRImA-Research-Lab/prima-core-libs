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
package org.primaresearch.shared.variable;

/**
 * Value object for Variable holding an integer.
 *  
 * @author Christian Clausner
 *
 */
public class IntegerValue extends VariableValue implements Comparable<VariableValue> {

	private static final long serialVersionUID = 1L;
	
	public int val;

	/**
	 * Constructor with initialisation to 0
	 */
	public IntegerValue() {
		this.val = 0;
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public IntegerValue(int val) {
		this.val = val;
	}
	
	@Override
	public String toString() {
		return Integer.toString(val);
	}
	
	@Override
	public String getType() {
		return "Integer";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof IntegerValue)
			return this.val == ((IntegerValue)obj).val;
		if (obj instanceof Integer)
			return this.val == ((Integer)obj);
		return false;
	}

	
	@Override
	public int compareTo(VariableValue other) {
		if (other == null || !(other instanceof IntegerValue))
			return 0;
		return ((Integer)val).compareTo((Integer)((IntegerValue)other).val);
	}

}
