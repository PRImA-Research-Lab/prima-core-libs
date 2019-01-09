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
package org.primaresearch.variable;

import java.text.DecimalFormat;

import org.primaresearch.shared.variable.DoubleValueFormatter;

/**
 * DoubleValueFormatter for server side or stand-alone applications.
 * Uses Java DecimalFormat.
 * 
 * @author Christian Clausner
 *
 */
public class DecimalFormatter implements DoubleValueFormatter {

	private static final long serialVersionUID = 1L;
	private DecimalFormat format;
	
	/**
	 * Constructor
	 * @param pattern The format pattern (see {@link java.text.DecimalFormat DecimalFormat} for syntax).
	 */
	public DecimalFormatter(String pattern) {
		format = new DecimalFormat(pattern);
	}
	
	@Override
	public String format(double value) {
		return format.format(value);
	}

}
