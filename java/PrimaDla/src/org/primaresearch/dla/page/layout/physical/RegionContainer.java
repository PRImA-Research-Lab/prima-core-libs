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
package org.primaresearch.dla.page.layout.physical;


/**
 * Interface for classes that are able to contain nested layout regions.
 * 
 * @author Christian Clausner
 *
 */
public interface RegionContainer {
	
	/**
	 * Checks if this object contains nested regions 
	 * @return <code>true</code> if there are nested regions, <code>false</code> otherwise
	 */
	public boolean hasRegions();
	
	/**
	 * Returns the number of regions that are nested within this object
	 */
	public int getRegionCount();
	
	/**
	 * Returns a nested region
	 * @param index Position
	 * @return Region object
	 */
	public Region getRegion(int index);
	
	/**
	 * Adds a nested region
	 * @param region Region to add
	 */
	public void addRegion(Region region);
	
	/**
	 * Removes a nested region
	 * @param region Region to remove
	 */
	public void removeRegion(Region region);

}
