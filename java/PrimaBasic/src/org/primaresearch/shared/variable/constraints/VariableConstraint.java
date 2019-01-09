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
package org.primaresearch.shared.variable.constraints;

import java.io.Serializable;

import org.primaresearch.shared.variable.VariableValue;


/**
 * Interface for value constraints a variable can have (such as allowed values for string variables).
 * 
 * @author Christian Clausner
 *
 */
public interface VariableConstraint extends Serializable {

	/**
	 * Checks if a given variable complies with this constraint. 
	 * @return true if the value is valid, false otherwise (and no exception is thrown)
	 * @throws IllegalArgumentException If the given value is invalid and the constraint is set up to throw this exception. 
	 */
	public boolean isValid(VariableValue value);
	
	/**
	 * Specifies if this constraint throws an {@link IllegalArgumentException} within the isValid() method.
	 */
	public boolean throwsIllegalArgumentException();
	
	/**
	 * Checks if the given value is valid and if not, tries to infer a valid value.
	 * This could be for instance a truncate operation for integer values that are out of bounds.
	 */
	public VariableValue getValidValue(VariableValue value);
	
	/**
	 * Creates a deep copy of this constraint.
	 */
	public VariableConstraint clone();
}
