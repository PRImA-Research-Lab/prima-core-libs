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
 * Variable implementation for boolean values.
 * 
 * @author Christian Clausner
 *
 */
public class BooleanVariable extends BaseVariable implements Variable {

	private static final long serialVersionUID = 1L;
	
	private BooleanValue val;

	@SuppressWarnings("unused")
	private BooleanVariable() {
		super();
	}

	/**
	 * Constructor with initialisation to <code>false</code>.
	 */
	public BooleanVariable(String name) {
		this(name, new BooleanValue());
	}

	/**
	 * Constructor with custom initialisation.
	 */
	public BooleanVariable(String name, BooleanValue initialValue) {
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
			this.val = (BooleanValue)getValueComplyingWithConstraint(value);
		} catch(ClassCastException exc) {
			throw new WrongVariableTypeException("Wrong variable type. Expected: Boolean;  Actual: "+value.getType());
		}
	}

	@Override
	public void parseValue(String valueInTextForm) {
		try {
			this.setValue(new BooleanValue(new Boolean(valueInTextForm)));
		} catch (WrongVariableTypeException e) {
			//Cannot happen
			e.printStackTrace();
		}
	}

	@Override
	public Variable clone() {
		BooleanVariable var = new BooleanVariable(name);
		var.copyFrom(this);
		try {
			var.setValue(val == null ? null : new BooleanValue(val.val));
		} catch (WrongVariableTypeException e) {
			//Cannot happen
			e.printStackTrace();
		}
		return var;
	}


}
