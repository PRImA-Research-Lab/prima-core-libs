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
package org.primaresearch.dla.page.io.xml.sax;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.io.UnsupportedFormatVersionException;

public class SaxPageHandlerLegacyTest {

	File legacyPageFile;

	@Before
	public void setUp() throws Exception {
		legacyPageFile = new File("c:/junit/legacypage.xml");
		if (!legacyPageFile.exists())
			throw new Exception("Page XML file not found: "+ legacyPageFile.getPath());
	}
	
	@Test
	public void testGetPageObject() {
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(legacyPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		assertNotNull(page);
		
		//Reading order
		assertNotNull(page.getLayout().getReadingOrder());
		ReadingOrder order = page.getLayout().getReadingOrder();
		
		assertEquals(2, order.getRoot().getSize());
		assertTrue(order.getRoot().isOrdered());
	}

}
