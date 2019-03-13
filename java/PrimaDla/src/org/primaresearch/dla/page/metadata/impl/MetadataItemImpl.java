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
package org.primaresearch.dla.page.metadata.impl;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.dla.page.metadata.MetadataItem;
import org.primaresearch.labels.Labels;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Implementation of metadata item (additional metadata)
 * 
 * @author Christian Clausner
 *
 */
public class MetadataItemImpl implements MetadataItem {

	private static final long serialVersionUID = 1L;
	private VariableMap attributes;
	private Labels labels;

	/**
	 * Constructor
	 * @param attrFactory
	 */
	public MetadataItemImpl(AttributeFactory attrFactory) {
		attributes = attrFactory.createAttributes(ContentType.MetadataItem);
	}
	
	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	@Override
	public String getType() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_type).getValue()).val;
	}

	@Override
	public String getName() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_name).getValue()).val;
	}

	@Override
	public String getValue() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_value).getValue()).val;
	}

	@Override
	public void setType(String type) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_type).setValue(VariableValue.createValueObject(type));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setName(String name) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_name).setValue(VariableValue.createValueObject(name));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(String value) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_value).setValue(VariableValue.createValueObject(value));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Labels getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Labels labels) {
		this.labels = labels;		
	}

	@Override
	public String getDate() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_date).getValue()).val;
	}

	@Override
	public void setDate(String date) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_date).setValue(VariableValue.createValueObject(date));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}



}
