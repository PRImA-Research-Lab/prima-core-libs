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
 * Interface for text objects with multiple text content variants.
 * 
 * @author Christian Clausner
 *
 */
public interface TextContentVariants extends TextContent {

	/**
	 * Returns the number of text content variants.
	 * @return Count >= 1
	 */
	public int getTextContentVariantCount();
	
	/**
	 * Returns the text content variant for the given index
	 * @return Text content object
	 */
	public TextContent getTextContentVariant(int index);
	
	/**
	 * Adds a new text content variant
	 * @return The new text content object
	 */
	public TextContent addTextContentVariant();
	
	/**
	 * Removes the text content variant at the given index, if there are two or more variants.
	 * One text content variant has to exist at all times.	 
	 */
	public void reomveTextContentVariant(int index);

}
