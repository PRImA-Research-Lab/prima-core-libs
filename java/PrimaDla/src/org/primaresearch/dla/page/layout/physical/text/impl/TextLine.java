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
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.VariableValue;

/**
 * Specialised low level text object representing text lines within a page layout.
 * 
 * @author Christian Clausner
 *
 */
public class TextLine extends LowLevelTextObject 
						implements LowLevelTextContainer {

	private LowLevelTextContainerImpl words = new LowLevelTextContainerImpl();

	private Polygon baseline;
	
	private ContentFactory contentFactory;

	
	protected TextLine(ContentFactory contentFactory, IdRegister idRegister, Id id, Polygon coords, 
						VariableMap attributes, //VariableMap textStyle, 
						LowLevelTextContainer parentRegion) {
		super(idRegister, id, coords, attributes, parentRegion, contentFactory.getAttributeFactory());
		this.contentFactory = contentFactory;
	}
	
	@Override
	public boolean hasTextObjects() {
		return words.hasTextObjects();
	}

	@Override
	public int getTextObjectCount() {
		return words.getTextObjectCount();
	}

	@Override
	public LowLevelTextObject getTextObject(int index) {
		return words.getTextObject(index);
	}

	public Word createWord() {
		return createWord(null);
	}
	
	/**
	 * Creates a new word object and adds it to this text line.
	 * @param id Preferred ID for the word (not guaranteed, check the result word for the actual ID)
	 * @return The new word
	 */
	public Word createWord(String id) {
		Word word = (Word)contentFactory.createContent(LowLevelTextType.Word);
		word.setParent(this);
		if (id != null) {
			try {
				word.setId(id);
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		addTextObject(word);
		return word;
	}

	@Override
	public void addTextObject(LowLevelTextObject textObj) {
		words.addTextObject(textObj);
	}

	@Override
	public ContentType getType() {
		return LowLevelTextType.TextLine;
	}

	@Override
	public LowLevelTextObject getTextObject(Id id) {
		return words.getTextObject(id);
	}

	@Override
	public void removeTextObject(int index) throws IndexOutOfBoundsException {
		words.removeTextObject(index);
	}

	@Override
	public void removeTextObject(Id id) {
		words.removeTextObject(id);
	}

	@Override
	public List<LowLevelTextObject> getTextObjectsSorted() {
		return words.getTextObjectsSorted(true);
	}

	/**
	 * Returns the baseline attached to this text line.
	 */
	public Polygon getBaseline() {
		return baseline;
	}

	/**
	 * Sets the baseline attached to this text line.
	 */
	public void setBaseline(Polygon baseline) {
		this.baseline = baseline;
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
	
	public String getPrimaryLanguage() {
		return ((StringValue)getAttributes().get(DefaultXmlNames.ATTR_primaryLanguage).getValue()).val;
	}

	public void setPrimaryLanguage(String lang) {
		try {
			getAttributes().get(DefaultXmlNames.ATTR_primaryLanguage).setValue(VariableValue.createValueObject(lang));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String composeText(boolean replaceTextContent, boolean recursive) {
		String composed = "";
		//Compose from words
		for (int i=0; i<words.getTextObjectCount(); i++) {
			if (recursive)
				((LowLevelTextContainer)words.getTextObject(i)).composeText(replaceTextContent, recursive);
			if (i > 0)
				composed += " ";
			composed += words.getTextObject(i).getText();
		}
		if (replaceTextContent && !composed.isEmpty())
			setText(composed);
		return composed;
	}

}
