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
package org.primaresearch.dla.page.layout.physical.text;

import org.primaresearch.dla.page.layout.physical.AttributeContainer;

public interface TextContent extends AttributeContainer {
	
	/**
	 * Returns the Unicode text. 
	 */
	public String getText();
	
	/**
	 * Returns the plain text.
	 */
	public String getPlainText();
	
	/**
	 * Sets the Unicode text.
	 */
	public void setText(String text);
	
	/**
	 * Sets the plain text.
	 */
	public void setPlainText(String text);
	
	/**
	 * Returns the OCR confidence for the text content.
	 */
	public Double getConfidence();
	
	/**
	 * Sets the OCR confidence for the text content.
	 */
	public void setConfidence(Double confidence);
	
	/**
	 * Returns comments for this text content variant. 
	 */
	public String getComments();

	/**
	 * Sets comments for this text content variant.
	 */
	public void setComments(String comments);

	/**
	 * Returns the intended or observed data type for this text content variant.
	 * For example 'xsd:decimal'
	 */
	public String getDataType();

	/**
	 * Sets the intended or observed data type for this text content variant.
	 * For example 'xsd:decimal'
	 */
	public void setDataType(String datatype);
	
	/**
	 * Returns details on the intended or observed data type for this text content variant.
	 * For example a regular expression
	 */
	public String getDataTypeDetails();

	/**
	 * Sets details on the intended or observed data type for this text content variant.
	 * For example a regular expression
	 */
	public void setDataTypeDetails(String details);
	
	/**
	 * Returns the rule that is to be applied when merging this text content with the next (e.g. paragraph).
	 * One of: no-whitespace, add-space, add-tab, add-line-break, add-custom, remove-last, remove-multiple, remove-all
	 */
	//public String getMergeWithNextRule();

	/**
	 * Sets the rule that is to be applied when merging this text content with the next (e.g. paragraph).
	 * One of: no-whitespace, add-space, add-tab, add-line-break, add-custom, remove-last, remove-multiple, remove-all or <code>null</code>
	 */
	//public void setMergeWithNextRule(String rule);
	
	/**
	 * Returns data for a custom 'merge with next' rule.
	 * For example the number of characters to be removed or a custom string to be added when merging texts.
	 */
	//public String getMergeWithNextRuleData();

	/**
	 * Sets data for a custom 'merge with next' rule.
	 * For example the number of characters to be removed or a custom string to be added when merging texts.
	 */
	//public void setMergeWithNextRuleData(String data);

}
