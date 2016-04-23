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

import java.util.Collection;

import org.primaresearch.shared.variable.constraints.ValidStringValues;

/**
 * Variable implementation for string values.
 * 
 * @author Christian Clausner
 *
 */
public class StringVariable extends BaseVariable implements Variable {
	
	public static final int TEXTTYPE_MULTILINE 	= 0;
	public static final int TEXTTYPE_SINGLELINE = 1;
	public static final int TEXTTYPE_LIST 		= 2;
	
	private static final long serialVersionUID = 1L;

	private StringValue val;
	
	private int textType = 0;
	
	@SuppressWarnings("unused")
	private StringVariable() {
		super();
	}
	
	/**
	 * Constructor with initialisation to empty string.
	 */
	public StringVariable(String name) {
		this(name, new StringValue());
	}

	/**
	 * Constructor with custom initialisation.
	 */
	public StringVariable(String name, StringValue initialValue) {
		super(name);
		val = initialValue;
	}
	
	@Override
	public VariableValue getValue() {
		return val;
	}

	@Override
	public void setValue(VariableValue value)  throws WrongVariableTypeException, IllegalArgumentException {
		checkValueAgainstConstraint(value);
		try {
			this.val = (StringValue)getValueComplyingWithConstraint(value);
		} catch(ClassCastException exc) {
			throw new WrongVariableTypeException("Wrong variable type. Expected: String;  Actual: "+value.getType());
		}
	}
	
	/**
	 * Returns the recommendation for how to present this variable in a graphical user interface.
	 * @return Text type ID (see TEXTTYPE_ constants)
	 */
	public int getTextType() {
		return textType;
	}

	/**
	 * Sets the recommendation for how to present this variable in a graphical user interface.
	 * @param textType Text type ID (see TEXTTYPE_ constants)
	 */
	public void setTextType(int textType) {
		this.textType = textType;
	}

	@Override
	public void parseValue(String valueInTextForm) {
		try {
			this.setValue(new StringValue(valueInTextForm));
		} catch (WrongVariableTypeException e) {
			//Cannot happen
			e.printStackTrace();
		}
	}
	
	@Override
	public Variable clone() {
		StringVariable var = new StringVariable(name);
		var.copyFrom(this);
		try {
			var.setValue(val == null ? null : new StringValue(val.val));
		} catch (WrongVariableTypeException e) {
			//Cannot happen
			e.printStackTrace();
		}
		return var;
	}
	
	/**
	 * Returns a set of valid values for this string variable.
	 * @return A set of Strings or null if no constraint has been set for this variable. 
	 */
	public Collection<String> getValidValues() {
		if (this.constraint == null || !(this.constraint instanceof ValidStringValues))
			return null;
		return ((ValidStringValues)this.constraint).getValidValues();
	}
}
