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
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.RegionContainer;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;

/**
 * Page content object iterator implementation for regions (zones/blocks).
 * 
 * @author Christian Clausner
 *
 */
public class RegionIterator implements ContentIterator {

	protected RegionType contentType;
	protected PageLayout pageLayout;
	protected Layer layer;
	protected int index = 0;
	protected NestedRegionIterator currentNestedRegionIterator = null;
	
	/**
	 * Constructor
	 * @param contentType Specific region type (e.g. table) or <code>null</code> to include all regions
	 * @param layer Restrict the iterator to this layer (use <code>null</code> for no restriction)
	 */
	public RegionIterator(PageLayout pageLayout, RegionType contentType, Layer layer) {
		this.contentType = contentType;
		this.pageLayout = pageLayout;
		this.layer = layer;
	}

	
	@Override
	public boolean hasNext() {
		if (contentType == null && layer == null) { //No specific region type or layer
			if (currentNestedRegionIterator == null)
				return index < pageLayout.getRegionCount();
			else
				return currentNestedRegionIterator.hasNext() || index < pageLayout.getRegionCount()-1;
		}
		//Filter by region type and/or layer 
		else { 
			boolean ok;
			int startIndex = index;
			
			//Check current nested iterator first
			if (currentNestedRegionIterator != null) {
				if (currentNestedRegionIterator.hasNext())
					return true;
				else
					startIndex++;
			}
			
			//Go through all remaining regions
			for (int i=startIndex; i<pageLayout.getRegionCount(); i++) {
				//Check region
				Region reg = pageLayout.getRegion(i);
				ok = true;
				if (contentType != null)
					ok = reg.getType().equals(contentType);
				if (ok && layer != null)
					ok = layer.containsRegionRef(reg.getId());
				if (ok)
					return ok;
				
				//Check nested regions
				NestedRegionIterator it = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
				if (it.hasNext())
					return true;
			}
		}
		return false;
	}

	@Override
	public ContentObject next() {
		if (!hasNext())
			return null;
		
		//Check current nested region iterator
		if (currentNestedRegionIterator != null) {
			//Is there a next nested region?
			if (currentNestedRegionIterator.hasNext())
				return currentNestedRegionIterator.next();
			else { //No next nested region
				//Proceed to next parent
				index++;
				currentNestedRegionIterator = null;
			}
		}
		
		Region ret = null;
		if (contentType == null && layer == null) { //No specific region type or layer
			ret = pageLayout.getRegion(index);
			currentNestedRegionIterator = new NestedRegionIterator((RegionContainer)ret, pageLayout, contentType, layer);
		}
		//Filter by region type and/or layer 
		else {
			boolean ok;
			
			//Go through all remaining regions
			for (int i=index; i<pageLayout.getRegionCount(); i++) {
				//Current nested iterator cannot have a result here (otherwise it would have been found at the start of this method).
				
				//Check region (not nested)
				Region reg = pageLayout.getRegion(i);
				ok = true;
				if (contentType != null)
					ok = reg.getType().equals(contentType);
				if (ok && layer != null)
					ok = layer.containsRegionRef(reg.getId());
				if (ok) {
					ret = reg;
					if (((RegionContainer)reg).getRegionCount() == 0) //No child regions
						index = i+1;
					else {
						index = i;
						currentNestedRegionIterator = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
					}
					break;
				}
				
				//Check nested regions
				NestedRegionIterator it = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
				if (it.hasNext()) {
					ret = (Region)it.next();
					currentNestedRegionIterator = it;
					index = i;
					break;
				}
			}
			
		}
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
	
	
	/**
	 * Iterates over all nested regions of a parent region
	 * 
	 * @author Christian Clausner
	 *
	 */
	private static class NestedRegionIterator extends RegionIterator {

		private RegionContainer parent;
		
		public NestedRegionIterator(RegionContainer parent, PageLayout pageLayout,
				RegionType contentType, Layer layer) {
			super(pageLayout, contentType, layer);
			this.parent = parent;
		}
		
		@Override
		public boolean hasNext() {
			if (contentType == null && layer == null) { //No specific region type or layer
				if (currentNestedRegionIterator == null)
					return index < parent.getRegionCount();
				else
					return currentNestedRegionIterator.hasNext() || index < parent.getRegionCount()-1;
			}
			//Filter by region type and/or layer 
			else { 
				boolean ok;
				int startIndex = index;
				
				//Check current nested iterator first
				if (currentNestedRegionIterator != null) {
					if (currentNestedRegionIterator.hasNext())
						return true;
					else
						startIndex++;
				}
				
				//Go through all remaining regions
				for (int i=startIndex; i<parent.getRegionCount(); i++) {
					//Check region
					Region reg = parent.getRegion(i);
					ok = true;
					if (contentType != null)
						ok = reg.getType().equals(contentType);
					if (ok && layer != null)
						ok = layer.containsRegionRef(reg.getId());
					if (ok)
						return ok;
					
					//Check nested regions
					NestedRegionIterator it = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
					if (it.hasNext())
						return true;
				}
			}
			return false;
		}

		@Override
		public ContentObject next() {
			if (!hasNext())
				return null;
			
			//Check current nested region iterator
			if (currentNestedRegionIterator != null) {
				//Is there a next nested region?
				if (currentNestedRegionIterator.hasNext())
					return currentNestedRegionIterator.next();
				else { //No next nested region
					//Proceed to next parent
					index++;
					currentNestedRegionIterator = null;
				}
			}
			
			Region ret = null;
			if (contentType == null && layer == null) { //No specific region type or layer
				ret = parent.getRegion(index);
				currentNestedRegionIterator = new NestedRegionIterator((RegionContainer)ret, pageLayout, contentType, layer);
			}
			//Filter by region type and/or layer 
			else {
				boolean ok;
				
				//Go through all remaining regions
				for (int i=index; i<parent.getRegionCount(); i++) {
					//Current nested iterator cannot have a result here (otherwise it would have been found at the start of this method).
					
					//Check region (not nested)
					Region reg = parent.getRegion(i);
					ok = true;
					if (contentType != null)
						ok = reg.getType().equals(contentType);
					if (ok && layer != null)
						ok = layer.containsRegionRef(reg.getId());
					if (ok) {
						ret = reg;
						if (((RegionContainer)reg).getRegionCount() == 0) //No child regions
							index = i+1;
						else {
							index = i;
							currentNestedRegionIterator = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
						}
						break;
					}
					
					//Check nested regions
					NestedRegionIterator it = new NestedRegionIterator((RegionContainer)reg, pageLayout, contentType, layer);
					if (it.hasNext()) {
						ret = (Region)it.next();
						currentNestedRegionIterator = it;
						index = i;
						break;
					}
				}
				
			}
			return ret;
		}
	}

}
