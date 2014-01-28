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
package org.coode.owl.generalise.structural;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.oppl.ConstraintSystem;
import org.coode.owl.generalise.AbstractOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public class OWLEntityRelevancePolicyOWLObjectGeneralisation extends
        AbstractOWLObjectGeneralisation {
    /** @param policy
     *            policy
     * @param entityProvider
     *            entityProvider
     * @param constraintSystem
     *            constraintSystem */
    public OWLEntityRelevancePolicyOWLObjectGeneralisation(
            RelevancePolicy<OWLEntity> policy, OWLEntityProvider entityProvider,
            ConstraintSystem constraintSystem) {
        super(new OWLEntityReplacementVariableProvider(policy, entityProvider,
                constraintSystem));
    }
}
