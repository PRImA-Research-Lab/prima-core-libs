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
package org.primaresearch.dla.page.layout.physical.text.impl;

import java.util.List;

import org.primaresearch.dla.page.io.xml.DefaultXmlNames;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainerImpl;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextObject;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Specialised low level text object representing words within a page layout.
 * 
 * @author Christian Clausner
 *
 */
public class Word extends LowLevelTextObject implements LowLevelTextContainer {

	private LowLevelTextContainerImpl glyphs = new LowLevelTextContainerImpl();

	private ContentFactory contentFactory;

	
	protected Word(ContentFactory contentFactory, IdRegister idRegister, Id id, Polygon coords, VariableMap attributes, 
				 LowLevelTextContainer parentLine) {
		super(idRegister, id, coords, attributes, parentLine, contentFactory.getAttributeFactory());
		this.contentFactory = contentFactory;
	}

	@Override
	public boolean hasTextObjects() {
		return glyphs.hasTextObjects();
	}

	@Override
	public int getTextObjectCount() {
		return glyphs.getTextObjectCount();
	}

	@Override
	public LowLevelTextObject getTextObject(int index) {
		return glyphs.getTextObject(index);
	}

	public Glyph createGlyph() {
		return createGlyph(null);
	}
	
	/**
	 * Creates a new glyph object and adds it to this word
	 * @param id Preferred ID for the glyph (not guaranteed, check the returned glyph for the actual ID)
	 * @return New glyph
	 */
	public Glyph createGlyph(String id) {
		Glyph glyph = (Glyph)contentFactory.createContent(LowLevelTextType.Glyph);
		glyph.setParent(this);
		if (id != null) {
			try {
				glyph.setId(id);
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		addTextObject(glyph);
		return glyph;
	}

	@Override
	public void addTextObject(LowLevelTextObject textObj) {
		glyphs.addTextObject(textObj);
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.Word;
	}

	@Override
	public LowLevelTextObject getTextObject(Id id) {
		return glyphs.getTextObject(id);
	}

	@Override
	public void removeTextObject(int index) throws IndexOutOfBoundsException {
		glyphs.removeTextObject(index);
	}

	@Override
	public void removeTextObject(Id id) {
		glyphs.removeTextObject(id);
	}

	@Override
	public List<LowLevelTextObject> getTextObjectsSorted() {
		return glyphs.getTextObjectsSorted(true);
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
	
	public String getLanguage() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_language).getValue()).val;
	}

	public void setLanguage(String lang) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_language).setValue(VariableValue.createValueObject(lang));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String composeText(boolean replaceTextContent, boolean recursive) {
		String composed = "";
		//Compose from glyphs
		// No recursion (glyphs are the lowest level)
		for (int i=0; i<glyphs.getTextObjectCount(); i++) {
			composed += glyphs.getTextObject(i).getText();
		}
		if (replaceTextContent && !composed.isEmpty())
			setText(composed);
		return composed;
	}
}
