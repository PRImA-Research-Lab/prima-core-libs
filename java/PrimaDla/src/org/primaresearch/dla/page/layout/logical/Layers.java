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

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;

/**
 * Container class for logical layers that can contain layout regions.
 * 
 * @author Christian Clausner
 *
 */
public class Layers {

	private PageLayout layout;
	private IdRegister idRegister;
	private ContentFactory contentFactory;
	private List<Layer> layers = new ArrayList<Layer>();

	/**
	 * Constructor
	 * @param layout Page layout which the layers are intended for
	 * @param idRegister ID register (for creating layers)
	 * @param contentFactory Content factory (for creating layers)
	 */
	public Layers(PageLayout layout, IdRegister idRegister, ContentFactory contentFactory) {
		this.layout = layout;
		this.idRegister = idRegister;
		this.contentFactory = contentFactory;
	}
	
	/**
	 * Returns the number of layers.
	 */
	public int getSize() {
		return layers.size();
	}

	/**
	 * Returns the layer at the given index.
	 * @throws IndexOutOfBoundsException
	 */
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	/**
	 * Creates and returns a new layer.
	 */
	public Layer createLayer() {
		Layer layer;
		try {
			layer = new Layer(layout, idRegister, contentFactory, idRegister.generateId("lay"));
			layers.add(layer);
			return layer;
		} catch (InvalidIdException e) {
		}
		return null;
	}
}
