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
package org.primaresearch.dla.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.converter.ConversionMessage;
import org.primaresearch.dla.page.layout.converter.ConverterHub;
import org.primaresearch.dla.page.layout.physical.AttributeContainer;
import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.ContentFactory;
import org.primaresearch.dla.page.layout.physical.DefaultAttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.metadata.MetaData;
import org.primaresearch.ident.Id;
import org.primaresearch.ident.IdRegister;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.ident.XmlIdRegister;
import org.primaresearch.io.FormatModel;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.labels.HasLabels;
import org.primaresearch.labels.Labels;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Central class representing one page of a document (e.g. a book page).
 * 
 * @author Christian Clausner
 */
public class Page implements AttributeContainer, HasLabels, Serializable {

	private static final long serialVersionUID = 1L;
	transient private PageLayout layout;
	transient private ContentFactory contentFactory;
	transient private IdRegister idRegister;
	transient private MetaData metaData;
	private String imageFilename;
	transient private Id gtsId = null;
	transient private FormatVersion formatVersion = null;

	private VariableMap attributes;
	
	private VariableMap userDefinedAttributes = null;
	
	transient private Labels labels = null;
	
	transient private List<AlternativeImage> alternativeImages;
	
	transient private MeasurementUnit measurementUnit = MeasurementUnit.PIXEL;


	/**
	 * Returns the version of the page format.
	 * @return Version object
	 */
	public FormatVersion getFormatVersion() {
		return formatVersion;
	}

	/**
	 * Converts the page to the specified format.
	 * Note that this might change the page layout.
	 */
	public List<ConversionMessage> setFormatVersion(FormatModel formatModel) {
		return setFormatVersion(formatModel, true);
	}

	/**
	 * Sets the format to the specified version.
	 * Note that this might change the page layout.
	 * @param convert Run conversion
	 * @return Messages (if converting) or null
	 */
	public List<ConversionMessage> setFormatVersion(FormatModel formatModel, boolean convert) {
		contentFactory.setAttributeFactory(createAttributeFactory(formatModel));
		
		List<ConversionMessage> ret = null;
		if (convert)
			ret = ConverterHub.convert(this, formatModel);
		this.formatVersion = formatModel.getVersion();
		return ret;
	}

	private AttributeFactory createAttributeFactory(FormatModel formatModel) {
		AttributeFactory attribFactory = null;
		if (formatModel != null) {
			attribFactory = new DefaultAttributeFactory(formatModel);
		} else {
			attribFactory = new DefaultAttributeFactory();
		}		
		return attribFactory;
	}

	/**
	 * Returns the main image file that is associated with this page.
	 * @return Filename
	 */
	public String getImageFilename() {
		return imageFilename;
	}

	/**
	 * Sets the main image file that is associated with this page.
	 * @param imageFilename Filename (without path)
	 */
	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	/**
	 * Constructor using the default page format. 
	 */
	public Page() {
		this(PageXmlInputOutput.getLatestSchemaModel());
	}

	/**
	 * Constructor using dynamic page format. 
	 * @param formatModel Model for dynamic format
	 */
	public Page(FormatModel formatModel) {
		this.idRegister = new XmlIdRegister();
		this.formatVersion = formatModel.getVersion();
		AttributeFactory attrFactory = createAttributeFactory(formatModel);
		contentFactory = new ContentFactory(idRegister, attrFactory);
		layout = new PageLayout(contentFactory);
		metaData = new MetaData(contentFactory);
		attributes = attrFactory.createAttributes(ContentType.Page);
	}

	/**
	 * Returns the page layout
	 * @return Layout object
	 */
	public PageLayout getLayout() {
		return layout;
	}
	
	/**
	 * Returns the page metadata
	 * @return Metadata object
	 */
	public MetaData getMetaData() {
		return metaData;
	}
	
	/**
	 * Returns the ground truth and storage ID of this page
	 * @return ID object
	 */
	public Id getGtsId() {
		return gtsId;
	}

	/**
	 * Sets the ground truth and storage ID of this page
	 * @param gtsId ID object
	 * @throws InvalidIdException ID is being used already (must be unique)
	 */
	public void setGtsId(Id gtsId) throws InvalidIdException {
		idRegister.registerId(gtsId, this.gtsId);
		this.gtsId = gtsId;
	}

	/**
	 * Sets the ground truth and storage ID of this page
	 * @param gtsId ID text
	 * @throws InvalidIdException ID is being used already (must be unique) or the format is invalid
	 */
	public void setGtsId(String gtsId) throws InvalidIdException {
		this.gtsId = idRegister.registerId(gtsId, this.gtsId);
	}

	@Override
	public VariableMap getAttributes() {
		return attributes;
	}
	
	/**
	 * User-defined attributes (text, int, decimal or boolean)
	 * @param createIfNotExists Set to true if to create an empty variable map if none exists yet.
	 * @return Variable map or <code>null</code>
	 */
	public VariableMap getUserDefinedAttributes(boolean createIfNotExists) {
		if (userDefinedAttributes == null && createIfNotExists)
			userDefinedAttributes = new VariableMap();
		return userDefinedAttributes;
	}
	
	/**
	 *  User-defined attributes (text, int, decimal or boolean)
	 * @param attrs Variable map
	 */
	public void setUserDefinedAttributes(VariableMap attrs) {
		userDefinedAttributes = attrs;
	}
	
	/**
	 * Returns a list of alternative images that are associated with this page (e.g. bilevel/bitonal/black-and-white image)
	 * @return List with image objects
	 */
	public List<AlternativeImage> getAlternativeImages() {
		if (alternativeImages == null)
			alternativeImages = new ArrayList<AlternativeImage>();
		return alternativeImages;
	}



	/**
	 * Returns the measurement unit for coordinates
	 * @return Current unit
	 */
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}

	/**
	 * Sets the measurement unit for coordinates
	 * @param unit Unit object
	 */
	public void setMeasurementUnit(MeasurementUnit unit) {
		this.measurementUnit = unit;
	}
		


	/**
	 * Measurement unit for coordinates.<br>
	 * Introduced to support ALTO XML files. Use <code>XmlInputOutput.postProcessPage(...)</code>
	 * to scale all coordinates using the image information.
	 */
	public static class MeasurementUnit {
		public static final MeasurementUnit PIXEL = new MeasurementUnit("pixel", 0.0);
		/** One tenth of a mm */
		public static final MeasurementUnit MM_BY_10 = new MeasurementUnit("mm10", 254.0);
		/** 1200th of an inch */
		public static final MeasurementUnit INCH_BY_1200 = new MeasurementUnit("inch1200", 1200.0);

		private String name;
		private double discreteValuesPerInch;
	
		public MeasurementUnit(String name, double discreteValuesPerInch) {
			this.name = name;
			this.discreteValuesPerInch = discreteValuesPerInch;
		}

		public double getDiscreteValuesPerInch() { 
			return discreteValuesPerInch; 
		} 
		
		public String getName() { 
			return name; 
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof MeasurementUnit))
				return false;
			return name.equals(((MeasurementUnit)other).getName());
		}
	}



	@Override
	public Labels getLabels() {
		return labels;
	}

	@Override
	public void setLabels(Labels labels) {
		this.labels = labels;		
	}
}
