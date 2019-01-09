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
package org.primaresearch.dla.page.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.primaresearch.collections.IndexedMap;
import org.primaresearch.collections.IndexedMapImpl;
import org.primaresearch.dla.page.layout.logical.ContentObjectRelation;
import org.primaresearch.dla.page.layout.logical.ContentObjectRelation.RelationType;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.GroupMember;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.logical.Layers;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.logical.RegionRef;
import org.primaresearch.dla.page.layout.logical.Relations;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.RegionContainer;
import org.primaresearch.dla.page.layout.physical.impl.LowLevelTextObjectIterator;
import org.primaresearch.dla.page.layout.physical.impl.RegionIterator;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextObject;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Dimension;
import org.primaresearch.maths.geometry.Polygon;

/**
 * Class representing the layout and text content of a document page.
 * 
 * @author Christian Clausner
 *
 */
public class PageLayout {

	private Dimension size = new Dimension();
	
	private GeometricObject border = null;
	private GeometricObject printSpace = null;
	
	private ContentFactory contentFactory;
	
	private IndexedMap<Id, Region> regions = new IndexedMapImpl<Id, Region>();
	
	private ReadingOrder readingOrder = null;
	private Layers layers = null;
	private Relations relations = null;
	
	private static Comparator<ContentObject> contentObjectSizeComparator = null;
	
	/**
	 * Constructor
	 * @param contentFactory Internal factory to create content such as regions, lines, reading order, ...  
	 */
	public PageLayout(ContentFactory contentFactory) {
		this.contentFactory = contentFactory;
	}
	
	/**
	 * Creates a region of the specified type. 
	 * @param type Type of region (e.g. TextRegion, ImageRegion, ...) 
	 * @return Region object
	 */
	public Region createRegion(RegionType type) {
		return createRegion(type, null);
	}

	/**
	 * Creates a region of the specified type. 
	 * @param type Type of region (e.g. TextRegion, ImageRegion, ...) 
	 * @return Region object
	 */
	public Region createRegion(RegionType type, String id) {
		return createRegion(type, id, null);
	}

	/**
	 * Creates a region of the specified type. 
	 * @param type Type of region (e.g. TextRegion, ImageRegion, ...) 
	 * @param id Preferred ID for the region (not guaranteed, check the returned region for the actual ID)
	 * @param parentRegion Parent region (for nesting of regions) 
	 * @return Region object
	 */
	public Region createRegion(RegionType type, String id, RegionContainer parentRegion) {
		Region reg = (Region)contentFactory.createContent(type, parentRegion);
		if (id != null) {
			try {
				reg.setId(id);
			} catch (InvalidIdException e) {
				e.printStackTrace(); 
			}
		}
		if (parentRegion == null )
			regions.put(reg.getId(), reg);
		else
			parentRegion.addRegion(reg);
		return reg;
	}
	
	/**
	 * Returns the number of regions in this page layout.
	 */
	public int getRegionCount() {
		return regions.size();
	}

	/**
	 * Returns the region at the specified index.
	 * @throws IndexOutOfBoundsException
	 */
	public Region getRegion(int index) {
		return regions.getAt(index);
	}
	
	/**
	 * Returns the region with the given ID.
	 */
	public Region getRegion(Id regionId) {
		if (regionId == null)
			return null;
		return regions.get(regionId);
	}

	/**
	 * Returns the region with the given ID.
	 */
	public Region getRegion(String regionId) {
		if (regionId == null)
			return null;
		try {
			return regions.get(contentFactory.getIdRegister().getId(regionId));
		} catch (InvalidIdException e) {
			return null;
		}
	}

	/**
	 * Looks for a region at the given position within the document page.
	 * @return A region object or null.
	 */
	public Region getRegionAt(int x, int y) {
		List<Region> candidates = new LinkedList<Region>();
		for (ContentIterator it = this.iterator(null); it.hasNext(); ) {
			ContentObject region = it.next();
			if (region.getCoords() != null) {
				Polygon coords = region.getCoords();
				if (coords.isPointInside(x, y)) {
					candidates.add((Region)region);
				}
			}
		}
		
		if (candidates.size() > 1) {
			//If multiple candidates -> sort by size and return smallest
			Collections.sort(candidates, getContentObjectSizeComparator());
		}
		if (!candidates.isEmpty())
			return candidates.get(0);
		
		/*for (int i=0; i<regions.size(); i++) {
			Region region = regions.getAt(i);
			if (region.getCoords() != null) {
				Polygon coords = region.getCoords();
				if (coords.isPointInside(x, y)) {
					return region;
				}
			}
		}*/
		return null;
	}
	
	/**
	 * Looks for a content object of a specific type at the given position within the document page.
	 * @return A content object or null.
	 */
	public ContentObject getObjectAt(int x, int y, ContentType type) {
		if (type instanceof RegionType)
			return getRegionAt(x, y);
		else if (type instanceof LowLevelTextType) { //Text lines, word, glyph
			for (ContentIterator it = this.iterator(RegionType.TextRegion); it.hasNext(); ) {
				Region reg = (Region)it.next(); 
				if (reg instanceof LowLevelTextContainer) {
					ContentObject obj = getLowLevelTextObjectAt((LowLevelTextContainer)reg, x, y, (LowLevelTextType)type);
					if (obj != null)
						return obj;
				}
			}		
		}
		return null;
	}
	
	/**
	 * Returns the content object of given type and ID 
	 * @param type Object type (e.g. region or text line)
	 * @param id Object id
	 * @return The object or <code>null</code> if it could not be found
	 */
	public ContentObject getObject(ContentType type, String id) {
		if (type instanceof RegionType)
			return getRegion(id);
		else if (type instanceof LowLevelTextType) { //Text lines, word, glyph
			for (int i=0; i<regions.size(); i++) {
				Region reg = regions.getAt(i); 
				if (reg instanceof LowLevelTextContainer) {
					ContentObjectRelation rel = getLowLevelTextObject((LowLevelTextContainer)reg, (LowLevelTextType)type, id);
					if (rel != null)
						return rel.getObject2();
				}
			}		
		}
		return null;
	}
	
	/**
	 * Returns a parent-child relation object
	 * @param childType Content type of the child
	 * @param childId ID of the child
	 * @return Relation object or <code>null</code>
	 */
	public ContentObjectRelation getParentChildRelation(ContentType childType, String childId) {
		if (childType instanceof RegionType)
			return null;
		else if (childType instanceof LowLevelTextType) { //Text lines, word, glyph
			for (int i=0; i<regions.size(); i++) {
				Region reg = regions.getAt(i); 
				if (reg instanceof LowLevelTextContainer) {
					ContentObjectRelation rel = getLowLevelTextObject((LowLevelTextContainer)reg, (LowLevelTextType)childType, childId);
					if (rel != null)
						return rel;
				}
			}		
		}
		return null;
	}
	
	/**
	 * Looks recursively for a text object with the given ID.
	 * @param parent Parent container
	 * @param type Text object type to look for
	 * @return A text object or null.
	 */
	private ContentObjectRelation getLowLevelTextObject(LowLevelTextContainer parent, LowLevelTextType type, String id) {
		if (parent == null)
			return null;
			
		//Iterate over all child text objects
		for (int i=0; i<parent.getTextObjectCount(); i++) {
			LowLevelTextObject obj = parent.getTextObject(i);

			//Is the child of the type we are looking for?
			if (type.equals(obj.getType())) {
				//Check if the given ID matches
				try {
					if (obj.getId().equals(contentFactory.getIdRegister().getId(id)))
						return new ContentObjectRelation((ContentObject)parent, obj, RelationType.ParentChildRelation, null, null);
				} catch (InvalidIdException e) {
					return null;
				}
			}
			//Is the child a text object container itself?
			else if (obj instanceof LowLevelTextContainer){
				//Recursion
				ContentObjectRelation rel = getLowLevelTextObject((LowLevelTextContainer)obj, type, id);
				if (rel != null)
					return rel;
			} 
			else //If the child is neither of the type we're looking for nor a container, we can leave the recursion now.
				return null;
		}
		return null;
	}
	
	/**
	 * Looks recursively for a text object at the given position.
	 * @param parent Parent container
	 * @param type Text object type to look for
	 * @return A text object or null.
	 */
	private LowLevelTextObject getLowLevelTextObjectAt(LowLevelTextContainer parent, int x, int y, LowLevelTextType type) {
		if (parent == null)
			return null;
			
		//Iterate over all child text objects
		for (int i=0; i<parent.getTextObjectCount(); i++) {
			LowLevelTextObject obj = parent.getTextObject(i);

			//Is the child of the type we are looking for?
			if (type.equals(obj.getType())) {
				//Check if the given point is inside the childs polygon
				Polygon polygon = ((GeometricObject)obj).getCoords();
				if (polygon != null && polygon.isPointInside(x, y)) {
					return obj;
				}
			}
			//Is the child a text object container itself?
			else if (obj instanceof LowLevelTextContainer){
				//Recursion
				obj = getLowLevelTextObjectAt((LowLevelTextContainer)obj, x, y, type);
				if (obj != null)
					return obj;
			} 
			else //If the child is neither of the type we're looking for nor a container, we can leave the recursion now.
				return null;
		}
		return null;
	}
	
	/**
	 * Checks if this page layout contains text objects of the given type 
	 */
	public boolean hasLowLevelTextObject(LowLevelTextType type) {
		
		for (int i=0; i<regions.size(); i++) {
			Region reg = regions.getAt(i); 
			if (reg instanceof LowLevelTextContainer) {
				if (hasLowLevelTextObject((LowLevelTextContainer)reg, type))
					return true;
			}
		}		

		return false;
	}
	
	/**
	 * Checks if the given text container contains text objects of the specified type 
	 */
	private boolean hasLowLevelTextObject(LowLevelTextContainer container, LowLevelTextType type) {
		for (int i=0; i<container.getTextObjectCount(); i++) {
			LowLevelTextObject obj = container.getTextObject(i);
			if (obj.getType().equals(type))
				return true;
			if (obj instanceof LowLevelTextContainer) {
				if (hasLowLevelTextObject((LowLevelTextContainer)obj, type))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns the page border object
	 */
	public GeometricObject getBorder() {
		return border;
	}

	/**
	 * Sets the page border.
	 */
	public void setBorder(GeometricObject border) {
		this.border = border;
	}
	
	/**
	 * Returns the page print space object.
	 */
	public GeometricObject getPrintSpace() {
		return printSpace;
	}

	/**
	 * Sets the page print space.
	 */
	public void setPrintSpace(GeometricObject printSpace) {
		this.printSpace = printSpace;
	}
	
	/**
	 * Returns the page width.
	 */
	public int getWidth() {
		return size.width;
	}
	
	/**
	 * Returns the page height.
	 */
	public int getHeight() {
		return size.height;
	}
	
	/**
	 * Sets width and height of the page. 
	 */
	public void setSize(int width, int height) {
		size.width = width;
		size.height = height;
	}
	
	/**
	 * Returns the reading order of the layout.
	 */
	public ReadingOrder getReadingOrder() {
		return readingOrder;
	}

	/**
	 * Creates and returns a new reading order for the layout.
	 */
	public ReadingOrder createReadingOrder() {
		readingOrder = contentFactory.createReadingOrder(this);
		return readingOrder;
	}
	
	/**
	 * Returns the layers of the layout.
	 */
	public Layers getLayers() {
		return layers;
	}

	/**
	 * Creates and returns a new layers object for the layout.
	 */
	public Layers createLayers() {
		layers = contentFactory.createLayers(this);
		return layers;
	}
	
	/**
	 * Removes the specified region object from the page layout.
	 */
	public void removeRegion(Id regionId) {
		removeRegion(regionId, false);
	}

	/**
	 * Removes the specified region object from the page layout.
	 * @param unregisterId If set to <code>true</code> the ID will be removed from the ID register and is free to be used again
	 */
	public void removeRegion(Id regionId, boolean unregisterId) {
		if (regionId == null)
			return;
		regions.remove(regionId);
		if (unregisterId)
			this.contentFactory.getIdRegister().unregisterId(regionId);
	}

	/**
	 * Removes the region at the specified index from the page layout.
	 * @throws IndexOutOfBoundsException
	 */
	public void removeRegion(int index) {
		removeRegion(index, false);
	}

	/**
	 * Removes the region at the specified index from the page layout.
	 * @param unregisterId If set to <code>true</code> the ID will be removed from the ID register and is free to be used again
	 * @throws IndexOutOfBoundsException
	 */
	public void removeRegion(int index, boolean unregisterId) {
		Region reg = regions.removeAt(index);
		if (unregisterId && reg != null)
			this.contentFactory.getIdRegister().unregisterId(reg.getId());
	}

	/**
	 * Returns a sorted list of all regions. The sorting is primarily done by reading order and secondarily by y position. 
	 * @return List of region objects
	 */
	public List<Region> getRegionsSorted() {
		return getRegionsSorted(false);
	}
	
	/**
	 * Returns a sorted list of all regions. The sorting is primarily done by reading order and secondarily by y position. 
	 * @return List of region objects
	 */
	public List<Region> getRegionsSorted(boolean includeNestedRegion) {
		List<Region> sortedRegions = new ArrayList<Region>(this.getRegionCount());
		
		List<Region> notInReadingOrder = new ArrayList<Region>();
		
		if (readingOrder != null) {
			addRegionsFromReadingOrder(readingOrder.getRoot(), sortedRegions);

			//Save ids in a set for fast lookup
			Set<Id> idSet = new HashSet<Id>();
			for (int i=0; i<sortedRegions.size(); i++)
				idSet.add(sortedRegions.get(i).getId());

			if (includeNestedRegion) {
				for (RegionIterator it = new RegionIterator(this, null, null); it.hasNext(); ) {
					Region reg = (Region)it.next();
					if (!idSet.contains(reg.getId()))
						notInReadingOrder.add(reg);
				}
			} else {
				for (int i=0; i<regions.size(); i++) {
					if (!idSet.contains(regions.getAt(i).getId()))
						notInReadingOrder.add(regions.getAt(i));
				}
			}
		}
		else { //No reading order
			if (includeNestedRegion) {
				for (RegionIterator it = new RegionIterator(this, null, null); it.hasNext(); ) 
					notInReadingOrder.add((Region)it.next());
			} else {
				for (int i=0; i<regions.size(); i++)
					notInReadingOrder.add(regions.getAt(i));
			}
		}
		
		//Sort the remaining regions by y position
		Collections.sort(notInReadingOrder, GeometricObjectPositionComparator.getInstance(false));
		
		for (int i=0; i<notInReadingOrder.size(); i++)
			sortedRegions.add(notInReadingOrder.get(i));

		return sortedRegions;
	}
	
	/**
	 * Adds all regions that are referenced from the given group to the specified list
	 * @param group (in) Group with region references
	 * @param list (out) Target list
	 */
	private void addRegionsFromReadingOrder(Group group, List<Region> list) {
		if (group == null)
			return;
		//Children
		for (int i=0; i<group.getSize(); i++) {
			GroupMember member = group.getMember(i);
			if (member instanceof RegionRef) {
				Region reg = getRegion(((RegionRef)member).getRegionId());
				if (reg != null)
					list.add(reg);
			}
			else { //Group
				addRegionsFromReadingOrder((Group)member, list);
			}
		}
	}
	
	/**
	 * Returns the container for all relations between regions.
	 */
	public Relations getRelations() {
		if (relations == null)
			relations = new Relations(contentFactory.getIdRegister());
		return relations;
	}
	
	/**
	 * Creates a region object with the id of the given object and the new region type. 
	 * @param region Region to change
	 * @param newType New region type
	 * @return Region object with new type
	 * @throws IllegalArgumentException If the input is not a region
	 */
	public ContentObject changeTypeOfRegion(ContentObject region, RegionType newType) throws IllegalArgumentException {
		if (region == null || !(region.getType() instanceof RegionType))
			throw new IllegalArgumentException("Not a region: "+region.getType());
		
		if (region.getType().equals(newType))
			return region;
		
		ContentObject newRegion = contentFactory.createContent(newType);

		//Remove old region from layout
		RegionContainer parentRegion = null;
		if (region instanceof Region)
			parentRegion = ((Region)region).getParentRegion();
		if (parentRegion == null)
			removeRegion(region.getId());
		else
			((RegionContainer)parentRegion).removeRegion((Region)region);
		this.contentFactory.getIdRegister().unregisterId(region.getId());

		//Copy ID, outline, ...
		try {
			newRegion.setId(region.getId());
		} catch (InvalidIdException e) {
		}
		newRegion.setCoords(region.getCoords().clone());

		//Add new region to layout
		if (parentRegion == null || !(newRegion instanceof Region))
			regions.put(newRegion.getId(), (Region)newRegion);
		else
			parentRegion.addRegion((Region)newRegion);
		
		return newRegion;
	}

	/**
	 * Returns a new iterator for a specific page content type.
	 * @param contentType A specific region type or low level text object type. Use <code>null</code> for an iterator that includes all regions.
	 * @return The iterator
	 */
	public ContentIterator iterator(ContentType contentType) {
		return iterator(contentType, null);
	}
	
	/**
	 * Returns a new iterator for a specific page content type.
	 * @param contentType A specific region type or low level text object type. Use <code>null</code> for an iterator that includes all regions.
	 * @param layer Restrict the iterator to this layer (use <code>null</code> for no restriction)
	 * @return The iterator
	 */
	public ContentIterator iterator(ContentType contentType, Layer layer) {
		if (contentType == null || contentType instanceof RegionType)
			return new RegionIterator(this, (RegionType)contentType, layer);
		else if (contentType instanceof LowLevelTextType)
			return new LowLevelTextObjectIterator(this, (LowLevelTextType)contentType, layer);
		throw new IllegalArgumentException("Unsupported content type for iterator");
	}
	
	/**
	 * Creates a comparator using the bounding box area of content objects
	 * @return Comparator object
	 */
	private static Comparator<ContentObject> getContentObjectSizeComparator() {
		if (contentObjectSizeComparator == null) {
			contentObjectSizeComparator = new Comparator<ContentObject>() {
				@Override
				public int compare(ContentObject o1, ContentObject o2) {
					if (o1 == null || o2 == null || o1.getCoords() == null || o2.getCoords() == null
							|| o1.getCoords().getSize() < 3 || o2.getCoords().getSize() < 3)
						return 0;
					return new Integer(o1.getCoords().getBoundingBox().getWidth() 
							* o1.getCoords().getBoundingBox().getWidth()).compareTo(
							new Integer(o2.getCoords().getBoundingBox().getWidth() 
									* o2.getCoords().getBoundingBox().getWidth()));
				}
			};
		}
		return contentObjectSizeComparator;
	}
}
