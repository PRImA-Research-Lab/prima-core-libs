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
package org.primaresearch.dla.page.layout.logical;

import org.primaresearch.ident.Id;

/**
 * A group member pointing to an Identifiable object (e.g. a region).
 * @author Christian Clausner
 *
 */
public class RegionRef implements GroupMember {

	private Id regionId;
	private Group parentGroup;
	
	/**
	 * Constructor
	 * @param parentGroup Parent group (this constructor does NOT add the RegionRef object to the parent group)
	 * @param regionId ID of referenced region
	 */
	RegionRef(Group parentGroup, Id regionId) {
		this.parentGroup = parentGroup;
		this.regionId = regionId;
	}
	
	/**
	 * Returns the ID of the referenced region
	 */
	public Id getRegionId() {
		return regionId;
	}

	@Override
	public Group getParent() {
		return parentGroup;
	}
	
	/**
	 * Moves this group member to another group.
	 */
	@Override
	public void moveTo(Group newParent) {
		parentGroup.remove(this);
		newParent.add(this);
	}
}
