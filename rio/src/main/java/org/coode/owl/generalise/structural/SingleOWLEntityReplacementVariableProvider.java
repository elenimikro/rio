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
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

/** @author eleni */
public class SingleOWLEntityReplacementVariableProvider extends VariableProvider {
    class VariableVisitor implements VariableTypeVisitorEx<Variable<?>> {
        @Override
        public Variable<?> visitCLASSVariableType(CLASSVariableType classVariableType) {
            return createVariable("?owlClass", classVariableType);
        }

        /**
         * @param type type
         * @return variable
         */
        protected Variable<?> createVariable(String name, VariableType<?> type) {
            try {
                return getConstraintSystem().createVariableWithVerifiedName(name, type, null);
            } catch (OPPLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Variable<?> visitOBJECTPROPERTYVariableType(
            OBJECTPROPERTYVariableType objectpropertyVariableType) {
            return createVariable("?owlObjectproperty", objectpropertyVariableType);
        }

        @Override
        public Variable<?> visitDATAPROPERTYVariableType(
            DATAPROPERTYVariableType datapropertyVariableType) {
            return createVariable("?owlDatatypeProperty", datapropertyVariableType);
        }

        @Override
        public Variable<?> visitINDIVIDUALVariableType(
            INDIVIDUALVariableType individualVariableType) {
            return createVariable("?owlIndividual", individualVariableType);
        }

        @Override
        public Variable<?> visitCONSTANTVariableType(CONSTANTVariableType constantVariableType) {
            return createVariable("?owlLiteral", constantVariableType);
        }

        @Override
        public Variable<?> visitANNOTATIONPROPERTYVariableType(
            ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
            return createVariable("?owlAnnotationProperty", annotationpropertyVariableType);
        }
    }

    private static final class AbstractingVisitor implements OWLObjectVisitorEx<Variable<?>> {
        private SingleOWLEntityReplacementVariableProvider _this;

        public AbstractingVisitor(SingleOWLEntityReplacementVariableProvider _object) {
            _this = _object;
        }

        @Override
        public <T> Variable<?> doDefault(T _object) {
            if (_object.equals(_this.getOWLObject())) {
                return _this.getStar((OWLObject) _object);
            }
            if (_object instanceof OWLEntity
                && _this.getRelevancePolicy().isRelevant((OWLEntity) _object)) {
                return null;
            } else {
                return _this.getVariable((OWLObject) _object);
            }
        }

        @Override
        public Variable<?> visit(IRI iri) {
            OWLObject owlEntity = _this.getOWLEntity(iri);
            return owlEntity != null ? owlEntity.accept(this) : null;
        }
    }

    private OWLObject owlObject;
    private final RelevancePolicy<OWLEntity> relevancePolicy;
    private final AbstractingVisitor abstracter;
    private VariableVisitor varVisitor = new VariableVisitor();

    /**
     * @param relevancePolicy relevancePolicy
     * @param entityProvider entityProvider
     */
    public SingleOWLEntityReplacementVariableProvider(RelevancePolicy<OWLEntity> relevancePolicy,
        OWLEntityProvider entityProvider) {
        super(entityProvider);
        if (relevancePolicy == null) {
            throw new NullPointerException("The relevance policy cannot be null");
        }
        this.relevancePolicy = relevancePolicy;
        abstracter = new AbstractingVisitor(this);
    }

    @Override
    protected Variable<?> getAbstractingVariable(OWLObject object) {
        if (object == null) {
            throw new NullPointerException("The owlObject cannot be null");
        }
        return object.accept(abstracter);
    }

    Variable<?> getVariable(OWLObject object) {
        Variable<?> toReturn = null;
        VariableType<?> variableType = VariableTypeFactory.getVariableType(object);
        if (variableType != null) {
            toReturn = variableType.accept(varVisitor);
        }
        return toReturn;
    }

    Variable<?> getStar(OWLObject o) {
        try {
            return getConstraintSystem().createVariableWithVerifiedName("?star",
                VariableTypeFactory.getVariableType(o), null);
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

    /**
     * @param o o
     */
    public void setOWLObject(OWLObject o) {
        owlObject = o;
    }

    /** @return the relevancePolicy */
    public RelevancePolicy<OWLEntity> getRelevancePolicy() {
        return relevancePolicy;
    }
}
