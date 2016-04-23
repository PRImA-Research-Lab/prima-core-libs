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

import org.primaresearch.shared.variable.VariableValue;

/**
 * Constraint for ordered variable types (such as Interger and Double).
 * Defines a minimum and maximum value.
 * 
 * @author Christian Clausner
 *
 */
public class MinMaxConstraint implements VariableConstraint, Serializable {

	private static final long serialVersionUID = 1L;
	VariableValue min;
	VariableValue max;
	
	public MinMaxConstraint(VariableValue min, VariableValue max) {
		if (!(min instanceof Comparable<?>) || !(max instanceof Comparable<?>))
			throw new IllegalArgumentException("MinMaxConstraint needs Comparable bounds.");
		this.min = min;
		this.max = max;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isValid(VariableValue value) {
		if (value == null)
			return false;
		if (!(value instanceof Comparable<?>))
			return false;
		return ((Comparable<VariableValue>)min).compareTo(value) <= 0 && ((Comparable<VariableValue>)max).compareTo(value) >= 0;
	}

	@Override
	public boolean throwsIllegalArgumentException() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VariableValue getValidValue(VariableValue value) {
		if (value == null)
			return min;
		if (!(value instanceof Comparable<?>))
			return min;
		if (((Comparable<VariableValue>)min).compareTo(value) > 0)
			return min;
		if (((Comparable<VariableValue>)max).compareTo(value) < 0)
			return max;
		return value;
	}

	public VariableConstraint clone() {
		MinMaxConstraint copy = new MinMaxConstraint(min, max);
		return copy;
	}

	public VariableValue getMin() {
		return min;
	}

	public VariableValue getMax() {
		return max;
	}

}
