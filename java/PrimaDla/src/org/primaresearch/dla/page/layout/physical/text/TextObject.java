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


/**
 * Interface for objects with text content (e.g. text region, word, ...).
 * 
 * @author Christian Clausner
 *
 */
public interface TextObject extends TextContentVariants {
	

	/**
	 * Bold text?
	 * @return <code>true</code> for bold text, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isBold();
	
	/**
	 * Bold text?
	 * @param bold <code>true</code> for bold text, <code>false</code> for normal text, or <code>null</code> for 'not set'. 
	 */
	public void setBold(Boolean bold);

	/**
	 * Italic text?
	 * @return <code>true</code> for italic text, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isItalic();
	
	/**
	 * Italic text?
	 * @param italic <code>true</code> for italic text, <code>false</code> for normal text, or <code>null</code> for 'not set'. 
	 */
	public void setItalic(Boolean italic);

	/**
	 * Underlined text?
	 * @return <code>true</code> for underlined text, <code>false</code> for not underlined text, or <code>null</code> if not set. 
	 */
	public Boolean isUnderlined();
	
	/**
	 * Underlined text?
	 * @param underlined <code>true</code> for underlined text, <code>false</code> for not underlined text, or <code>null</code> if not set. 
	 */
	public void setUnderlined(Boolean underlined);

	/**
	 * Subscript (small letters below the baseline)?
	 * @return <code>true</code> for subscript, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isSubscript();
	
	/**
	 * Subscript (small letters below the baseline)?
	 * @param subscript <code>true</code> for subscript, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public void setSubscript(Boolean subscript);

	/**
	 * Superscript (small letters above the line of text)?
	 * @return <code>true</code> for superscript, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isSuperscript();
	
	/**
	 * Superscript (small letters above the line of text)?
	 * @param superscript <code>true</code> for superscript, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public void setSuperscript(Boolean superscript);

	/**
	 * Strikethrough (line through text)?
	 * @return <code>true</code> for strikethrough, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isStrikethrough();
	
	/**
	 * Strikethrough (line through text)?
	 * @param strikethrough <code>true</code> for strikethrough, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public void setStrikethrough(Boolean strikethrough);

	/**
	 * Small caps (lower case characters appear as smaller capitals)?
	 * @return <code>true</code> for small caps, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isSmallCaps();
	
	/**
	 * Small caps (lower case characters appear as smaller capitals)?
	 * @param smallCaps <code>true</code> for small caps, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public void setSmallCaps(Boolean smallCaps);

	/**
	 * Letter spaced (gaps between characters)?
	 * @return <code>true</code> for letter spaced text, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public Boolean isLetterSpaced();
	
	/**
	 * Letter spaced (gaps between characters)?
	 * @param letterSpaced <code>true</code> for letter spaced text, <code>false</code> for normal text, or <code>null</code> if not set. 
	 */
	public void setLetterSpaced(Boolean letterSpaced);

	
}
