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
package org.primaresearch.dla.page.layout.logical;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;

/**
 * Class for logical reading order of layout regions.
 * The root group provides access to the actual reading order members.
 * 
 * @author Christian Clausner
 *
 */
public class ReadingOrder {

	private Group root;
	private Double confidence;
	
	/**
	 * Constructor
	 * @param layout Page layout the reading order is intended for 
	 * @param idRegister ID register (for creating groups)
	 * @param contentFactory Content factory (for creating groups)
	 */
	public ReadingOrder(PageLayout layout, IdRegister idRegister, ContentFactory contentFactory) {
		try {
			root = new Group(layout, idRegister, contentFactory, idRegister.generateId("g"), null, true);
		} catch (InvalidIdException e) {
		}
	}
	
	/**
	 * Returns the root group (the reading order always has a root group)
	 */
	public Group getRoot() {
		return root;
	}
	
	/**
	 * Checks the the region with the given ID is referenced in the reading order.
	 * 
	 * @param regionId ID of referenced region
	 * @return True, if the region has been found; false otherwise
	 */
	public boolean contains(Id regionId) {
		if (root != null)
			return root.containsRegionRef(regionId);
		return false;
	}

	/**
	 * Reading order recognition confidence
	 * @return 0.0..1.0
	 */
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * Reading order recognition confidence
	 * @param confidence 0.0..1.0
	 */
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	
	
}
