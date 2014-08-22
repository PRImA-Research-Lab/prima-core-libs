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
package org.primaresearch.dla.page.layout.physical.text.impl;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextObject;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Specialised low level text object representing glyphs within a page layout.
 * 
 * @author Christian Clausner
 *
 */
public class Glyph extends LowLevelTextObject {

	protected Glyph(IdRegister idRegister, Id id, Polygon coords, VariableMap attributes, 
					LowLevelTextContainer parentWord) {
		super(idRegister, id, coords, attributes, parentWord);
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.Glyph;
	}

	@Override
	public Boolean isBold() {
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
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_bold).getValue()).val;
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
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_italic).getValue()).val;
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
	public Boolean isSubscript() {
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_underlined).getValue()).val;
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
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_subscript).getValue()).val;
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
		return ((BooleanValue)getAttributes().get(DefaultXmlNames.ATTR_superscript).getValue()).val;
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
		return false;
	}
	
	@Override
	public void setLetterSpaced(Boolean letterSpaced) {
		//Glyph cannot be letter spaced
	}
}
