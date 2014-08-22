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
 * Value object for Variable holding a string.
 *  
 * @author Christian Clausner
 *
 */
public class StringValue extends VariableValue {

	private static final long serialVersionUID = 1L;
	
	public String val;
	
	/**
	 * Constructor with initialisation to empty string.
	 */
	public StringValue() {
		this.val = "";
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public StringValue(String val) {
		this.val = val;
	}
	
	@Override
	public String toString() {
		return val != null ? val : "";
	}

	@Override
	public String getType() {
		return "String";
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof StringValue)
			return this.val == ((StringValue)obj).val;
		if (obj instanceof String)
			return ((String)obj).equals(this.val);
		return false;
	}
}
