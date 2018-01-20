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
package org.coode.proximitymatrix.ui;

import java.awt.Component;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.semanticweb.owlapi.model.OWLRuntimeException;

class ShowMessageRuntimeExceptionHandler implements RuntimeExceptionHandler {
    private final Component parentComponent;

    /**
     * @param parentComponent parentComponent
     */
    public ShowMessageRuntimeExceptionHandler(Component parentComponent) {
        if (parentComponent == null) {
            throw new NullPointerException("The parent component cannot be null");
        }
        this.parentComponent = parentComponent;
    }

    @Override
    public void handleOWLRuntimeException(OWLRuntimeException e) {
        JOptionPane.showMessageDialog(getParentComponent(), e.getMessage(), "OPPL Runtime error",
            JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handlePatternSyntaxExcpetion(PatternSyntaxException e) {
        JOptionPane.showMessageDialog(getParentComponent(), e.getMessage(), "OPPL Runtime error",
            JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handleException(RuntimeException e) {
        JOptionPane.showMessageDialog(getParentComponent(), e.getMessage(), "OPPL Runtime error",
            JOptionPane.ERROR_MESSAGE);
    }

    /** @return the parentComponent */
    public Component getParentComponent() {
        return parentComponent;
    }
}
