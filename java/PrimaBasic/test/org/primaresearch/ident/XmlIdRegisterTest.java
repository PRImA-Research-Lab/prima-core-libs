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
package org.primaresearch.ident;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.ident.IdRegister.InvalidIdException;

public class XmlIdRegisterTest {

	@Test
	public void test() {
		try{
			XmlIdRegister register = new XmlIdRegister("pre");
			
			//Generate and register ID with default prefix
			Id id1 = register.generateId();
			assertNotNull(id1);
			assertTrue(id1.toString().startsWith("pre"));
			try {
				register.registerId(id1);
			} catch (InvalidIdException e1) {
				e1.printStackTrace();
				fail();
			}
			
			//Generate and register ID with custom prefix
			Id id2 = register.generateId("bla");
			assertNotNull(id2);
			assertTrue(id2.toString().startsWith("bla"));
			try {
				register.registerId(id2);
			} catch (InvalidIdException e1) {
				e1.printStackTrace();
				fail();
			}
			
			//Try to register existing ID
			try {
				register.registerId(register.getId(id1.toString()));
				fail("Exception expected");
			} catch (InvalidIdException e) {
			}
	
			//Register unused IDs 
			try {
				register.registerId(register.getId("pre2"));
				register.registerId("pre3");
			} catch (InvalidIdException e) {
				e.printStackTrace();
				fail();
			}
			
			//Register and unregister
			try {
				register.registerId("pre4", id1);
			} catch (InvalidIdException e) {
				e.printStackTrace();
				fail();
			}
			
			//Register or create (ID exists already)
			Id id3 = register.registerOrCreateNewId("pre4");
			assertNotNull(id3);
			assertFalse(id3.toString().equals("pre4"));
			
			//Register or create (ID does not exist already)
			Id id4 = register.registerOrCreateNewId("blub1");
			assertNotNull(id4);
			assertTrue(id4.toString().equals("blub1"));
	
			//Unregister
			register.unregisterId(id4);
			Id id5 = register.registerOrCreateNewId("blub1");
			assertNotNull(id5);
			assertTrue(id5.toString().equals("blub1"));
			
			//Invalid ID format
			try {
				register.registerId("123");
				fail("Exception expected");
			} catch (InvalidIdException e) {
			}
		} catch(Exception exc) {
			exc.printStackTrace();
			fail();
		}
	}

}
