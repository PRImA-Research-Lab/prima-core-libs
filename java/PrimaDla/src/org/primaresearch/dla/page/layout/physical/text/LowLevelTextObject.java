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

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.AlternativeImage;
import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.text.impl.TextContentVariants;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.labels.Labels;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Abstract class representing low level text objects such as text line, word and glyph.
 * 
 * @author Christian Clausner
 *
 */
abstract public class LowLevelTextObject implements TextObject, ContentObject {
	
	private Id id;
	private IdRegister idRegister;
	
	private Polygon coords;

	private VariableMap attributes;
	
	private VariableMap userDefinedAttributes = null;

	private LowLevelTextContainer parent;
	
	private TextContentVariants textContentVariants;

	transient private Labels labels;
	
	transient private List<AlternativeImage> alternativeImages;

	
	protected LowLevelTextObject(IdRegister idRegister, Id id, Polygon coords, 
								VariableMap attributes, LowLevelTextContainer parent,
								AttributeFactory attrFactory) {
		this.idRegister = idRegister;
		this.id = id;
		this.coords = coords;
		this.attributes = attributes;
		//this.textStyle = textStyle;
		this.parent = parent;
		textContentVariants = new TextContentVariants(attrFactory);
	}

	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	@Override
	public Polygon getCoords() {
		return coords;
	}

	@Override
	public void setCoords(Polygon coords) {
		this.coords = coords;
	}

	@Override
	public Id getId() {
		return id;
	}
	
	@Override
	public IdRegister getIdRegister() {
		return idRegister;
	}
	
	@Override
	public void setId(String id) throws InvalidIdException {
		this.id = idRegister.registerId(id, this.id);
	}
	
	@Override
	public void setId(Id id) throws InvalidIdException {
		idRegister.registerId(id, this.id);
		this.id = id;
	}

	@Override
	public boolean isTemporary() {
		return this.getId().toString().equals(TEMP_ID_SUFFIX);
	}



	public LowLevelTextContainer getParent() {
		return parent;
	}

	public void setParent(LowLevelTextContainer parent) {
		this.parent = parent;
	}
	
	@Override
	public String getText() {
		return textContentVariants.getText();
	}

	@Override
	public String getPlainText() {
		return textContentVariants.getPlainText();
	}
	
	@Override
	public void setText(String text) {
		textContentVariants.setText(text);
	}
	
	@Override
	public void setPlainText(String text) {
		textContentVariants.setPlainText(text);
	}
	
	@Override
	public String getComments() {
		return textContentVariants.getComments();
	}

	@Override
	public void setComments(String comments) {
		textContentVariants.setComments(comments);
	}

	@Override
	public String getDataType() {
		return textContentVariants.getDataType();
	}

	@Override
	public void setDataType(String datatype) {
		textContentVariants.setDataType(datatype);
	}

	@Override
	public String getDataTypeDetails() {
		return textContentVariants.getDataTypeDetails();
	}

	@Override
	public void setDataTypeDetails(String details) {
		textContentVariants.setDataTypeDetails(details);
	}
	
	/*@Override
	public String getMergeWithNextRule() {
		return textContentVariants.getMergeWithNextRule();
	}

	@Override
	public void setMergeWithNextRule(String rule) {
		textContentVariants.setMergeWithNextRule(rule);		
	}

	@Override
	public String getMergeWithNextRuleData() {
		return textContentVariants.getMergeWithNextRuleData();
	}

	@Override
	public void setMergeWithNextRuleData(String data) {
		textContentVariants.setMergeWithNextRuleData(data);		
	}*/


	@Override
	public int getTextContentVariantCount() {
		return textContentVariants.getTextContentVariantCount();
	}

	@Override
	public TextContent getTextContentVariant(int index) {
		return textContentVariants.getTextContentVariant(index);
	}

	@Override
	public TextContent addTextContentVariant() {
		return textContentVariants.addTextContentVariant();
	}

	@Override
	public void reomveTextContentVariant(int index) {
		textContentVariants.reomveTextContentVariant(index);
	}
	
	@Override
	public Double getConfidence() {
		return textContentVariants.getConfidence();
	}

	@Override
	public void setConfidence(Double confidence) {
		textContentVariants.setConfidence(confidence);
	}

	/**
	 * User-defined attributes (text, int, decimal or boolean)
	 * @param createIfNotExists Set to true if to create an empty variable map if none exists yet.
	 * @return Variable map or <code>null</code>
	 */
	public VariableMap getUserDefinedAttributes(boolean createIfNotExists) {
		if (userDefinedAttributes == null && createIfNotExists)
			userDefinedAttributes = new VariableMap();
		return userDefinedAttributes;
	}
	
	/**
	 *  User-defined attributes (text, int, decimal or boolean)
	 * @param attrs Variable map
	 */
	public void setUserDefinedAttributes(VariableMap attrs) {
		userDefinedAttributes = attrs;
	}


	@Override
	public Labels getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Labels labels) {
		this.labels = labels;		
	}
	
	/**
	 * Returns a list of alternative images that are associated with this region
	 * @return List with image objects
	 */
	public List<AlternativeImage> getAlternativeImages() {
		if (alternativeImages == null)
			alternativeImages = new ArrayList<AlternativeImage>();
		return alternativeImages;
	}
	
	@Override
	public Boolean isBold() {
		if (getAttributes().get(DefaultXmlNames.ATTR_bold) == null || getAttributes().get(DefaultXmlNames.ATTR_bold).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_bold).getValue()).val;
	}
	
	@Override
	public void setBold(Boolean bold) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_bold).setValue(VariableValue.createValueObject(bold));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isItalic() {
		if (getAttributes().get(DefaultXmlNames.ATTR_italic) == null || getAttributes().get(DefaultXmlNames.ATTR_italic).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_italic).getValue()).val;
	}
	
	@Override
	public void setItalic(Boolean italic) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_italic).setValue(VariableValue.createValueObject(italic));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isUnderlined() {
		if (getAttributes().get(DefaultXmlNames.ATTR_underlined) == null || getAttributes().get(DefaultXmlNames.ATTR_underlined).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_underlined).getValue()).val;
	}
	
	@Override
	public void setUnderlined(Boolean underlined) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_underlined).setValue(VariableValue.createValueObject(underlined));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getUnderlineStyle() {
		if (getAttributes().get(DefaultXmlNames.ATTR_underlineStyle) == null || getAttributes().get(DefaultXmlNames.ATTR_underlineStyle).getValue() == null)
			return null;
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_underlineStyle).getValue()).val;
	}
	
	@Override
	public void setUnderlineStyle(String style) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_underlineStyle).setValue(VariableValue.createValueObject(style));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isSubscript() {
		if (getAttributes().get(DefaultXmlNames.ATTR_subscript) == null || getAttributes().get(DefaultXmlNames.ATTR_subscript).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_subscript).getValue()).val;
	}
	
	@Override
	public void setSubscript(Boolean subscript) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_subscript).setValue(VariableValue.createValueObject(subscript));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isSuperscript() {
		if (getAttributes().get(DefaultXmlNames.ATTR_superscript) == null || getAttributes().get(DefaultXmlNames.ATTR_superscript).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_superscript).getValue()).val;
	}
	
	@Override
	public void setSuperscript(Boolean superscript) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_superscript).setValue(VariableValue.createValueObject(superscript));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isStrikethrough() {
		if (getAttributes().get(DefaultXmlNames.ATTR_strikethrough) == null || getAttributes().get(DefaultXmlNames.ATTR_strikethrough).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_strikethrough).getValue()).val;
	}
	
	@Override
	public void setStrikethrough(Boolean strikethrough) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_strikethrough).setValue(VariableValue.createValueObject(strikethrough));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isSmallCaps() {
		if (getAttributes().get(DefaultXmlNames.ATTR_smallCaps) == null || getAttributes().get(DefaultXmlNames.ATTR_smallCaps).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_smallCaps).getValue()).val;
	}
	
	@Override
	public void setSmallCaps(Boolean smallCaps) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_smallCaps).setValue(VariableValue.createValueObject(smallCaps));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isLetterSpaced() {
		if (getAttributes().get(DefaultXmlNames.ATTR_letterSpaced) == null || getAttributes().get(DefaultXmlNames.ATTR_letterSpaced).getValue() == null)
			return null;
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_letterSpaced).getValue()).val;
	}
	
	@Override
	public void setLetterSpaced(Boolean letterSpaced) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_letterSpaced).setValue(VariableValue.createValueObject(letterSpaced));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}
}
