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
package org.primaresearch.shared.variable.constraints;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableValue;

/**
 * Implementation of a variable constraint for string enumerations.
 * 
 * @author Christian Clausner
 *
 */
public class ValidStringValues implements VariableConstraint, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	Collection<String> validValues = new LinkedList<String>();
	
	public void addValidValue(String value) {
		validValues.add(value);
	}

	@Override
	public boolean isValid(VariableValue value) {
		if (value == null)
			return true;
		if (!(value instanceof StringValue))
			throw new IllegalArgumentException("Variable value is not a string");
			//return false;
		if (!validValues.contains(((StringValue)value).val))
			throw new IllegalArgumentException("Variable value '"+((StringValue)value).val+"' is in the list of valid values.");
		return true;
	}

	@Override
	public boolean throwsIllegalArgumentException() {
		return true;
	}

	@Override
	public VariableValue getValidValue(VariableValue value) {
		try {
			if (isValid(value))
				return value;
		} catch(IllegalArgumentException exc) {
			//Can be ignored here
		}
		return null;
	}

	public Collection<String> getValidValues() {
		return validValues;
	}
	
	public VariableConstraint clone() {
		ValidStringValues copy = new ValidStringValues();
		copy.validValues.addAll(validValues);
		return copy;
	}
}
