/**
 * File: $HeadURL: https://hdt-java.googlecode.com/svn/trunk/hdt-java/src/org/rdfhdt/hdt/dictionary/impl/section/DictionarySectionFactory.java $
 * Revision: $Rev: 194 $
 * Last modified: $Date: 2013-03-04 21:30:01 +0000 (lun, 04 mar 2013) $
 * Last modified by: $Author: mario.arias $
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contacting the authors:
 *   Mario Arias:               mario.arias@deri.org
 *   Javier D. Fernandez:       jfergar@infor.uva.es
 *   Miguel A. Martinez-Prieto: migumar2@infor.uva.es
 *   Alejandro Andres:          fuzzy.alej@gmail.com
 */

package org.rdfhdt.hdt.dictionary.impl.section;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.rdfhdt.hdt.dictionary.DictionarySectionPrivate;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.rdfhdt.hdt.util.io.CountInputStream;

/**
 * @author mario.arias
 *
 */
public class DictionarySectionFactory {
	
	private DictionarySectionFactory() {}
	
	public static DictionarySectionPrivate loadFrom(InputStream input, ProgressListener listener) throws IOException {
		if(!input.markSupported()) {
			throw new IllegalArgumentException("Need support for mark()/reset(). Please wrap the InputStream with a BufferedInputStream");
		}
		input.mark(64);
		int dictType = input.read();
		input.reset();
		input.mark(64);		// To allow children to reset() and try another instance.
		
		DictionarySectionPrivate section;
		
		switch(dictType) {
		case PFCDictionarySection.TYPE_INDEX:
			try{
				// First try load using the standard PFC 
				section = new PFCDictionarySection(new HDTSpecification());
				section.load(input, listener);
			} catch (IllegalArgumentException e) {
				// The PFC Could not load the file because it is too big, use PFCBig
				section = new PFCDictionarySectionBig(new HDTSpecification());
				section.load(input, listener);
			}
			return section;
		default:
			throw new IOException("DictionarySection implementation not available for id "+dictType);
		}
	}
	
	public static DictionarySectionPrivate loadFrom(CountInputStream input, File f, ProgressListener listener) throws IOException {
		input.mark(64);
		int dictType = input.read();
		input.reset();
		input.mark(64);		// To allow children to reset() and try another instance.
		
		switch(dictType) {
		case PFCDictionarySection.TYPE_INDEX:
				// First try load using the standard PFC 
			DictionarySectionPrivate section= new PFCDictionarySectionMap(input, f);

			return section;
		default:
			throw new IOException("DictionarySection implementation not available for id "+dictType);
		}
	}
}
