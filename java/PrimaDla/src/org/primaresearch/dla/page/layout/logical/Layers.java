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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private LayerComparator comparator = null;
	private boolean manageZIndexes = true;

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
	 * Returns the topmost layer
	 * @return Returns a layer or <code>null</code> if there is no layer
	 */
	public Layer getFrontLayer() {
		if (layers.isEmpty())
			return null;
		return layers.get(layers.size()-1);
	}

	/**
	 * Returns the back layer
	 * @return Returns a layer or <code>null</code> if there is no layer
	 */
	public Layer getBackLayer() {
		if (layers.isEmpty())
			return null;
		return layers.get(0);
	}

	/**
	 * Creates, adds, and returns a new layer (adds to the front).
	 */
	public Layer createLayer() {
		return createLayer(true);
	}
	
	/**
	 * Creates, adds, and returns a new layer.
	 * @param addToFront Use <code>true</code> to add the layer to the front (topmost) or <code>false</code> to add it to the back.
	 */
	public Layer createLayer(boolean addToFront) {
		Layer layer;
		try {
			layer = new Layer(layout, idRegister, contentFactory, idRegister.generateId("lay"));
			sort();
			if (addToFront)
				layers.add(layer);
			else //Add to back
				layers.add(0, layer);
			if (manageZIndexes)
				updateZIndexes();
			return layer;
		} catch (InvalidIdException e) {
		}
		return null;
	}
	
	/**
	 * Sorts the layer list back (low z-index) to front (high z-index)
	 */
	public void sort() {
		if (comparator == null)
			comparator = new LayerComparator();
		Collections.sort(layers, comparator);
	}
	
	/**
	 * Sets the z-index of each layer equal to its position in the layers list
	 */
	private void updateZIndexes() {
		for (int i=0; i<layers.size(); i++)
			layers.get(i).setZIndex(i);
	}

	/**
	 * Checks if the z-indexes of the layers are managed by this class 
	 */
	public boolean isManageZIndexes() {
		return manageZIndexes;
	}

	/**
	 * Sets if the z-indexes of the layers are managed by this class 
	 */
	public void setManageZIndexes(boolean manageZIndexes) {
		this.manageZIndexes = manageZIndexes;
	}

	/**
	 * Comparator to sort layers back (low z-index) to front (high z-index)
	 * 
	 * @author Christian Clausner
	 *
	 */
	private static class LayerComparator implements Comparator<Layer> {
		@Override
		public int compare(Layer o1, Layer o2) {
			if (o1 == null || o2 == null)
				return 0;
			return Integer.valueOf(o1.getZIndex()).compareTo(o2.getZIndex());
		}
	}
}
