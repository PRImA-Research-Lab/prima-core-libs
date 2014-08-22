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

import java.io.Serializable;

/**
 * Base class for values of the class Variable.
 * There are also overloaded factory methods to generically create value objects.
 * 
 * @author Christian Clausner
 *
 */
public abstract class VariableValue implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public String toString();
	
	abstract public boolean equals(Object obj);

	/**
	 * Creates an IntergerValue object.
	 */
	public static VariableValue createValueObject(int value) {
		return new IntegerValue(value);
	}

	/**
	 * Creates a DoubleValue object.
	 */
	public static VariableValue createValueObject(double value) {
		return new DoubleValue(value);
	}

	/**
	 * Creates a BooleanValue object.
	 */
	public static VariableValue createValueObject(boolean value) {
		return new BooleanValue(value);
	}

	/**
	 * Creates a StringValue object.
	 */
	public static VariableValue createValueObject(String value) {
		if (value == null)
			return null;
		return new StringValue(value);
	}
	
	/**
	 * Returns the type ID of this variable value. 
	 */
	public abstract String getType();
	

}
