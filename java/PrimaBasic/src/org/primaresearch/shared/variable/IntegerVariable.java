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
package org.primaresearch.shared.variable;


/**
 * Variable implementation for integer values.
 * 
 * @author Christian Clausner
 *
 */
public class IntegerVariable extends BaseVariable implements Variable {

	private static final long serialVersionUID = 1L;

	private IntegerValue val;

	private Integer step = null;
	
	/**
	 * Only for GWT
	 */
	public IntegerVariable() {
		super();
	}
	
	/**
	 * Constructor with initialisation to 0
	 */
	public IntegerVariable(String name) {
		this(name, new IntegerValue());
	}

	/**
	 * Constructor with custom initialisation.
	 */
	public IntegerVariable(String name, IntegerValue initialValue) {
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
			this.val = (IntegerValue)getValueComplyingWithConstraint(value);
		} catch(ClassCastException exc) {
			throw new WrongVariableTypeException("Wrong variable type. Expected: Integer;  Actual: "+value.getType());
		}
	}
	
	@Override
	public void parseValue(String valueInTextForm) {
		try {
			this.setValue(new IntegerValue(new Integer(valueInTextForm)));
		} catch (NumberFormatException e) {
			this.val = new IntegerValue(0);
		} catch (WrongVariableTypeException e) {
			//Cannot happen 
			e.printStackTrace();
		}
	}

	@Override
	public Variable clone() {
		IntegerVariable var = new IntegerVariable(name);
		var.copyFrom(this);
		try {
			var.setValue(val == null ? null : new IntegerValue(val.val));
		} catch (WrongVariableTypeException e) {
			//Cannot happen 
			e.printStackTrace();
		}
		var.setStep(this.step);
		return var;
	}

	/**
	 * Returns the intended step width for this variable. 
	 * @return The step width or <code>null</code> if not defined.
	 */
	public Integer getStep() {
		return step;
	}

	/**
	 * Sets the intended step width for this variable. 
	 * @param step The step width or <code>null</code> for undefined.
	 */
	public void setStep(Integer step) {
		this.step = step;
	}

		
}
