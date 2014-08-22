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
package org.primaresearch.dla.page.layout.physical.text;

import java.util.List;

import org.primaresearch.ident.Id;

/**
 * Container for low-level text objects (text lines, words, glyphs).
 * 
 * @author Christian Clausner
 *
 */
public interface LowLevelTextContainer {

	/**
	 * Checks if there child text objects
	 * @return <code>true</code> if there are children, <code>false</code> if empty
	 */
	public boolean hasTextObjects();
	
	/**
	 * Returns the number of child text objects
	 */
	public int getTextObjectCount();

	/**
	 * Returns a child text object
	 * @param index Position
	 * @return Child text object
	 * @throws IndexOutOfBoundsException Invalid index
	 */
	public LowLevelTextObject getTextObject(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Returns a child text object
	 * @param id ID of child
	 * @return Child text object
	 */
	public LowLevelTextObject getTextObject(Id id);

	/**
	 * Adds a child text object
	 * @param textObj Object to add
	 */
	public void addTextObject(LowLevelTextObject textObj);
	
	/**
	 * Removes a child text object
	 * @param index Position
	 * @throws IndexOutOfBoundsException Invalid index
	 */
	public void removeTextObject(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Removes a child text object
	 * @param id ID of child
	 */
	public void removeTextObject(Id id);
	
	/**
	 * Returns a sorted list of all text objects (sorts by x or y position, depending on the object type). 
	 */
	public List<LowLevelTextObject> getTextObjectsSorted();
	
	/**
	 * Composes the text for this object using the child text objects.
	 * @param replaceTextContent If set to <code>true</code> the text content of this object will be replaced by the composed text (only if the composed text is not empty)
	 * @param recursive If set to <code>true</code> all lower levels of text objects are used for the composition
	 * @return Composed text
	 */
	public String composeText(boolean replaceTextContent, boolean recursive);

}
