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
package org.primaresearch.dla.page.io.xml;

import java.io.File;
import java.net.URL;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.Page.MeasurementUnit;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.FileTarget;
import org.primaresearch.dla.page.io.UrlInput;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.shared.ContentType;
import org.primaresearch.dla.page.layout.physical.shared.LowLevelTextType;
import org.primaresearch.io.FormatModel;
import org.primaresearch.io.FormatModelSource;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.SchemaModelParser;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlValidator;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.NoSchemasException;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;
import org.primaresearch.maths.geometry.Point;
import org.primaresearch.maths.geometry.Polygon;

/**
 * Central access point for reading and writing PAGE XML.<br>
 * <br>
 * Note: Page objects can only be saved using the XML format they are set to.
 * Call Page.setFormatVersion to convert the page object to another version
 * if necessary.<br>
 * <br>
 * To validate a page object without writing a file call the validate() method
 * of a PageWriter.
 * 
 * @author Christian Clausner
 *
 */
public class PageXmlInputOutput implements FormatModelSource {

	private static PageXmlInputOutput instance = null;
	private XmlModelAndValidatorProvider validatorProvider;
	
	/**
	 * Constructor (private because this is a singleton).
	 */
	private PageXmlInputOutput() {
		try {
			validatorProvider = new PageXmlModelAndValidatorProvider(); //Provider supporting the default schemas
		} catch (NoSchemasException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the instance of the singleton.
	 */
	public static PageXmlInputOutput getInstance() {
		if (instance == null)
			instance = new PageXmlInputOutput();
		return instance;
	}
	
	/**
	 * Returns the validator provider of the singleton.
	 */
	private static XmlModelAndValidatorProvider getValidatorProvider() {
		return getInstance().validatorProvider;
	}
	
	/**
	 * Sets the validator provider of the singleton. 
	 */
	private static void setValidatorProvider(XmlModelAndValidatorProvider provider) {
		getInstance().validatorProvider = provider;
	}
	
	/**
	 * Sets the location of additional schema files and assumes the default schema file name 'pagecontent.xsd'.
	 * @param rootFolder Root of the schema folder structure containing the schema files.
	 * @throws NoSchemasException No schemas found at the given location
	 */
	public static void setAdditionalSchemaLocation(String rootFolder) throws NoSchemasException {
		setAdditionalSchemaLocation(rootFolder, "pagecontent.xsd");
	}
	
	/**
	 * Sets the location of additional schema files and searches for schemas having the specified name.
	 * 
	 * @param rootFolder Root of the schema folder structure containing the schema files
	 * @param schemaFilename Usually a filename with extension .xsd
	 * @throws NoSchemasException No schemas found at the given location
	 */
	public static void setAdditionalSchemaLocation(String rootFolder, String schemaFilename) throws NoSchemasException {
		if (rootFolder == null)
			setValidatorProvider(new PageXmlModelAndValidatorProvider());
		else
			setValidatorProvider(new PageXmlModelAndValidatorProvider(rootFolder, schemaFilename));
	}
	
	/**
	 * Creates and returns an XML writer for PAGE using the latest schema version.
	 * 
	 * @throws UnsupportedSchemaVersionException Schema file could not be found.
	 */
	public static XmlPageWriter getWriterForLastestXmlFormat(/*boolean validation*/) throws UnsupportedSchemaVersionException {
		XmlValidator validator = null;
		//if (validation) {
			XmlModelAndValidatorProvider validatorProvider = getValidatorProvider();
			if (validatorProvider != null) {
				validator = validatorProvider.getValidator(new XmlFormatVersion("2019-07-15"));
			}
		//}
		return new XmlPageWriter_2019_07_15(validator);
	}
	
	/**
	 * Creates and returns an XML writer for PAGE using the specified schema version.
	 * This might require the schema location to be set beforehand.
	 * 
	 * @throws UnsupportedSchemaVersionException The schema file could not be found.
	 */
	public static XmlPageWriter getWriter(XmlFormatVersion schemaVersion) throws UnsupportedSchemaVersionException {
		XmlValidator validator = null;

		XmlModelAndValidatorProvider validatorProvider = getValidatorProvider();
		if (validatorProvider != null) {
			validator = validatorProvider.getValidator(schemaVersion);
		}
		if (new XmlFormatVersion("2019-07-15").equals(schemaVersion))
			return new XmlPageWriter_2019_07_15(validator);

		if (new XmlFormatVersion("2018-07-15").equals(schemaVersion))
			return new XmlPageWriter_2018_07_15(validator);

		if (new XmlFormatVersion("2017-07-15").equals(schemaVersion))
			return new XmlPageWriter_2017_07_15(validator);

		if (new XmlFormatVersion("2016-07-15").equals(schemaVersion))
			return new XmlPageWriter_2016_07_15(validator);

		if (new XmlFormatVersion("2013-07-15").equals(schemaVersion))
			return new XmlPageWriter_2013_07_15(validator);
		
		//Legacy
		return new XmlPageWriter_2010_03_19(validator);
	}
	
	/**
	 * Saves the given document page to an XML file at the specified
	 * location, using the latest PAGE XML format or the format the
	 * page object has been loaded with. 
	 *   
	 * @param page Page object
	 * @param filePath Target file
	 * @throws UnsupportedSchemaVersionException Schema file could not be found.
	 */
	public static boolean writePage(Page page, String filePath/*, boolean validate*/) throws UnsupportedSchemaVersionException {
		XmlPageWriter writer = null;
		if (page.getFormatVersion() == null || !(page.getFormatVersion() instanceof XmlFormatVersion))
			writer = getWriterForLastestXmlFormat(/*validate*/);
		else {
			writer = getWriter((XmlFormatVersion)page.getFormatVersion());
		}
		try {
			return writer.write(page, new FileTarget(new File(filePath)));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Creates and returns an XML reader for PAGE.
	 */
	public static XmlPageReader getReader(/*boolean validation*/) {
		XmlModelAndValidatorProvider validatorProvider = null;
		//if (validation)
			validatorProvider = getValidatorProvider();
		return new XmlPageReader(validatorProvider);
	}
	
	/**
	 * Creates a page object from the given XML file (no validation).
	 * 
	 * @param filePath Path to PAGE XML file.
	 * @return Page object
	 */
	//public static Page readPage(String filePath) {
	//	try {
	//		return readPage(filePath, false);
	//	} catch (UnsupportedFormatVersionException e) {
	//		e.printStackTrace(); //Cannot happen
	//	}
	//	return null;
	//}
	
	/**
	 * Creates a page object from the given XML file.
	 * 
	 * @param filePath Path to PAGE XML file.
	 * @return Page object
	 * @throws UnsupportedSchemaVersionException Schema file not found
	 */
	public static Page readPage(String filePath/*, boolean validate*/) throws UnsupportedFormatVersionException {
		XmlPageReader reader = getReader(/*validate*/);
		return reader.read(new FileInput(new File(filePath)));
	}

	/**
	 * Creates a page object from the given XML file.
	 * 
	 * @param url URL of PAGE XML file.
	 * @return Page object
	 * @throws UnsupportedSchemaVersionException Schema file not found
	 */
	public static Page readPage(URL url) throws UnsupportedFormatVersionException {
		XmlPageReader reader = getReader(/*validate*/);
		return reader.read(new UrlInput(url));
	}

	/**
	 * Returns the model of the latest XML schema.
	 */
	public static SchemaModelParser getLatestSchemaModel() {
		PageXmlInputOutput instance = getInstance();
		try {
			return instance.validatorProvider.getSchemaParser(instance.validatorProvider.getLatestSchemaVersion());
		} catch (UnsupportedSchemaVersionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the model of the latest XML schema.
	 */
	public static SchemaModelParser getSchemaModel(XmlFormatVersion version) {
		PageXmlInputOutput instance = getInstance();
		try {
			return instance.validatorProvider.getSchemaParser(version);
		} catch (UnsupportedSchemaVersionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FormatModel getFormatModel(FormatVersion version) throws UnsupportedFormatVersionException {
		PageXmlInputOutput instance = getInstance();
		return instance.validatorProvider.getSchemaParser((XmlFormatVersion)version);
	}
	
	/**
	 * Post-processes the given page object using the specified image dimension and resolution.<br>
	 * This could include scaling all coordinates if they are not measured in pixel. 
	 * @param page Page object to post-process
	 * @param imageWidth Width of the document image
	 * @param imageHeight Height of the document image
	 * @param dpiHor X resolution of the document image
	 * @param dpiVert Y resolution of the document image
	 */
	public static void postProcessPage(Page page, int imageWidth, int imageHeight, double dpiHor, double dpiVert) {
		if (page == null || page.getLayout() == null || MeasurementUnit.PIXEL.equals(page.getMeasurementUnit()))
			return;
		
		PageLayout layout = page.getLayout();
		
		double scaleX = 1.0;
		double scaleY = 1.0;
		
		double conversion = page.getMeasurementUnit().getDiscreteValuesPerInch();
		
		if (conversion == 0.0)
			return;
		
		//If the image dimensions don't equal the document dimensions, we can asume that the 
		//document width and height are using the measurement unit as well. In that case, we
		//can calculate the scaling factor from the size difference.
		if (layout.getWidth() != imageWidth && layout.getHeight() != imageHeight) {
			//Sanity check: If the page dimensions are in pixel, even though the
			//			    measurement unit is not pixel, we need shouldn't use them.

			//Go through all regions a see if they are within the page bounds
			boolean ok = true;
			for (ContentIterator it=layout.iterator(null); it.hasNext(); ) {
				Polygon polygon = it.next().getCoords();
				if (polygon != null) {
					for (int i=0; i<polygon.getSize(); i++) {
						Point p = polygon.getPoint(i);
						if (p.x > layout.getWidth() || p.y > layout.getHeight()) {
							ok = false;
							break;
						}
					}
				}
			}
			
			if (ok) {
				scaleX = (double)imageWidth / (double)layout.getWidth();
				scaleY = (double)imageHeight / (double)layout.getHeight();
			}
		}
		
		//Use the image resolution to calculate the scaling factor
		if (scaleX == 1.0 && scaleY == 1.0) 
		{
			scaleX = dpiHor / conversion; 
			scaleY = dpiVert / conversion;
		}

		if (scaleX == 0 || scaleY == 0)
			return;	
		
		//Now scale all coordinates
		// Document size
		layout.setSize(imageWidth, imageHeight);
		//Region, lines, words, glyphs
		ContentType types[] = new ContentType[]{null, LowLevelTextType.TextLine, LowLevelTextType.Word, LowLevelTextType.Glyph};
		for (ContentType tp : types) {
			for (ContentIterator it=layout.iterator(tp); it.hasNext(); ) {
				scalePolygon(it.next().getCoords(), scaleX, scaleY);
			}
		}
		//Border, print space
		if (layout.getBorder() != null)
			scalePolygon(layout.getBorder().getCoords(), scaleX, scaleY);
		if (layout.getPrintSpace() != null)
			scalePolygon(layout.getPrintSpace().getCoords(), scaleX, scaleY);
	}
	
	/**
	 * Scales all points of the given polygon
	 * @param polygon Polygon with 2D points
	 * @param scaleX Multiplier for x coordinates
	 * @param scaleY Multiplier for y coordinates
	 */
	private static void scalePolygon(Polygon polygon, double scaleX, double scaleY) {
		if (polygon == null)
			return;
		for (int i=0; i<polygon.getSize(); i++) {
			Point p = polygon.getPoint(i);
			p.x = (int)((double)p.x * scaleX + 0.5) ;
			p.y = (int)((double)p.y * scaleY + 0.5);
		}
		polygon.setBoundingBoxOutdated();
	}

}
