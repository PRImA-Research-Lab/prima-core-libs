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
package org.primaresearch.dla.page.layout.physical;

import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.ident.Identifiable;

/**
 * Represents physical page content objects (e.g. region, text line). 
 * 
 * @author Christian Clausner
 *
 */
public interface ContentObject extends AttributeContainer, Identifiable, GeometricObject {

	/**
	 * Suffix used for IDs of temporary content objects 
	 */
	public static final String TEMP_ID_SUFFIX = "357564684568544579089";
	
	/**
	 * Returns the type of this page content object 
	 */
	public ContentType getType();

	/**
	 * Checks if this is a temporary content object
	 * @return <code>true</code> if temporary, <code>false</code> if normal content object
	 */
	public boolean isTemporary();
}
