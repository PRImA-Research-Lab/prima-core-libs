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

import org.primaresearch.dla.page.layout.physical.ContentObject;
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

	private String textConentUnicode;
	private String textContentPlain;

	private Double ocrConfidence = null;

	private VariableMap attributes;
	//private VariableMap textStyle;
	
	private LowLevelTextContainer parent;
	
	
	protected LowLevelTextObject(IdRegister idRegister, Id id, Polygon coords, 
								VariableMap attributes, LowLevelTextContainer parent) {
		this.idRegister = idRegister;
		this.id = id;
		this.coords = coords;
		this.attributes = attributes;
		//this.textStyle = textStyle;
		this.parent = parent;
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


	@Override
	public Double getConfidence() {
		return ocrConfidence;
	}

	@Override
	public void setConfidence(Double confidence) {
		this.ocrConfidence = confidence;
	}

	public LowLevelTextContainer getParent() {
		return parent;
	}

	public void setParent(LowLevelTextContainer parent) {
		this.parent = parent;
	}
	
	//@Override
	//public VariableMap getTextStyle() {
	//	return textStyle;
	//}
	
	
}
