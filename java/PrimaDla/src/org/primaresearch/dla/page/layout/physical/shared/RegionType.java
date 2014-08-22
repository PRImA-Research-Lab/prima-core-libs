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
package org.primaresearch.dla.page.layout.physical.shared;

/**
 * Content types for layout regions.
 * 
 * @author Christian Clausner
 */
public class RegionType extends ContentType {

	private static final long serialVersionUID = 1L;
	/** For text regions */
	public static final RegionType TextRegion 			= new RegionType("TextRegion");
	/** For image regions */
	public static final RegionType ImageRegion 			= new RegionType("ImageRegion");
	/** For graphics */
	public static final RegionType GraphicRegion 		= new RegionType("GraphicRegion");
	/** For line drawings */
	public static final RegionType LineDrawingRegion 	= new RegionType("LineDrawingRegion");
	/** For charts */
	public static final RegionType ChartRegion 			= new RegionType("ChartRegion");
	/** For tables */
	public static final RegionType TableRegion 			= new RegionType("TableRegion");
	/** For mathematical formulas/equations */
	public static final RegionType MathsRegion 			= new RegionType("MathsRegion");
	/** For separators */
	public static final RegionType SeparatorRegion 		= new RegionType("SeparatorRegion");
	/** For advertisements */
	public static final RegionType AdvertRegion 		= new RegionType("AdvertRegion");
	/** For chemical notations */
	public static final RegionType ChemRegion 			= new RegionType("ChemRegion");
	/** For musical notations */
	public static final RegionType MusicRegion 			= new RegionType("MusicRegion");
	/** For noise on the page */
	public static final RegionType NoiseRegion 			= new RegionType("NoiseRegion");
	/** For regions of unknown/undetermined type */
	public static final RegionType UnknownRegion 		= new RegionType("UnknownRegion");

	/**
	 * Empty constructor (required for GWT)
	 */
	protected RegionType() {
		super();
	}
	
	/**
	 * Constructor
	 * @param name Type name
	 */
	protected RegionType(String name) {
		super(name);
	}
}
