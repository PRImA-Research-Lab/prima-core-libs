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
package org.primaresearch.dla.page.layout.physical.shared;

/**
 * Content types for low level text objects (text lines, words, glyphs).
 * 
 * @author Christian Clausner
 */
public class LowLevelTextType extends ContentType {
	private static final long serialVersionUID = 1L;
	
	/** Type for text line objects */
	public static final LowLevelTextType TextLine = new LowLevelTextType("TextLine");
	/** Type for word objects */
	public static final LowLevelTextType Word 	= new LowLevelTextType("Word");
	/** Type for glyph objects */
	public static final LowLevelTextType Glyph 	= new LowLevelTextType("Glyph");
	/** Type for grapheme objects */
	public static final LowLevelTextType Grapheme 	= new LowLevelTextType("Grapheme");
	/** Type for grapheme groups */
	public static final LowLevelTextType GraphemeGroup 	= new LowLevelTextType("GraphemeGroup");
	/** Type for non-printing character objects */
	public static final LowLevelTextType NonPrintingCharacter 	= new LowLevelTextType("NonPrintingCharacter");

	/**
	 * Empty constructor (required for GWT)
	 */
	protected LowLevelTextType() {
		super();
	}
	
	/**
	 * Constructor
	 * @param name Type name
	 */
	protected LowLevelTextType(String name) {
		super(name);
	}
}
