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
package org.primaresearch.io.xml.variable;

/**
 * Contains hard-coded XML element and attribute names for writing variables.
 * 
 * @author Christian Clausner
 *
 */
public interface VariableXmlNames {

	public static final String ELEMENT_Parameters = "Parameters";
	public static final String ELEMENT_Parameter = "Parameter";
	public static final String ELEMENT_Description = "Description";
	public static final String ELEMENT_ValidValues = "ValidValues";
	public static final String ELEMENT_Value = "Value";

	public static final String ATTR_id = "id";
	public static final String ATTR_type = "type";
	public static final String ATTR_name = "name";
	public static final String ATTR_caption = "caption";
	public static final String ATTR_sortIndex = "sortIndex";
	public static final String ATTR_readOnly = "readOnly";
	public static final String ATTR_isSet = "isSet";
	public static final String ATTR_version = "version";
	public static final String ATTR_visible = "visible";
	public static final String ATTR_value = "value";
	public static final String ATTR_min = "min";
	public static final String ATTR_max = "max";
	public static final String ATTR_step = "step";
	public static final String ATTR_textType = "textType";
}
