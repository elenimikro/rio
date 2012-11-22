/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package org.coode.utils.owl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class ManchesterSyntaxRenderer implements OWLObjectRenderer {
	private ManchesterOWLSyntaxObjectRenderer ren;
	private WriterDelegate writerDelegate;

	public ManchesterSyntaxRenderer() {
		this.writerDelegate = new WriterDelegate();
		this.ren = new ManchesterOWLSyntaxObjectRenderer(this.writerDelegate,
				new SimpleShortFormProvider());
		this.ren.setUseWrapping(false);
	}

	@Override
    public synchronized String render(OWLObject object) {
		this.writerDelegate.reset();
		object.accept(this.ren);
		return this.writerDelegate.toString();
	}

	@Override
    public synchronized void setShortFormProvider(ShortFormProvider shortFormProvider) {
		this.ren = new ManchesterOWLSyntaxObjectRenderer(this.writerDelegate,
				shortFormProvider);
	}

	private static class WriterDelegate extends Writer {
		private StringWriter delegate;

		public WriterDelegate() {
			// TODO Auto-generated constructor stub
		}

		private void reset() {
			this.delegate = new StringWriter();
		}

		@Override
		public String toString() {
			return this.delegate.getBuffer().toString();
		}

		@Override
		public void close() throws IOException {
			this.delegate.close();
		}

		@Override
		public void flush() throws IOException {
			this.delegate.flush();
		}

		@Override
		public void write(char cbuf[], int off, int len) throws IOException {
			this.delegate.write(cbuf, off, len);
		}
	}
}
