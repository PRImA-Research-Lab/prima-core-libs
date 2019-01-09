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
 * Value object for Variable holding a double.
 *  
 * @author Christian Clausner
 *
 */
public class DoubleValue extends VariableValue implements Comparable<VariableValue> {

	private static final long serialVersionUID = 1L;
	
	//CC 02.11.2012 - Had to remove DecimalFormat since it is not supported by GWT
	//public static final NumberFormat format = NumberFormat.getFormat("#.######");
	/** Default pattern to be used by formatters. */
	public static final String defaultFormatPattern = "#.######";
	
	/** Formatter to be used for converting a double value to a string. */
	private static DoubleValueFormatter formatter = null; 
	
	public double val;
	
	/**
	 * Sets the formatter that is to be used to convert a double value to a string. 
	 */
	public static void setFormatter(DoubleValueFormatter formatter) {
		DoubleValue.formatter = formatter;
	}

	/**
	 * Constructor with initialisation to 0.0
	 */
	public DoubleValue() {
		this.val = 0.0;
	}
	
	/**
	 * Constructor with custom initialisation.
	 */
	public DoubleValue(double val) {
		this.val = val;
	}
	
	/**
	 * Returns a formatted representation of the double value of this value object.<br>
	 * Use {@link #setFormatter(DoubleValueFormatter) setFormatter} to customise the format.<br>
	 * By default {@link Double#toString(double) Double.toString} is used.
	 */
	public String toString() {
		if (formatter != null)
			return formatter.format(val);
		return Double.toString(val);
	}
	
	@Override
	public String getType() {
		return "Double";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof DoubleValue)
			return this.val == ((DoubleValue)obj).val;
		if (obj instanceof Double)
			return this.val == ((Double)obj);
		return false;
	}
	
	@Override
	public int compareTo(VariableValue other) {
		if (other == null || !(other instanceof DoubleValue))
			return 0;
		return ((Double)val).compareTo((Double)((DoubleValue)other).val);
	}

}
