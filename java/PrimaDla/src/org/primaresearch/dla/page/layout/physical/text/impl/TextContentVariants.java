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
package org.primaresearch.dla.page.layout.physical.text.impl;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.text.TextContent;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;

/**
 * Container holding multiple text content version / variants
 * Also implements the <code>TextContent</code> interface by accessing the first (main) text content variant.
 * 
 * @author Christian Clausner
 *
 */
public class TextContentVariants implements TextContent {

	private AttributeFactory attrFactory;
	private List<TextContent> textContentVariants = new ArrayList<TextContent>();
	
	/**
	 * Constructor
	 * @param attrFactory Attribute factory implementation that can create the attributes for a text content object
	 */
	public TextContentVariants(AttributeFactory attrFactory) {
		this.attrFactory = attrFactory;
		
		//Create initial text content (there is always one text content)
		addTextContentVariant();
	}
	
	/**
	 * Returns the number of text content variants.
	 * @return Count >= 1
	 */
	public int getTextContentVariantCount() {
		return textContentVariants.size();
	}
	
	/**
	 * Returns the text content variant for the given index
	 * @return Text content object
	 */
	public TextContent getTextContentVariant(int index) {
		return textContentVariants.get(index);
	}
	
	/**
	 * Adds a new text content variant
	 * @return The new text content object
	 */
	public TextContent addTextContentVariant() {
		textContentVariants.add(new TextContentVariant(attrFactory));
		return textContentVariants.get(textContentVariants.size() - 1);
	}
	
	/**
	 * Removes the text content variant at the given index, if there are two or more variants.
	 * One text content variant has to exist at all times.	 
	 */
	public void reomveTextContentVariant(int index) {
		if (textContentVariants.size() > 1)
			textContentVariants.remove(index);
	}

	@Override
	public String getText() {
		return textContentVariants.get(0).getText();
	}

	@Override
	public String getPlainText() {
		return textContentVariants.get(0).getPlainText();
	}

	@Override
	public void setText(String text) {
		textContentVariants.get(0).setText(text);
	}

	@Override
	public void setPlainText(String text) {
		textContentVariants.get(0).setPlainText(text);
	}

	@Override
	public Double getConfidence() {
		return textContentVariants.get(0).getConfidence();
	}

	@Override
	public void setConfidence(Double confidence) {
		textContentVariants.get(0).setConfidence(confidence);
	}

	@Override
	public String getComments() {
		return textContentVariants.get(0).getComments();
	}

	@Override
	public void setComments(String comments) {
		textContentVariants.get(0).setComments(comments);
	}

	@Override
	public String getDataType() {
		return textContentVariants.get(0).getDataType();
	}

	@Override
	public void setDataType(String datatype) {
		textContentVariants.get(0).setDataType(datatype);
	}

	@Override
	public String getDataTypeDetails() {
		return textContentVariants.get(0).getDataTypeDetails();
	}

	@Override
	public void setDataTypeDetails(String details) {
		textContentVariants.get(0).setDataTypeDetails(details);
	}
	
	@Override
	public VariableMap getAttributes() {
		return textContentVariants.get(0).getAttributes();
	}

	
	/**
	 * TextContent implementation representing one version / variant of a page layout object's text content.
	 * 
	 * @author Christian Clausner
	 *
	 */
	public static class TextContentVariant implements TextContent {

		private VariableMap attributes;
		private String textConentUnicode;
		private String textContentPlain;
		
		/**
		 * Constructor. Creates default attributes for text content objects
		 */
		public TextContentVariant(AttributeFactory attrFactory) {
			if (attrFactory != null)
				attributes = attrFactory.createAttributes(this);
		}

		@Override
		public VariableMap getAttributes() {
			return attributes;
		}

		@Override
		public String getText() {
			return textConentUnicode;
		}

		@Override
		public String getPlainText() {
			return textContentPlain;
		}
		
		@Override
		public void setText(String text) {
			textConentUnicode = text;
		}
		
		@Override
		public void setPlainText(String text) {
			textContentPlain = text;
		}

		@Override
		public Double getConfidence() {
			if (getAttributes().get(DefaultXmlNames.ATTR_conf).getValue() == null)
				return null;
			return ((DoubleValue)getAttributes().get(DefaultXmlNames.ATTR_conf).getValue()).val;
		}

		@Override
		public void setConfidence(Double confidence) {
			try {
				getAttributes().get(DefaultXmlNames.ATTR_conf).setValue(VariableValue.createValueObject(confidence));
			} catch (WrongVariableTypeException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getComments() {
			if (getAttributes().get(DefaultXmlNames.ATTR_comments).getValue() == null)
				return null;
			return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_comments).getValue()).val;
		}

		@Override
		public void setComments(String comments) {
			try {
				getAttributes().get(DefaultXmlNames.ATTR_comments).setValue(VariableValue.createValueObject(comments));
			} catch (WrongVariableTypeException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getDataType() {
			if (getAttributes().get(DefaultXmlNames.ATTR_dataType) == null || getAttributes().get(DefaultXmlNames.ATTR_dataType).getValue() == null)
				return null;
			return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_dataType).getValue()).val;
		}

		@Override
		public void setDataType(String datatype) {
			try {
				getAttributes().get(DefaultXmlNames.ATTR_dataType).setValue(VariableValue.createValueObject(datatype));
			} catch (WrongVariableTypeException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getDataTypeDetails() {
			if (getAttributes().get(DefaultXmlNames.ATTR_dataTypeDetails).getValue() == null)
				return null;
			return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_dataTypeDetails).getValue()).val;
		}

		@Override
		public void setDataTypeDetails(String details) {
			try {
				getAttributes().get(DefaultXmlNames.ATTR_dataTypeDetails).setValue(VariableValue.createValueObject(details));
			} catch (WrongVariableTypeException e) {
				e.printStackTrace();
			}
		}

		
	}




}
