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
package org.primaresearch.dla.page.layout.converter;

/**
 * Format conversion related message
 * 
 * @author Christian Clausner
 *
 */
public class ConversionMessage {

	public static final int CONVERSION_GENERAL				 	= 0;
	public static final int CONVERSION_RESET_INVALID_ATTRIBUTE 	= 1;
	public static final int CONVERSION_ADD_REQUIRED_REGION 		= 2;
	
	
	private String text;
	private int code;

	/**
	 * Constructor for general message
	 * @param text Message content
	 */
	public ConversionMessage(String text) {
		this(text, CONVERSION_GENERAL);
	}

	/**
	 * Constructor for specific message code
	 * @param text Message content
	 * @param code Message code (see <code>CONVERSION_...</code> constants)
	 */
	public ConversionMessage(String text, int code) {
		this.text = text;
	}
	
	/**
	 * Returns the message content
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Returns the message code (see <code>CONVERSION_...</code> constants)
	 */
	public int getCode() {
		return code;
	}

}
