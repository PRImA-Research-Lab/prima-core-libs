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
package org.primaresearch.dla.page.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.primaresearch.dla.page.io.xml.PageErrorHandler;
import org.primaresearch.io.xml.IOError;

public class PageReaderBase {

	protected PageErrorHandler lastErrors;

	protected InputStream getInputStream(InputSource source) {
		if (source instanceof FileInput) {
			File f = ((FileInput)source).getFile();
			try {
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				lastErrors.getErrors().add(new IOError("Could not open stream from file: "+e.getMessage()));
			} 
		} 
		else if (source instanceof UrlInput) {
			try {
				return ((UrlInput)source).getUrl().openStream();
			} catch (IOException e) {
				e.printStackTrace();
				lastErrors.getErrors().add(new IOError("Could not open stream from URL: "+e.getMessage()));
			}
		}
		else 
			throw new IllegalArgumentException("Only FileInput and UrlInput allowed for XmlPageReader");
		return null;
	}
}
