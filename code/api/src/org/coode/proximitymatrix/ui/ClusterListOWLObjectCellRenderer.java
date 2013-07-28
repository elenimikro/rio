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
/**
 * 
 */
package org.coode.proximitymatrix.ui;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.ShortFormProvider;

/**
 * @author Luigi Iannone
 * 
 */
public class ClusterListOWLObjectCellRenderer implements ListCellRenderer {
	private final ShortFormProvider shortFormProvider;

	/**
	 * @param shortFormProvider
	 */
	public ClusterListOWLObjectCellRenderer(ShortFormProvider shortFormProvider) {
		if (shortFormProvider == null) {
			throw new NullPointerException("The short form provider cannot be null");
		}
		this.shortFormProvider = shortFormProvider;
	}

	@Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
		Component toReturn = defaultListCellRenderer.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		if (value instanceof Set<?>) {
			Set<String> values = new HashSet<String>(((Set<?>) value).size());
			for (Object object : (Set<?>) value) {
				values.add(this.render(object));
			}
			toReturn = defaultListCellRenderer.getListCellRendererComponent(list, values,
					index, isSelected, cellHasFocus);
		}
		return toReturn;
	}

	protected String render(Object object) {
		String toReturn = object.toString();
		if (object instanceof OWLEntity) {
			toReturn = this.getShortFormProvider().getShortForm((OWLEntity) object);
		}
		return toReturn;
	}

	/**
	 * @return the shortFormProvider
	 */
	public ShortFormProvider getShortFormProvider() {
		return this.shortFormProvider;
	}
}
