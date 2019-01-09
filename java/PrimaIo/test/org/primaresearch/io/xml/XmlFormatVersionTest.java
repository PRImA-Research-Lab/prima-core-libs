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
package org.primaresearch.io.xml;

import static org.junit.Assert.*;

import org.junit.Test;

public class XmlFormatVersionTest {

	@Test
	public void testEqualsObject() {
		assertTrue(new XmlFormatVersion("2010-03-19").equals(new XmlFormatVersion("2010-03-19")));
		assertFalse(new XmlFormatVersion("2010-03-19").equals(new XmlFormatVersion("2010-01-12")));
	}

	@Test
	public void testIsNewerThan() {
		assertTrue(new XmlFormatVersion("2010-03-19").isNewerThan(new XmlFormatVersion("2010-01-12")));
		assertFalse(new XmlFormatVersion("2010-01-12").isNewerThan(new XmlFormatVersion("2010-03-19")));
	}

	@Test
	public void testIsOlderThan() {
		assertTrue(new XmlFormatVersion("2010-01-12").isOlderThan(new XmlFormatVersion("2010-03-19")));
		assertFalse(new XmlFormatVersion("2010-03-19").isOlderThan(new XmlFormatVersion("2010-01-12")));
	}

}
