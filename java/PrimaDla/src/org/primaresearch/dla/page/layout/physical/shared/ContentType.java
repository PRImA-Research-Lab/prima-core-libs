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
package org.primaresearch.dla.page.layout.physical.shared;

import java.io.Serializable;

/**
 * Base class for different types of page content.
 * 
 * @author Christian Clausner
 *
 */
public class ContentType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Content type for a document page itself
	 */
	public static final ContentType Page = new ContentType("Page");

	/**
	 * Content type for the page border
	 */
	public static final ContentType Border = new ContentType("Border");

	/**
	 * Content type for the page print space
	 */
	public static final ContentType PrintSpace = new ContentType("PrintSpace");

	private String name;
	
	/**
	 * Empty constructor (required for GWT)
	 */
	protected ContentType() {
		name = null;
	}
	
	/**
	 * Constructor
	 * @param name Type name
	 */
	protected ContentType(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Initialises this type
	 * @param name Type name
	 */
	public void init(String name) {
		this.name = name;
	}
	
	public boolean equals(ContentType other) {
		if (other == null)
			return false;
		return name.equals(other.name);
	}

}
