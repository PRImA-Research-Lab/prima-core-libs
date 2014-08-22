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
package org.primaresearch.dla.page.io.xml.sax;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.FileTarget;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.dla.page.io.xml.XmlPageWriter;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;

public class SaxPageHandler_AltoTest {

	File alto20File;

	@Before
	public void setUp() throws Exception {
		alto20File = new File("c:/junit/altotest_2_0.xml");
		if (!alto20File.exists())
			throw new Exception("ALTO file not found: "+ alto20File.getPath());
	}

	@Test
	public void testAlto20() {
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(alto20File));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		assertNotNull(page);
		
		//Save as PAGE XML
		try {
			XmlPageWriter writer = PageXmlInputOutput.getWriterForLastestXmlFormat();
			boolean success = writer.write(page, new FileTarget(new File("c:/junit/alto20ConvertedToPAGE.xml")));
			assertTrue(success);
		} catch (UnsupportedSchemaVersionException e) {
			e.printStackTrace();
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
	}

}
