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
package org.primaresearch.io;

import java.util.Map;

import org.primaresearch.shared.variable.VariableMap;

/**
 * Interface for data models. An example is the SchemaParser for the PAGE XML schema.
 * @author Christian Clausner
 *
 */
public interface FormatModel {

	public Map<String, VariableMap> getTypeAttributeTemplates();
	
	/**
	 * Filters the given attributes and returns only the ones for the specified type filter. 
	 * @param typeFilter E.g. 'TextStyleType'
	 * @return Variable map with attributes
	 */
	public VariableMap filterAttributes(VariableMap allAttributes, String typeFilter);
	
	/**
	 * Returns the version of this model
	 * @return Version object
	 */
	public FormatVersion getVersion();
}
