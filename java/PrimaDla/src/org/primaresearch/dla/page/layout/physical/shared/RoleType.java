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
package org.primaresearch.dla.page.layout.physical.shared;

import java.io.Serializable;

/**
 * Content types for region roles (table cell etc.).
 * 
 * @author Christian Clausner
 */
public class RoleType extends ContentType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Type for table cell roles */
	public static final RoleType TableCellRole = new RoleType("TableCellRole");

	/**
	 * Empty constructor (required for GWT)
	 */
	public RoleType() {
		super();
	}
	
	/**
	 * Constructor
	 * @param name Type name
	 */
	protected RoleType(String name) {
		super(name);
	}
}
