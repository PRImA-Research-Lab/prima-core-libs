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
package org.primaresearch.dla.page.layout.physical;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.TextObject;

/**
 * Processes all content objects of a page using a customisable processing method.
 * 
 * @author Christian Clausner
 *
 */
public abstract class ContentObjectProcessor {

	private ContentObject currentObject = null;
	
	/** Flag to set if text lines, words, and glyphs should be included or not */
	private boolean includeLowLevelTextObjects = true;

	/**
	 * Called for each content object. Override with custom implementation.
	 */
	public abstract void doProcess(ContentObject contentObject);

	/**
	 * Called for each content object.
	 */
	private void process(ContentObject contentObject) {
		currentObject = contentObject;
		doProcess(contentObject);
		currentObject = null;
	}

	/**
	 * Returns the content object that is currently processed.
	 */
	public ContentObject getCurrentObject() {
		return currentObject;
	}

	/**
	 * Run the processor.
	 */
	public void run(Page page) {
	
		//Handle content objects
		PageLayout layout = page.getLayout();
		for (int r=0; r<layout.getRegionCount(); r++) {
			Region region = layout.getRegion(r);
			process(region);
			
			//Text object children
			if (includeLowLevelTextObjects && region instanceof LowLevelTextContainer) {
				ProcessChildren((LowLevelTextContainer)region);
			}
			
			//Sub-regions (nested regions)
			if (region instanceof RegionContainer) {
				ProcessNestedRegions((RegionContainer)region);
			}
		}
	}
	
	/**
	 * Processes the child content objects of the given parent (recursive).
	 */
	private void ProcessChildren(LowLevelTextContainer parent) {
		for (int c=0; c<parent.getTextObjectCount(); c++) {
			TextObject obj = parent.getTextObject(c);
			process((ContentObject)obj);
			
			if (obj instanceof LowLevelTextContainer) {
				ProcessChildren((LowLevelTextContainer)obj);
			}
		}
	}
	
	/**
	 * Processes nested regions of the given parent (recursive).
	 */
	private void ProcessNestedRegions(RegionContainer container) {
		for (int c=0; c<container.getRegionCount(); c++) {
			ContentObject region = container.getRegion(c);
			process((ContentObject)region);
			
			//Text object children
			if (includeLowLevelTextObjects && region instanceof LowLevelTextContainer) {
				ProcessChildren((LowLevelTextContainer)region);
			}
			
			//Sub-regions (nested regions)
			if (region instanceof RegionContainer) {
				ProcessNestedRegions((RegionContainer)region);
			}
		}
	}

	/** 
	 * Flag to set if text lines, words, and glyphs should be included or not 
	 */
	public boolean isIncludeLowLevelTextObjects() {
		return includeLowLevelTextObjects;
	}

	/** 
	 * Flag to set if text lines, words, and glyphs should be included or not 
	 */
	public void setIncludeLowLevelTextObjects(boolean includeLowLevelTextObjects) {
		this.includeLowLevelTextObjects = includeLowLevelTextObjects;
	}
	
	
}
