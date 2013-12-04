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

import static org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax.*;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;

/** Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 25-Apr-2007<br>
 * <br> */
public class ManchesterOWLSyntaxObjectRenderer extends AbstractRenderer implements
        OWLObjectVisitor {
    public static final int LINE_LENGTH = 70;

    /** @param writer
     * @param entityShortFormProvider */
    public ManchesterOWLSyntaxObjectRenderer(Writer writer,
            ShortFormProvider entityShortFormProvider) {
        super(writer, entityShortFormProvider);
    }

    protected List<? extends OWLObject> sort(Collection<? extends OWLObject> objects) {
        List<? extends OWLObject> sortedObjects = new ArrayList<OWLObject>(objects);
        Collections.sort(sortedObjects);
        return sortedObjects;
    }

    protected void write(Set<? extends OWLObject> objects, ManchesterOWLSyntax delimeter,
            boolean newline) {
        int tab = getIndent();
        pushTab(tab);
        for (Iterator<? extends OWLObject> it = sort(objects).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                if (newline && isUseWrapping()) {
                    writeNewLine();
                }
                this.write(delimeter);
            }
        }
        popTab();
    }

    protected void writeCommaSeparatedList(Set<? extends OWLObject> objects) {
        for (Iterator<OWLObject> it = new TreeSet<OWLObject>(objects).iterator(); it
                .hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.write(", ");
            }
        }
    }

    protected void write(Set<? extends OWLClassExpression> objects, boolean newline) {
        // boolean lastWasNamed = false;
        boolean first = true;
        for (OWLObject desc : sort(objects)) {
            if (!first) {
                if (newline && isUseWrapping()) {
                    writeNewLine();
                }
                this.write(" ", AND, " ");
            }
            first = false;
            if (desc instanceof OWLAnonymousClassExpression) {
                this.write("(");
            }
            desc.accept(this);
            if (desc instanceof OWLAnonymousClassExpression) {
                this.write(")");
            }
            // lastWasNamed = desc instanceof OWLClass;
        }
    }

    private void writeRestriction(OWLQuantifiedDataRestriction restriction,
            ManchesterOWLSyntax keyword) {
        restriction.getProperty().accept(this);
        this.write(keyword);
        boolean conjunctionOrDisjunction = false;
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            if (restriction.getFiller() instanceof OWLObjectIntersectionOf
                    || restriction.getFiller() instanceof OWLObjectUnionOf) {
                conjunctionOrDisjunction = true;
                incrementTab(4);
            }
            this.write("(");
        }
        restriction.getFiller().accept(this);
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            this.write(")");
            if (conjunctionOrDisjunction) {
                popTab();
            }
        }
    }

    private void writeRestriction(OWLQuantifiedObjectRestriction restriction,
            ManchesterOWLSyntax keyword) {
        restriction.getProperty().accept(this);
        this.write(keyword);
        boolean conjunctionOrDisjunction = false;
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            if (restriction.getFiller() instanceof OWLObjectIntersectionOf
                    || restriction.getFiller() instanceof OWLObjectUnionOf) {
                conjunctionOrDisjunction = true;
                incrementTab(4);
            }
            this.write("(");
        }
        restriction.getFiller().accept(this);
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            this.write(")");
            if (conjunctionOrDisjunction) {
                popTab();
            }
        }
    }

    private
            <R extends OWLPropertyRange, P extends OWLPropertyExpression<R, P>, V extends OWLObject>
            void writeRestriction(OWLHasValueRestriction<R, P, V> restriction) {
        restriction.getProperty().accept(this);
        this.write(VALUE);
        restriction.getValue().accept(this);
    }

    private
            <R extends OWLPropertyRange, P extends OWLPropertyExpression<R, P>, F extends OWLPropertyRange>
            void writeRestriction(OWLCardinalityRestriction<R, P, F> restriction,
                    ManchesterOWLSyntax keyword) {
        restriction.getProperty().accept(this);
        this.write(keyword);
        this.write(Integer.toString(restriction.getCardinality()));
        // if(restriction.isQualified()) {
        writeSpace();
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            this.write("(");
        }
        restriction.getFiller().accept(this);
        if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
            this.write(")");
        }
        // }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Class expressions
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(OWLClass desc) {
        this.write(getShortFormProvider().getShortForm(desc));
    }

    @Override
    public void visit(OWLObjectIntersectionOf desc) {
        this.write(desc.getOperands(), true);
    }

    @Override
    public void visit(OWLObjectUnionOf desc) {
        boolean first = true;
        for (OWLClassExpression op : desc.getOperands()) {
            if (!first) {
                if (isUseWrapping()) {
                    writeNewLine();
                }
                this.write(" ", OR, " ");
            }
            first = false;
            if (op.isAnonymous()) {
                this.write("(");
            }
            op.accept(this);
            if (op.isAnonymous()) {
                this.write(")");
            }
        }
    }

    @Override
    public void visit(OWLObjectComplementOf desc) {
        this.write("", NOT, desc.isAnonymous() ? " " : "");
        if (desc.isAnonymous()) {
            this.write("(");
        }
        desc.getOperand().accept(this);
        if (desc.isAnonymous()) {
            this.write(")");
        }
    }

    @Override
    public void visit(OWLObjectSomeValuesFrom desc) {
        this.writeRestriction(desc, SOME);
    }

    @Override
    public void visit(OWLObjectAllValuesFrom desc) {
        this.writeRestriction(desc, ONLY);
    }

    @Override
    public void visit(OWLObjectHasValue desc) {
        this.writeRestriction(desc);
    }

    @Override
    public void visit(OWLObjectMinCardinality desc) {
        this.writeRestriction(desc, MIN);
    }

    @Override
    public void visit(OWLObjectExactCardinality desc) {
        this.writeRestriction(desc, EXACTLY);
    }

    @Override
    public void visit(OWLObjectMaxCardinality desc) {
        this.writeRestriction(desc, MAX);
    }

    @Override
    public void visit(OWLObjectHasSelf desc) {
        desc.getProperty().accept(this);
        this.write(SOME);
        this.write(SELF);
    }

    @Override
    public void visit(OWLObjectOneOf desc) {
        this.write("{");
        this.write(desc.getIndividuals(), ONE_OF_DELIMETER, false);
        this.write("}");
    }

    @Override
    public void visit(OWLDataSomeValuesFrom desc) {
        this.writeRestriction(desc, SOME);
    }

    @Override
    public void visit(OWLDataAllValuesFrom desc) {
        this.writeRestriction(desc, ONLY);
    }

    @Override
    public void visit(OWLDataHasValue desc) {
        this.writeRestriction(desc);
    }

    @Override
    public void visit(OWLDataMinCardinality desc) {
        this.writeRestriction(desc, MIN);
    }

    @Override
    public void visit(OWLDataExactCardinality desc) {
        this.writeRestriction(desc, EXACTLY);
    }

    @Override
    public void visit(OWLDataMaxCardinality desc) {
        this.writeRestriction(desc, MAX);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Entities stuff
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(OWLObjectProperty property) {
        this.write(getShortFormProvider().getShortForm(property));
    }

    @Override
    public void visit(OWLDataProperty property) {
        this.write(getShortFormProvider().getShortForm(property));
    }

    @Override
    public void visit(OWLNamedIndividual individual) {
        this.write(getShortFormProvider().getShortForm(individual));
    }

    @Override
    public void visit(OWLAnnotationProperty property) {
        this.write(getShortFormProvider().getShortForm(property));
    }

    @Override
    public void visit(OWLDatatype datatype) {
        this.write(getShortFormProvider().getShortForm(datatype));
    }

    @Override
    public void visit(OWLAnonymousIndividual individual) {
        this.write(individual.toString());
    }

    @Override
    public void visit(IRI iri) {
        this.write(iri.toQuotedString());
    }

    @Override
    public void visit(OWLAnnotation node) {
        writeAnnotations(node.getAnnotations());
        node.getProperty().accept(this);
        writeSpace();
        node.getValue().accept(this);
    }

    // private String escape(String s) {
    // for(int i = 0; i < s.length(); i++) {
    // char ch = s.charAt(i);
    // if(i == 0 && ch == '\'') {
    // return s;
    // }
    // if(" [](){},^<>?@".indexOf(ch) != -1) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("'");
    // sb.append(s);
    // sb.append("'");
    // return sb.toString();
    // }
    // }
    // return s;
    // }
    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Data stuff
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(OWLDataComplementOf node) {
        this.write(NOT);
        if (node.getDataRange().isDatatype()) {
            node.getDataRange().accept(this);
        } else {
            this.write("(");
            node.getDataRange().accept(this);
            this.write(")");
        }
    }

    @Override
    public void visit(OWLDataOneOf node) {
        this.write("{");
        this.write(node.getValues(), ONE_OF_DELIMETER, false);
        this.write("}");
    }

    @Override
    public void visit(OWLDataIntersectionOf node) {
        this.write("(");
        this.write(node.getOperands(), AND, false);
        this.write(")");
    }

    @Override
    public void visit(OWLDataUnionOf node) {
        this.write("(");
        this.write(node.getOperands(), OR, false);
        this.write(")");
    }

    @Override
    public void visit(OWLDatatypeRestriction node) {
        node.getDatatype().accept(this);
        this.write("[");
        this.write(node.getFacetRestrictions(), FACET_RESTRICTION_SEPARATOR, false);
        this.write("]");
    }

    @Override
    public void visit(OWLLiteral node) {
        if (node.getDatatype().isDouble()) {
            this.write(node.getLiteral());
        } else if (node.getDatatype().isFloat()) {
            this.write(node.getLiteral());
            this.write("f");
        } else if (node.getDatatype().isInteger()) {
            this.write(node.getLiteral());
        } else if (node.getDatatype().isBoolean()) {
            this.write(node.getLiteral());
        } else {
            pushTab(getIndent());
            writeLiteral(node.getLiteral());
            if (node.hasLang()) {
                this.write("@");
                this.write(node.getLang());
            } else if (!node.isRDFPlainLiteral()) {
                this.write("^^");
                node.getDatatype().accept(this);
            }
            popTab();
        }
    }

    private void writeLiteral(String literal) {
        this.write("\"");
        if (literal.indexOf("\"") == -1 && literal.indexOf("\\") != -1) {
            this.write(literal);
        } else {
            literal = literal.replace("\\", "\\\\");
            literal = literal.replace("\"", "\\\"");
            this.write(literal);
        }
        this.write("\"");
        // if(literal.indexOf('\"') != -1) {
        // write("\"\"\"");
        // write(literal);
        // write("\"\"\"");
        // }
        // else {
        // write("\"");
        // write(literal);
        // write("\"");
        // }
    }

    @Override
    public void visit(OWLFacetRestriction node) {
        this.write(node.getFacet().getSymbolicForm());
        writeSpace();
        node.getFacetValue().accept(this);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Property expression stuff
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(OWLObjectInverseOf property) {
        this.write(INVERSE);
        this.write("(");
        property.getInverse().accept(this);
        this.write(")");
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Annotation stuff
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Stand alone axiom representation
    //
    // We render each axiom as a one line frame
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private boolean wrapSave;
    private boolean tabSave;

    private void setAxiomWriting() {
        wrapSave = isUseWrapping();
        tabSave = isUseTabbing();
        setUseWrapping(false);
        setUseTabbing(false);
    }

    private void restore() {
        setUseTabbing(tabSave);
        setUseWrapping(wrapSave);
    }

    @Override
    public void visit(OWLSubClassOfAxiom axiom) {
        setAxiomWriting();
        axiom.getSubClass().accept(this);
        this.write(SUBCLASS_OF);
        axiom.getSuperClass().accept(this);
        restore();
    }

    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        setAxiomWriting();
        this.write(NOT);
        this.write("(");
        axiom.getSubject().accept(this);
        this.write(" ");
        axiom.getProperty().accept(this);
        this.write(" ");
        axiom.getObject().accept(this);
        this.write(")");
        restore();
    }

    // private void writePropertyCharacteristic(ManchesterOWLSyntax
    // characteristic) {
    // setAxiomWriting();
    // writeSectionKeyword(CHARACTERISTICS);
    // write(characteristic);
    // restore();
    // }
    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(ASYMMETRIC);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(REFLEXIVE);
        axiom.getProperty().accept(this);
        restore();
    }

    private void writeBinaryOrNaryList(ManchesterOWLSyntax binaryKeyword,
            Set<? extends OWLObject> objects, ManchesterOWLSyntax naryKeyword) {
        if (objects.size() == 2) {
            Iterator<? extends OWLObject> it = objects.iterator();
            it.next().accept(this);
            this.write(binaryKeyword);
            it.next().accept(this);
        } else {
            writeSectionKeyword(naryKeyword);
            writeCommaSeparatedList(objects);
        }
    }

    @Override
    public void visit(OWLDisjointClassesAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(DISJOINT_WITH, axiom.getClassExpressions(),
                DISJOINT_CLASSES);
        restore();
    }

    @Override
    public void visit(OWLDataPropertyDomainAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        this.write(DOMAIN);
        axiom.getDomain().accept(this);
        restore();
    }

    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        this.write(DOMAIN);
        axiom.getDomain().accept(this);
        restore();
    }

    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(EQUIVALENT_TO, axiom.getProperties(), EQUIVALENT_PROPERTIES);
        restore();
    }

    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        setAxiomWriting();
        this.write(NOT);
        this.write("(");
        axiom.getSubject().accept(this);
        this.write(" ");
        axiom.getProperty().accept(this);
        this.write(" ");
        axiom.getObject().accept(this);
        this.write(")");
        restore();
    }

    @Override
    public void visit(OWLDifferentIndividualsAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(DIFFERENT_FROM, axiom.getIndividuals(),
                DIFFERENT_INDIVIDUALS);
        restore();
    }

    @Override
    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(DISJOINT_WITH, axiom.getProperties(), DISJOINT_PROPERTIES);
        restore();
    }

    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(DISJOINT_WITH, axiom.getProperties(), DISJOINT_PROPERTIES);
        restore();
    }

    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        this.write(RANGE);
        axiom.getRange().accept(this);
        restore();
    }

    @Override
    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        setAxiomWriting();
        axiom.getSubject().accept(this);
        this.write(" ");
        axiom.getProperty().accept(this);
        this.write(" ");
        axiom.getObject().accept(this);
        restore();
    }

    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(FUNCTIONAL);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        setAxiomWriting();
        axiom.getSubProperty().accept(this);
        this.write(SUB_PROPERTY_OF);
        axiom.getSuperProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
        setAxiomWriting();
        axiom.getOWLClass().accept(this);
        this.write(DISJOINT_UNION_OF);
        writeCommaSeparatedList(axiom.getClassExpressions());
        restore();
    }

    private void writeFrameType(OWLObject object) {
        setAxiomWriting();
        if (object instanceof OWLOntology) {
            writeFrameKeyword(ONTOLOGY);
            OWLOntology ont = (OWLOntology) object;
            if (!ont.isAnonymous()) {
                this.write("<");
                this.write(ont.getOntologyID().getOntologyIRI().toString());
                this.write(">");
            }
        } else {
            if (object instanceof OWLClassExpression) {
                writeFrameKeyword(CLASS);
            } else if (object instanceof OWLObjectPropertyExpression) {
                writeFrameKeyword(OBJECT_PROPERTY);
            } else if (object instanceof OWLDataPropertyExpression) {
                writeFrameKeyword(DATA_PROPERTY);
            } else if (object instanceof OWLIndividual) {
                writeFrameKeyword(INDIVIDUAL);
            } else if (object instanceof OWLAnnotationProperty) {
                writeFrameKeyword(ANNOTATION_PROPERTY);
            }
        }
        object.accept(this);
    }

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        setAxiomWriting();
        writeFrameType(axiom.getEntity());
        restore();
    }

    @Override
    public void visit(OWLAnnotationAssertionAxiom axiom) {
        setAxiomWriting();
        axiom.getSubject().accept(this);
        this.write(" ");
        axiom.getAnnotation().accept(this);
        restore();
    }

    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        this.write(DOMAIN);
        axiom.getDomain().accept(this);
    }

    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        this.write(RANGE);
        axiom.getRange().accept(this);
    }

    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        setAxiomWriting();
        axiom.getSubProperty().accept(this);
        this.write(SUB_PROPERTY_OF);
        axiom.getSuperProperty().accept(this);
    }

    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(SYMMETRIC);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLDataPropertyRangeAxiom axiom) {
        setAxiomWriting();
        axiom.getProperty().accept(this);
        writeSectionKeyword(RANGE);
        axiom.getRange().accept(this);
        restore();
    }

    @Override
    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(FUNCTIONAL);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        setAxiomWriting();
        writeFrameKeyword(EQUIVALENT_PROPERTIES);
        writeCommaSeparatedList(axiom.getProperties());
        restore();
    }

    @Override
    public void visit(OWLClassAssertionAxiom axiom) {
        setAxiomWriting();
        axiom.getIndividual().accept(this);
        this.write(TYPE);
        axiom.getClassExpression().accept(this);
        restore();
    }

    @Override
    public void visit(OWLEquivalentClassesAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(EQUIVALENT_TO, axiom.getClassExpressions(),
                EQUIVALENT_CLASSES);
        restore();
    }

    @Override
    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        setAxiomWriting();
        axiom.getSubject().accept(this);
        this.write(" ");
        axiom.getProperty().accept(this);
        this.write(" ");
        axiom.getObject().accept(this);
        restore();
    }

    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(TRANSITIVE);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(IRREFLEXIVE);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        setAxiomWriting();
        axiom.getSubProperty().accept(this);
        writeSectionKeyword(SUB_PROPERTY_OF);
        axiom.getSuperProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        setAxiomWriting();
        writeSectionKeyword(INVERSE_FUNCTIONAL);
        axiom.getProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLSameIndividualAxiom axiom) {
        setAxiomWriting();
        writeBinaryOrNaryList(SAME_AS, axiom.getIndividuals(), SAME_INDIVIDUAL);
        restore();
    }

    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        setAxiomWriting();
        for (Iterator<OWLObjectPropertyExpression> it = axiom.getPropertyChain()
                .iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.write(" o ");
            }
        }
        this.write(SUB_PROPERTY_OF);
        axiom.getSuperProperty().accept(this);
        restore();
    }

    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        setAxiomWriting();
        axiom.getFirstProperty().accept(this);
        this.write(INVERSE_OF);
        axiom.getSecondProperty().accept(this);
        restore();
    }

    @Override
    public void visit(SWRLRule rule) {
        setAxiomWriting();
        for (Iterator<SWRLAtom> it = rule.getBody().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.write(", ");
            }
        }
        this.write(" -> ");
        for (Iterator<SWRLAtom> it = rule.getHead().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.write(", ");
            }
        }
        restore();
    }

    @Override
    public void visit(OWLHasKeyAxiom axiom) {
        setAxiomWriting();
        axiom.getClassExpression().accept(this);
        this.write(HAS_KEY);
        this.write(axiom.getObjectPropertyExpressions(), COMMA, false);
        this.write(axiom.getDataPropertyExpressions(), COMMA, false);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SWRL
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(SWRLClassAtom node) {
        node.getPredicate().accept(this);
        this.write("(");
        node.getArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(SWRLDataRangeAtom node) {
        node.getPredicate().accept(this);
        this.write("(");
        node.getArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(SWRLObjectPropertyAtom node) {
        node.getPredicate().accept(this);
        this.write("(");
        node.getFirstArgument().accept(this);
        this.write(", ");
        node.getSecondArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(SWRLDataPropertyAtom node) {
        node.getPredicate().accept(this);
        this.write("(");
        node.getFirstArgument().accept(this);
        this.write(", ");
        node.getSecondArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(SWRLBuiltInAtom node) {
        this.write(node.getPredicate().toQuotedString());
        this.write("(");
        for (Iterator<SWRLDArgument> it = node.getArguments().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.write(", ");
            }
        }
        this.write(")");
    }

    @Override
    public void visit(SWRLVariable node) {
        this.write("?");
        this.write(node.getIRI().toQuotedString());
    }

    @Override
    public void visit(SWRLIndividualArgument node) {
        node.getIndividual().accept(this);
    }

    @Override
    public void visit(SWRLLiteralArgument node) {
        node.getLiteral().accept(this);
    }

    @Override
    public void visit(SWRLSameIndividualAtom node) {
        this.write(SAME_AS);
        this.write("(");
        node.getFirstArgument().accept(this);
        this.write(", ");
        node.getSecondArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(SWRLDifferentIndividualsAtom node) {
        this.write(DIFFERENT_FROM);
        this.write("(");
        node.getFirstArgument().accept(this);
        this.write(", ");
        node.getSecondArgument().accept(this);
        this.write(")");
    }

    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {}

    protected void writeAnnotations(Set<OWLAnnotation> annos) {
        if (annos.isEmpty()) {
            return;
        }
        writeNewLine();
        this.write(ANNOTATIONS.toString());
        this.write(": ");
        pushTab(getIndent());
        for (Iterator<OWLAnnotation> annoIt = annos.iterator(); annoIt.hasNext();) {
            OWLAnnotation anno = annoIt.next();
            // if (!anno.getAnnotations().isEmpty()) {
            // writeAnnotations(anno.getAnnotations());
            // }
            anno.accept(this);
            if (annoIt.hasNext()) {
                this.write(", ");
                writeNewLine();
            }
        }
        writeNewLine();
        writeNewLine();
        popTab();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Ontology
    //
    // /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(OWLOntology ontology) {}
}
