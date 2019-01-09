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

/**
 * Layer representing a group of regions with a specific z-index
 * 
 * @author Christian Clausner
 *
 */
public class Layer extends Group {

	private int zIndex = 0;
	

	/**
	 * Constructor
	 * @param layout Page layout the layer belongs to
	 * @param idRegister ID register of the page layout
	 * @param contentFactory Content factory of the page layout
	 * @param id Layer ID
	 */
	Layer(PageLayout layout, IdRegister idRegister,
			ContentFactory contentFactory, Id id) {

		super(layout, idRegister, contentFactory, id, null, false);
	}

	/**
	 * Returns the z index of this layer (for ordering in z-dimension)
	 */
	public int getZIndex() {
		return zIndex;
	}

	/**
	 * Sets the z index of this layer (for ordering in z-dimension)
	 */
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

}
