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
package org.primaresearch.dla.page.layout.physical;

import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;

/**
 * Iterator for all page content objects of a specified type
 *  
 * @author Christian Clausner
 *
 */
public interface ContentIterator {

	/** Is there another page content object? */
	public boolean hasNext();
	
	/** Progresses the iterator and returns the next page content object. */
	public ContentObject next();
	
	/** Returns the content type of the page content objects this iterator was created for. */
	public ContentType getContentType();

	/** Returns the layer this iterator is restricted to (no <code>null</code> for no restriction) */
	public Layer getLayer();
}
