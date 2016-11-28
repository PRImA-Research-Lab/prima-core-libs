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
package org.primaresearch.dla.page.layout.physical.text;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.text.impl.TextContentVariants;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.VariableMap;

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
	
	private LowLevelTextContainer parent;
	
	private TextContentVariants textContentVariants;

	
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

}
