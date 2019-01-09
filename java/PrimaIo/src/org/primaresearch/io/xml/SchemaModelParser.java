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
package org.primaresearch.io.xml;

import java.net.URL;
import java.util.Map;

import org.primaresearch.io.FormatModel;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Interface for XML schema parsers that extract type and attribute information from schema files.
 * 
 * @author Christian Clausner
 *
 */
public interface SchemaModelParser extends FormatModel {

	/**
	 * Parses the schema located at the given URL 
	 * @param schemaLocation Schema file to parse
	 */
	public void parse(URL schemaLocation);
	
	/**
	 * Returns attribute templates for all types that were found in the schema
	 * @return Map [type name, attribute templates]
	 */
	public Map<String, VariableMap> getTypeAttributeTemplates();
}
