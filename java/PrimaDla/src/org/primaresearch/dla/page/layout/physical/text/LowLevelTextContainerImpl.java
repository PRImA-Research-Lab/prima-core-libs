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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primaresearch.collections.IndexedMap;
import org.primaresearch.collections.IndexedMapImpl;
import org.primaresearch.dla.page.layout.GeometricObjectPositionComparator;
import org.primaresearch.ident.Id;

/**
 * Basic implementation of a low level text object container.
 * 
 * @author Christian Clausner
 *
 */
public class LowLevelTextContainerImpl {

	private IndexedMap<Id, LowLevelTextObject> textObjects = new IndexedMapImpl<Id, LowLevelTextObject>();
	
	public boolean hasTextObjects() {
		return !textObjects.isEmpty();
	}

	public int getTextObjectCount() {
		return textObjects.size();
	}

	public LowLevelTextObject getTextObject(int index) {
		return textObjects.getAt(index);
	}

	public void addTextObject(LowLevelTextObject textObj) {
		textObjects.put(textObj.getId(), textObj);
	}

	public LowLevelTextObject getTextObject(Id id) {
		return textObjects.get(id);
	}

	public void removeTextObject(int index) throws IndexOutOfBoundsException {
		textObjects.removeAt(index);
	}

	public void removeTextObject(Id id) {
		textObjects.remove(id);
	}
	
	public List<LowLevelTextObject> getTextObjectsSorted(boolean sortByX) {
		List<LowLevelTextObject> sorted = new ArrayList<LowLevelTextObject>(textObjects.size());
		sorted.addAll(textObjects.values());
		Collections.sort(sorted, GeometricObjectPositionComparator.getInstance(sortByX));
		return sorted;
	}


}
