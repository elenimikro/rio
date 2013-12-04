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

/** @author Eleni Mikroyannidi */
public class ManchesterSyntaxRenderer implements OWLObjectRenderer {
    private ManchesterOWLSyntaxObjectRenderer ren;
    private WriterDelegate writerDelegate;

    public ManchesterSyntaxRenderer() {
        writerDelegate = new WriterDelegate();
        ren = new ManchesterOWLSyntaxObjectRenderer(writerDelegate,
                new SimpleShortFormProvider());
        ren.setUseWrapping(false);
    }

    @Override
    public synchronized String render(OWLObject object) {
        writerDelegate.reset();
        object.accept(ren);
        return writerDelegate.toString();
    }

    @Override
    public synchronized void setShortFormProvider(ShortFormProvider shortFormProvider) {
        ren = new ManchesterOWLSyntaxObjectRenderer(writerDelegate, shortFormProvider);
    }

    private static class WriterDelegate extends Writer {
        private StringWriter delegate;

        public WriterDelegate() {
            // TODO Auto-generated constructor stub
        }

        private void reset() {
            delegate = new StringWriter();
        }

        @Override
        public String toString() {
            return delegate.getBuffer().toString();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        @Override
        public void write(char cbuf[], int off, int len) throws IOException {
            delegate.write(cbuf, off, len);
        }
    }
}
