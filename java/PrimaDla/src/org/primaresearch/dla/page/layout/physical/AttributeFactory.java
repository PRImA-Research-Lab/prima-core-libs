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
import org.primaresearch.shared.variable.VariableMap;

/**
 * Interface for factories that create attribute collections for page content objects.
 * Different types of content need different attributes.
 *  
 * @author Christian Clausner
 *
 */
public interface AttributeFactory {

	/**
	 * Returns a collection of attributes suitable for content objects of the given type.
	 * @return Map holding Variables
	 */
	public VariableMap createAttributes(ContentType type);
	
	
}
