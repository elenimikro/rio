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
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.variabletypes.ANNOTATIONPROPERTYVariableType;
import org.coode.oppl.variabletypes.CLASSVariableType;
import org.coode.oppl.variabletypes.CONSTANTVariableType;
import org.coode.oppl.variabletypes.DATAPROPERTYVariableType;
import org.coode.oppl.variabletypes.INDIVIDUALVariableType;
import org.coode.oppl.variabletypes.OBJECTPROPERTYVariableType;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.oppl.variabletypes.VariableTypeVisitorEx;
import org.coode.owl.generalise.VariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class SingleOWLEntityReplacementVariableProvider extends VariableProvider {
    private static final class AbstractingVisitor extends
            OWLObjectVisitorExAdapter<Variable<?>> {
        private SingleOWLEntityReplacementVariableProvider _this;

        public AbstractingVisitor(SingleOWLEntityReplacementVariableProvider _object) {
            _this = _object;
        }
        @Override
        protected Variable<?> getDefaultReturnValue(OWLObject _object) {
            if (_object.equals(_this.getOWLObject())) {
                return _this.getStar(_object);
            }
            if (_object instanceof OWLEntity
                    && _this.getRelevancePolicy().isRelevant((OWLEntity) _object)) {
                return null;
            } else {
                return _this.getVariable(_object);
            }
        }

        @Override
        public Variable<?> visit(final IRI iri) {
            OWLObject owlEntity = _this
                    .getOWLEntity(iri);
            return owlEntity != null ? owlEntity.accept(this) : null;
        }
    }

    private OWLObject owlObject;
    private final RelevancePolicy relevancePolicy;
    private final AbstractingVisitor abstracter;

    /** @param entityProvider
     * @param constraintSystem
     * @param owlObject */
    public SingleOWLEntityReplacementVariableProvider(
            final RelevancePolicy relevancePolicy,
            final OWLEntityProvider entityProvider) {
        super(entityProvider);
        if (relevancePolicy == null) {
            throw new NullPointerException("The relevance policy cannot be null");
        }
        this.relevancePolicy = relevancePolicy;
        abstracter = new AbstractingVisitor(this);
    }

    @Override
    protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
        if (owlObject == null) {
            throw new NullPointerException("The owlObject cannot be null");
        }
        return owlObject.accept(abstracter);
    }

    Variable<?> getVariable(final OWLObject owlObject) {
        Variable<?> toReturn = null;
        VariableType<?> variableType = VariableTypeFactory.getVariableType(owlObject);
        if (variableType != null) {
            toReturn = variableType.accept(new VariableTypeVisitorEx<Variable<?>>() {
                @Override
                public Variable<?> visitCLASSVariableType(
                        final CLASSVariableType classVariableType) {
                    return createVariable("?owlClass", classVariableType);
                }

                /** @param type
                 * @return
                 * @throws OPPLException */
                protected Variable<?> createVariable(final String name,
                        final VariableType<?> type) {
                    try {
                        return SingleOWLEntityReplacementVariableProvider.this
                                .getConstraintSystem().createVariableWithVerifiedName(
                                        name, type, null);
                    } catch (OPPLException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public Variable<?> visitOBJECTPROPERTYVariableType(
                        final OBJECTPROPERTYVariableType objectpropertyVariableType) {
                    return createVariable("?owlObjectproperty",
                            objectpropertyVariableType);
                }

                @Override
                public Variable<?> visitDATAPROPERTYVariableType(
                        final DATAPROPERTYVariableType datapropertyVariableType) {
                    return createVariable("?owlDatatypeProperty",
                            datapropertyVariableType);
                }

                @Override
                public Variable<?> visitINDIVIDUALVariableType(
                        final INDIVIDUALVariableType individualVariableType) {
                    return createVariable("?owlIndividual", individualVariableType);
                }

                @Override
                public Variable<?> visitCONSTANTVariableType(
                        final CONSTANTVariableType constantVariableType) {
                    return createVariable("?owlLiteral", constantVariableType);
                }

                @Override
                public
                        Variable<?>
                        visitANNOTATIONPROPERTYVariableType(
                                final ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
                    return createVariable("?owlAnnotationProperty",
                            annotationpropertyVariableType);
                }
            });
        }
        return toReturn;
    }

    Variable<?> getStar(final OWLObject owlObject) {
        try {
            return getConstraintSystem().createVariableWithVerifiedName("?star",
                    VariableTypeFactory.getVariableType(owlObject), null);
        } catch (OPPLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** @return the owlObject */
    public OWLObject getOWLObject() {
        if (owlObject == null) {
            throw new NullPointerException("The owlObject cannot be null");
        }
        return owlObject;
    }

    public void setOWLObject(final OWLObject o) {
        owlObject = o;
    }

    /** @return the relevancePolicy */
    public RelevancePolicy getRelevancePolicy() {
        return relevancePolicy;
    }
}
