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
 * Variable implementation for double values.
 * 
 * @author Christian Clausner
 *
 */
public class DoubleVariable extends BaseVariable implements Variable {
	
	private static final long serialVersionUID = 1L;

	private DoubleValue val;
	
	private Double step = null;
	
	/**
	 * Only for GWT
	 */
	public DoubleVariable() {
		super();
	}
	
	/**
	 * Constructor with initialisation to 0.0
	 */
	public DoubleVariable(String name) {
		this(name, new DoubleValue());
	}

	/**
	 * Constructor with custom initialisation.
	 */
	public DoubleVariable(String name, DoubleValue initialValue) {
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
			this.val = (DoubleValue)getValueComplyingWithConstraint(value);
		} catch(ClassCastException exc) {
			throw new WrongVariableTypeException("Wrong variable type. Expected: Double;  Actual: "+value.getType());
		}
	}
	
	@Override
	public void parseValue(String valueInTextForm) {
		try {
			this.setValue(new DoubleValue(Double.valueOf(valueInTextForm)));
		} catch (NumberFormatException e) {
			this.val = new DoubleValue(0.0);
		} catch (WrongVariableTypeException e) {
			//Cannot happen
			e.printStackTrace();
		}
	}

	@Override
	public Variable clone() {
		DoubleVariable var = new DoubleVariable(name);
		var.copyFrom(this);
		try {
			var.setValue(val == null ? null : new DoubleValue(val.val));
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
	public Double getStep() {
		return step;
	}

	/**
	 * Sets the intended step width for this variable. 
	 * @param step The step width or <code>null</code> for undefined.
	 */
	public void setStep(Double step) {
		this.step = step;
	}

	
}
