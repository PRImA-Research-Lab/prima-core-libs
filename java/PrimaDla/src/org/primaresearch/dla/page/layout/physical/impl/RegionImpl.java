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
package org.primaresearch.dla.page.layout.physical.impl;

import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.RegionContainer;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Basic implementation for layout regions.
 * 
 * @author Christian Clausner
 *
 */
public abstract class RegionImpl implements Region {

	private RegionType type;
	private Id id;
	private IdRegister idRegister;

	private Polygon coords;

	private VariableMap attributes;

	private RegionContainer parentRegion;
	
	private List<Region> nestedRegions = new ArrayList<Region>(); 
	
	/**
	 * Constructor
	 * @param idRegister ID register (for creating child objects)
	 * @param type Region type
	 * @param id Region ID
	 * @param coords Region outline
	 * @param attributes Region attributes
	 * @param parentRegion (optional) Parent region
	 */
	protected RegionImpl(IdRegister idRegister, RegionType type, Id id, Polygon coords, VariableMap attributes, RegionContainer parentRegion) {
		this.id = id;
		this.idRegister = idRegister;
		this.coords = coords;
		this.attributes = attributes;
		this.parentRegion = parentRegion;
		this.type = type;
	}

	@Override
	public ContentType getType() {
		return type;
	}

	@Override
	public boolean hasRegions() {
		return !nestedRegions.isEmpty();
	}

	@Override
	public int getRegionCount() {
		return nestedRegions.size();
	}

	@Override
	public Region getRegion(int index) {
		return nestedRegions.get(index);
	}

	@Override
	public RegionContainer getParentRegion() {
		return parentRegion;
	}

	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	@Override
	public Polygon getCoords() {
		return coords;
	}

	@Override
	public void setCoords(Polygon coords) {
		this.coords = coords;
	}

	@Override
	public Id getId() {
		return id;
	}

	@Override
	public IdRegister getIdRegister() {
		return idRegister;
	}

	@Override
	public void setId(String id) throws InvalidIdException {
		this.id = idRegister.registerId(id, this.id);
	}
	
	@Override
	public void setId(Id id) throws InvalidIdException {
		idRegister.registerId(id, this.id);
		this.id = id;
	}

	@Override
	public boolean isTemporary() {
		return this.getId().toString().equals(TEMP_ID_SUFFIX);
	}

	@Override
	public void addRegion(Region region) {
		nestedRegions.add(region);
	}

	@Override
	public void removeRegion(Region region) {
		nestedRegions.remove(region);
	}

}
