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
package org.primaresearch.dla.page.metadata;

import java.io.Serializable;

import org.primaresearch.dla.page.layout.physical.AttributeContainer;
import org.primaresearch.labels.HasLabels;

public interface MetadataItem extends AttributeContainer, HasLabels, Serializable {

	/**
	 * One of  author, imageProperties, processingStep, other
	 */
	public String getType();
	
	/**
	 * Name of item
	 */
	public String getName();
	
	/**
	 * Value of item
	 */
	public String getValue();

	/**
	 * Date of item (e.g. 2002-05-30T09:00:00)
	 */
	public String getDate();

	/**
	 * One of  author, imageProperties, processingStep, other
	 */
	public void setType(String type);
	
	/**
	 * Name of item
	 */
	public void setName(String name);
	
	/**
	 * Value of item
	 */
	public void setValue(String value);
	
	/**
	 * Date of item (e.g. 2002-05-30T09:00:00)
	 */
	public void setDate(String date);
	
}
