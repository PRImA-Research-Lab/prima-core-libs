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
package org.primaresearch.dla.page.layout.physical.impl;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextObject;

/**
 * Page content object iterator implementation for low  level text objects (text lines, words, and glyphs).
 * 
 * @author Christian Clausner
 *
 */
public class LowLevelTextObjectIterator implements ContentIterator {
	
	private LowLevelTextType contentType;
	private Layer layer;
	private int index = 0;
	private ContentIterator parentIterator;
	private LowLevelTextContainer parent = null;
	
	/**
	 * Constructor
	 * @param contentType Low level text object type (text line, word, or glyph)
	 * @param layer Restrict the iterator to this layer (use <code>null</code> for no restriction)
	 */
	public LowLevelTextObjectIterator(PageLayout pageLayout, LowLevelTextType contentType, Layer layer) {
		this.contentType = contentType;
		this.layer = layer;
		
		if (contentType.equals(LowLevelTextType.TextLine))
			parentIterator = new RegionIterator(pageLayout, RegionType.TextRegion, layer);
		else if (contentType.equals(LowLevelTextType.Word))
			parentIterator = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.TextLine, layer);
		else if (contentType.equals(LowLevelTextType.Glyph))
			parentIterator = new LowLevelTextObjectIterator(pageLayout, LowLevelTextType.Word, layer);
		
		parent = (LowLevelTextContainer)parentIterator.next();
	}

	@Override
	public boolean hasNext() {
		while (parent != null) {
			if (index < parent.getTextObjectCount())
				return true;
			parent = (LowLevelTextContainer)parentIterator.next();
			index = 0;
		}
		return false;
	}

	@Override
	public ContentObject next() {
		if (!hasNext())
			return null;
		LowLevelTextObject ret = parent.getTextObject(index);
		index++;
		return ret;
	}

	@Override
	public ContentType getContentType() {
		return contentType;
	}

	@Override
	public Layer getLayer() {
		return layer;
	}

}
