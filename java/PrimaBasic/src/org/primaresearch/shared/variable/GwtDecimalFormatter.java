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

import com.google.gwt.i18n.client.NumberFormat;

/**
 * DoubleValueFormatter for client side applications.
 * Uses GWT NumberFormat.
 * 
 * @author Christian Clausner
 *
 */
public class GwtDecimalFormatter implements DoubleValueFormatter {

	private static final long serialVersionUID = 1L;
	private NumberFormat format;
	
	/**
	 * Constructor
	 * @param pattern The format pattern (see {@link com.google.gwt.i18n.client.NumberFormat NumberFormat} for syntax).
	 */
	public GwtDecimalFormatter(String pattern) {
		format = NumberFormat.getDecimalFormat();
	}
	
	@Override
	public String format(double value) {
		return format.format(value);
	}

}
