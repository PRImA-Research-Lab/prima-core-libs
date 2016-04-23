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
package org.primaresearch.io;

/**
 * Interface for objects having a format model
 * @author Christian Clausner
 *
 */
public interface FormatModelSource {

	/**
	 * Returns the model of this object for a given version
	 * @param version Model version
	 * @return Model object
	 * @throws UnsupportedFormatVersionException This object doesn't support the given model version
	 */
	public FormatModel getFormatModel(FormatVersion version) throws UnsupportedFormatVersionException;
}
