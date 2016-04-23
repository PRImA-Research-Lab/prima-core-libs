/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.FileTarget;
import org.primaresearch.dla.page.io.PageReader;
import org.primaresearch.dla.page.io.PageWriter;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageWriter;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.DefaultTextRegionTypes;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.NoSchemasException;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;

public class XmlPageWriterTest {
	private static File xmlAllFeaturesFile = null;
	private static File xmlAllFeaturesPageFileMod = null;
	private static File xmlPageFile = null;
	private static File xmlPage2013File = null;
	private static File legacyPageFile = null;
	private static File emptyDocumentOutputFile = null;
	private static File allFeaturesOutputFile = null;
	private static File outputFileMod = null;
	private static File pageOutputFile = null;
	private static File page2013OutputFile = null;
	private static File legagyOutputFile = null;
	private static File fromScratchLegacyOutputFile = null;
	private static File fromScratchOutputFile = null;
	private static Page pageAllFeatures = null;
	private static Page pageMod = null;
	private static Page page = null;
	private static Page page2013 = null;
	private static Page legacyPage = null;

	@BeforeClass
	public static void setUp() throws Exception {
		//Empty document features file
		emptyDocumentOutputFile = new File("c:/junit/emptyDocumentOutput.xml");
		if (emptyDocumentOutputFile.exists()) {
			if (!emptyDocumentOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ emptyDocumentOutputFile.getPath());
		}

		//All features file
		allFeaturesOutputFile = new File("c:/junit/allFeaturesOutput.xml");
		if (allFeaturesOutputFile.exists()) {
			if (!allFeaturesOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ allFeaturesOutputFile.getPath());
		}

		xmlAllFeaturesFile = new File("c:/junit/allFeatures.xml");
		if (!xmlAllFeaturesFile.exists())
			throw new Exception("Page XML file not found: "+ xmlAllFeaturesFile.getPath());
		
		PageReader reader = PageXmlInputOutput.getReader();
		pageAllFeatures = reader.read(new FileInput(xmlAllFeaturesFile));
		if (pageAllFeatures == null)
			throw new Exception("Page XML could not be opened: "+ xmlAllFeaturesFile.getPath());

		//Normal page file
		pageOutputFile = new File("c:/junit/pageOutput.xml");
		if (pageOutputFile.exists()) {
			if (!pageOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ pageOutputFile.getPath());
		}

		xmlPageFile = new File("c:/junit/page.xml");
		if (!xmlPageFile.exists())
			throw new Exception("Page XML file not found: "+ xmlPageFile.getPath());
		
		page = reader.read(new FileInput(xmlPageFile));
		if (page == null)
			throw new Exception("Page XML could not be opened: "+ xmlPageFile.getPath());

		//2013 schema page file (new features of 2013 schema)
		page2013OutputFile = new File("c:/junit/page_2013-07-15_Output.xml");
		if (page2013OutputFile.exists()) {
			if (!page2013OutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ page2013OutputFile.getPath());
		}

		xmlPage2013File = new File("c:/junit/page_2013-07-15.xml");
		if (!xmlPage2013File.exists())
			throw new Exception("Page XML file not found: "+ xmlPage2013File.getPath());
		
		page2013 = reader.read(new FileInput(xmlPage2013File));
		if (page2013 == null)
			throw new Exception("Page XML could not be opened: "+ xmlPage2013File.getPath());

		//Modified schema
		outputFileMod = new File("c:/junit/pageModOutput.xml");
		if (outputFileMod.exists()) {
			if (!outputFileMod.delete())
				throw new Exception("Old output file could not be deleted: "+ outputFileMod.getPath());
		}

		xmlAllFeaturesPageFileMod = new File("c:/junit/pageMod.xml");
		if (!xmlAllFeaturesPageFileMod.exists())
			throw new Exception("Page XML file not found: "+ xmlAllFeaturesPageFileMod.getPath());
		
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation("c:/junit/schemamod");
		} catch (NoSchemasException e) {
			fail(e.getMessage());
		}
		reader = PageXmlInputOutput.getReader();
		pageMod = reader.read(new FileInput(xmlAllFeaturesPageFileMod));
		if (pageMod == null)
			throw new Exception("Page XML could not be opened: "+ xmlAllFeaturesPageFileMod.getPath());

		//Legacy schema
		legagyOutputFile = new File("c:/junit/legacyPageOutput.xml");
		if (legagyOutputFile.exists()) {
			if (!legagyOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ legagyOutputFile.getPath());
		}

		legacyPageFile = new File("c:/junit/legacyPage.xml");
		if (!legacyPageFile.exists())
			throw new Exception("Page XML file not found: "+ legacyPageFile.getPath());
		
		reader = PageXmlInputOutput.getReader();
		legacyPage = reader.read(new FileInput(legacyPageFile));
		if (legacyPage == null)
			throw new Exception("Page XML could not be opened: "+ legacyPageFile.getPath());

		//Legacy schema for newly created page object
		fromScratchLegacyOutputFile = new File("c:/junit/fromScratchLegacyPageOutput.xml");
		if (fromScratchLegacyOutputFile.exists()) {
			if (!fromScratchLegacyOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ fromScratchLegacyOutputFile.getPath());
		}
		
		//From scratch
		fromScratchOutputFile = new File("c:/junit/fromScratchPageOutput.xml");
		if (fromScratchOutputFile.exists()) {
			if (!fromScratchOutputFile.delete())
				throw new Exception("Old output file could not be deleted: "+ fromScratchOutputFile.getPath());
		}
	}

	@Test
	public void testEmptyDocument() {
		try {
			//Valid
			PageWriter writer;
			try {
				writer = PageXmlInputOutput.getWriterForLastestXmlFormat();
			} catch (UnsupportedSchemaVersionException e) {
				fail(e.getMessage());
				return;
			}
			
			Page page = new Page();
	
			try {
				writer.write(page, new FileTarget(emptyDocumentOutputFile));
			} catch (UnsupportedFormatVersionException e) {
				e.printStackTrace();
			} 
			assertTrue(emptyDocumentOutputFile.exists());
			
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	@Test
	public void testWrite() {
		try {
			//Valid
			PageWriter writer;
			try {
				writer = PageXmlInputOutput.getWriterForLastestXmlFormat();
			} catch (UnsupportedSchemaVersionException e) {
				fail(e.getMessage());
				return;
			}
	
			try {
				writer.write(pageAllFeatures, new FileTarget(allFeaturesOutputFile));
			} catch (UnsupportedFormatVersionException e) {
				e.printStackTrace();
			} 
			assertTrue(allFeaturesOutputFile.exists());
			
			//File with new features of 2013 schema
			try {
				writer.write(page2013, new FileTarget(page2013OutputFile));
			} catch (UnsupportedFormatVersionException e) {
				e.printStackTrace();
			}
			assertTrue(page2013OutputFile.exists());
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	@Test
	public void testValidate() {
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation("c:/junit/schema");
		} catch (NoSchemasException e) {
			fail(e.getMessage());
		}

		//Valid
		PageWriter writer;
		try {
			writer = PageXmlInputOutput.getWriterForLastestXmlFormat();
		} catch (UnsupportedSchemaVersionException e) {
			fail(e.getMessage());
			return;
		}
		try {
			assertTrue(writer.write(pageAllFeatures, new FileTarget(allFeaturesOutputFile)));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}

		//Valid2
		try {
			assertTrue(writer.write(page, new FileTarget(pageOutputFile)));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}

		//Invalid
		// Add an invalid attribute to a region
		pageAllFeatures.getLayout().getRegion(0).getAttributes().add(new StringVariable("kerning", new StringValue("not a number")));
		// Save and validate
		try {
			assertFalse(writer.write(pageAllFeatures, new FileTarget(allFeaturesOutputFile)));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		// Validate only
		try {
			assertFalse(writer.validate(pageAllFeatures));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testWriteLegacyFromScratch() {
		//Reset schemas
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation(null, null);
		} catch (NoSchemasException e1) {
			e1.printStackTrace();
		}
		
		Page pageFromScratch = new Page();
		
		//Try to write without conversion
		PageWriter writer;
		try {
			writer = PageXmlInputOutput.getWriter(new XmlFormatVersion("2009-03-16"));
		} catch (UnsupportedSchemaVersionException e) {
			fail(e.getMessage());
			return;
		}
		try {
			writer.write(pageFromScratch, new FileTarget(fromScratchLegacyOutputFile));
			fail("Exception was expected");
		} catch (UnsupportedFormatVersionException e) {
			//Expected
		}
		
		//Convert and try to save again
		try {
			pageFromScratch.setFormatVersion(PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2009-03-16")));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		try {
			writer.write(pageFromScratch, new FileTarget(fromScratchLegacyOutputFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertTrue(fromScratchLegacyOutputFile.exists());
	}

	@Test
	public void testWriteFromScratch() {

		try {
			//Create page and meta data
			Page page = new Page();
			try {
				page.setGtsId("pc-00001234");
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
			page.setImageFilename("00001234.tif");
			
			MetaData metadata = page.getMetaData();
			metadata.setCreator("Test class");
			
			//Create page layout
			PageLayout layout = page.getLayout();
			layout.setSize(1000, 2000);
			
			// Image
			Region region = layout.createRegion(RegionType.ImageRegion);
			Polygon outline = new Polygon();
			outline.addPoint(10, 20);
			outline.addPoint(110, 20);
			outline.addPoint(110, 120);
			outline.addPoint(10, 120);
			region.setCoords(outline);
			
			// Headline
			region = layout.createRegion(RegionType.TextRegion);
			outline = new Polygon();
			outline.addPoint(10, 220);
			outline.addPoint(110, 220);
			outline.addPoint(110, 320);
			outline.addPoint(10, 320);
			region.setCoords(outline);
			TextRegion textRegion = (TextRegion)region;
			textRegion.setTextType(DefaultTextRegionTypes.HEADING);
			textRegion.setText("This is the headline");
			
			// Text paragraph
			region = layout.createRegion(RegionType.TextRegion);
			outline = new Polygon();
			outline.addPoint(10, 350);
			outline.addPoint(110, 350);
			outline.addPoint(110, 520);
			outline.addPoint(10, 520);
			region.setCoords(outline);
			textRegion = (TextRegion)region;
			textRegion.setTextType(DefaultTextRegionTypes.PARAGRAPH);
			textRegion.setText("This is a paragraph.");
		
			//Save
			try {
				boolean success = PageXmlInputOutput.writePage(page, fromScratchOutputFile.getPath());
				assertTrue(success);
			} catch (UnsupportedSchemaVersionException e) {
				fail(e.getMessage());
				return;
			}
			
			assertTrue(fromScratchOutputFile.exists());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests writing a file using a modified schema (a new attribute has been added).
	 */
	@Test
	public void testValidateWithModifiedSchema() {
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation("c:/junit/schemamod");
		} catch (NoSchemasException e) {
			fail(e.getMessage());
		}
		
		//PageWriter writer;
		//try {
		//	writer = XmlInputOutput.getWriterForLastestXmlFormat(true);
		//} catch (UnsupportedSchemaVersionException e) {
		//	fail(e.getMessage());
		//	return;
		//}
		try {
			XmlPageWriter writer = PageXmlInputOutput.getWriter(new XmlFormatVersion("2012-03-06"));
			assertTrue(writer.write(pageMod, new FileTarget(outputFileMod)));
			//assertTrue(XmlInputOutput.writePage(pageMod, outputFileMod.getPath()));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		//assertTrue(writer.write(pageMod, new FileTarget(outputFileMod)));
	}
	
	@Test
	public void testWriteLegacy() {
		PageWriter writer;
		try {
			writer = PageXmlInputOutput.getWriter((XmlFormatVersion) legacyPage.getFormatVersion());
		} catch (UnsupportedSchemaVersionException e) {
			fail(e.getMessage());
			return;
		}
		try {
			writer.write(legacyPage, new FileTarget(legagyOutputFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertTrue(legagyOutputFile.exists());
	}



}
