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

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 25-Apr-2007<br>
 * <br>
 */
public class ManchesterOWLSyntaxObjectRenderer extends AbstractRenderer implements
		OWLObjectVisitor {
	public static final int LINE_LENGTH = 70;

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
		int tab = this.getIndent();
		this.pushTab(tab);
		for (Iterator<? extends OWLObject> it = this.sort(objects).iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				if (newline && this.isUseWrapping()) {
					this.writeNewLine();
				}
				this.write(delimeter);
			}
		}
		this.popTab();
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
		for (OWLObject desc : this.sort(objects)) {
			if (!first) {
				if (newline && this.isUseWrapping()) {
					this.writeNewLine();
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
				this.incrementTab(4);
			}
			this.write("(");
		}
		restriction.getFiller().accept(this);
		if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
			this.write(")");
			if (conjunctionOrDisjunction) {
				this.popTab();
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
				this.incrementTab(4);
			}
			this.write("(");
		}
		restriction.getFiller().accept(this);
		if (restriction.getFiller() instanceof OWLAnonymousClassExpression) {
			this.write(")");
			if (conjunctionOrDisjunction) {
				this.popTab();
			}
		}
	}

	private <R extends OWLPropertyRange, P extends OWLPropertyExpression<R, P>, V extends OWLObject> void writeRestriction(
			OWLHasValueRestriction<R, P, V> restriction) {
		restriction.getProperty().accept(this);
		this.write(VALUE);
		restriction.getValue().accept(this);
	}

	private <R extends OWLPropertyRange, P extends OWLPropertyExpression<R, P>, F extends OWLPropertyRange> void writeRestriction(
			OWLCardinalityRestriction<R, P, F> restriction, ManchesterOWLSyntax keyword) {
		restriction.getProperty().accept(this);
		this.write(keyword);
		this.write(Integer.toString(restriction.getCardinality()));
		// if(restriction.isQualified()) {
		this.writeSpace();
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
	public void visit(OWLClass desc) {
		this.write(this.getShortFormProvider().getShortForm(desc));
	}

	public void visit(OWLObjectIntersectionOf desc) {
		this.write(desc.getOperands(), true);
	}

	public void visit(OWLObjectUnionOf desc) {
		boolean first = true;
		for (OWLClassExpression op : desc.getOperands()) {
			if (!first) {
				if (this.isUseWrapping()) {
					this.writeNewLine();
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

	public void visit(OWLObjectSomeValuesFrom desc) {
		this.writeRestriction(desc, SOME);
	}

	public void visit(OWLObjectAllValuesFrom desc) {
		this.writeRestriction(desc, ONLY);
	}

	public void visit(OWLObjectHasValue desc) {
		this.writeRestriction(desc);
	}

	public void visit(OWLObjectMinCardinality desc) {
		this.writeRestriction(desc, MIN);
	}

	public void visit(OWLObjectExactCardinality desc) {
		this.writeRestriction(desc, EXACTLY);
	}

	public void visit(OWLObjectMaxCardinality desc) {
		this.writeRestriction(desc, MAX);
	}

	public void visit(OWLObjectHasSelf desc) {
		desc.getProperty().accept(this);
		this.write(SOME);
		this.write(SELF);
	}

	public void visit(OWLObjectOneOf desc) {
		this.write("{");
		this.write(desc.getIndividuals(), ONE_OF_DELIMETER, false);
		this.write("}");
	}

	public void visit(OWLDataSomeValuesFrom desc) {
		this.writeRestriction(desc, SOME);
	}

	public void visit(OWLDataAllValuesFrom desc) {
		this.writeRestriction(desc, ONLY);
	}

	public void visit(OWLDataHasValue desc) {
		this.writeRestriction(desc);
	}

	public void visit(OWLDataMinCardinality desc) {
		this.writeRestriction(desc, MIN);
	}

	public void visit(OWLDataExactCardinality desc) {
		this.writeRestriction(desc, EXACTLY);
	}

	public void visit(OWLDataMaxCardinality desc) {
		this.writeRestriction(desc, MAX);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Entities stuff
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////
	public void visit(OWLObjectProperty property) {
		this.write(this.getShortFormProvider().getShortForm(property));
	}

	public void visit(OWLDataProperty property) {
		this.write(this.getShortFormProvider().getShortForm(property));
	}

	public void visit(OWLNamedIndividual individual) {
		this.write(this.getShortFormProvider().getShortForm(individual));
	}

	public void visit(OWLAnnotationProperty property) {
		this.write(this.getShortFormProvider().getShortForm(property));
	}

	public void visit(OWLDatatype datatype) {
		this.write(this.getShortFormProvider().getShortForm(datatype));
	}

	public void visit(OWLAnonymousIndividual individual) {
		this.write(individual.toString());
	}

	public void visit(IRI iri) {
		this.write(iri.toQuotedString());
	}

	public void visit(OWLAnnotation node) {
		this.writeAnnotations(node.getAnnotations());
		node.getProperty().accept(this);
		this.writeSpace();
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

	public void visit(OWLDataOneOf node) {
		this.write("{");
		this.write(node.getValues(), ONE_OF_DELIMETER, false);
		this.write("}");
	}

	public void visit(OWLDataIntersectionOf node) {
		this.write("(");
		this.write(node.getOperands(), AND, false);
		this.write(")");
	}

	public void visit(OWLDataUnionOf node) {
		this.write("(");
		this.write(node.getOperands(), OR, false);
		this.write(")");
	}

	public void visit(OWLDatatypeRestriction node) {
		node.getDatatype().accept(this);
		this.write("[");
		this.write(node.getFacetRestrictions(), FACET_RESTRICTION_SEPARATOR, false);
		this.write("]");
	}

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
			this.pushTab(this.getIndent());
			this.writeLiteral(node.getLiteral());
			if (node.hasLang()) {
				this.write("@");
				this.write(node.getLang());
			} else if (!node.isRDFPlainLiteral()) {
				this.write("^^");
				node.getDatatype().accept(this);
			}
			this.popTab();
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

	public void visit(OWLFacetRestriction node) {
		this.write(node.getFacet().getSymbolicForm());
		this.writeSpace();
		node.getFacetValue().accept(this);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Property expression stuff
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////
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
		this.wrapSave = this.isUseWrapping();
		this.tabSave = this.isUseTabbing();
		this.setUseWrapping(false);
		this.setUseTabbing(false);
	}

	private void restore() {
		this.setUseTabbing(this.tabSave);
		this.setUseWrapping(this.wrapSave);
	}

	public void visit(OWLSubClassOfAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubClass().accept(this);
		this.write(SUBCLASS_OF);
		axiom.getSuperClass().accept(this);
		this.restore();
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		this.setAxiomWriting();
		this.write(NOT);
		this.write("(");
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.write(")");
		this.restore();
	}

	// private void writePropertyCharacteristic(ManchesterOWLSyntax
	// characteristic) {
	// setAxiomWriting();
	// writeSectionKeyword(CHARACTERISTICS);
	// write(characteristic);
	// restore();
	// }
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(ASYMMETRIC);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(REFLEXIVE);
		axiom.getProperty().accept(this);
		this.restore();
	}

	private void writeBinaryOrNaryList(ManchesterOWLSyntax binaryKeyword,
			Set<? extends OWLObject> objects, ManchesterOWLSyntax naryKeyword) {
		if (objects.size() == 2) {
			Iterator<? extends OWLObject> it = objects.iterator();
			it.next().accept(this);
			this.write(binaryKeyword);
			it.next().accept(this);
		} else {
			this.writeSectionKeyword(naryKeyword);
			this.writeCommaSeparatedList(objects);
		}
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(DISJOINT_WITH, axiom.getClassExpressions(),
				DISJOINT_CLASSES);
		this.restore();
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.write(DOMAIN);
		axiom.getDomain().accept(this);
		this.restore();
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.write(DOMAIN);
		axiom.getDomain().accept(this);
		this.restore();
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(EQUIVALENT_TO, axiom.getProperties(),
				EQUIVALENT_PROPERTIES);
		this.restore();
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		this.setAxiomWriting();
		this.write(NOT);
		this.write("(");
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.write(")");
		this.restore();
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(DIFFERENT_FROM, axiom.getIndividuals(),
				DIFFERENT_INDIVIDUALS);
		this.restore();
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(DISJOINT_WITH, axiom.getProperties(),
				DISJOINT_PROPERTIES);
		this.restore();
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(DISJOINT_WITH, axiom.getProperties(),
				DISJOINT_PROPERTIES);
		this.restore();
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.write(RANGE);
		axiom.getRange().accept(this);
		this.restore();
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.restore();
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(FUNCTIONAL);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubProperty().accept(this);
		this.write(SUB_PROPERTY_OF);
		axiom.getSuperProperty().accept(this);
		this.restore();
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		this.setAxiomWriting();
		axiom.getOWLClass().accept(this);
		this.write(DISJOINT_UNION_OF);
		this.writeCommaSeparatedList(axiom.getClassExpressions());
		this.restore();
	}

	private void writeFrameType(OWLObject object) {
		this.setAxiomWriting();
		if (object instanceof OWLOntology) {
			this.writeFrameKeyword(ONTOLOGY);
			OWLOntology ont = (OWLOntology) object;
			if (!ont.isAnonymous()) {
				this.write("<");
				this.write(ont.getOntologyID().getOntologyIRI().toString());
				this.write(">");
			}
		} else {
			if (object instanceof OWLClassExpression) {
				this.writeFrameKeyword(CLASS);
			} else if (object instanceof OWLObjectPropertyExpression) {
				this.writeFrameKeyword(OBJECT_PROPERTY);
			} else if (object instanceof OWLDataPropertyExpression) {
				this.writeFrameKeyword(DATA_PROPERTY);
			} else if (object instanceof OWLIndividual) {
				this.writeFrameKeyword(INDIVIDUAL);
			} else if (object instanceof OWLAnnotationProperty) {
				this.writeFrameKeyword(ANNOTATION_PROPERTY);
			}
		}
		object.accept(this);
	}

	public void visit(OWLDeclarationAxiom axiom) {
		this.setAxiomWriting();
		this.writeFrameType(axiom.getEntity());
		this.restore();
	}

	public void visit(OWLAnnotationAssertionAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getAnnotation().accept(this);
		this.restore();
	}

	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.write(DOMAIN);
		axiom.getDomain().accept(this);
	}

	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.write(RANGE);
		axiom.getRange().accept(this);
	}

	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubProperty().accept(this);
		this.write(SUB_PROPERTY_OF);
		axiom.getSuperProperty().accept(this);
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(SYMMETRIC);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		this.setAxiomWriting();
		axiom.getProperty().accept(this);
		this.writeSectionKeyword(RANGE);
		axiom.getRange().accept(this);
		this.restore();
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(FUNCTIONAL);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		this.setAxiomWriting();
		this.writeFrameKeyword(EQUIVALENT_PROPERTIES);
		this.writeCommaSeparatedList(axiom.getProperties());
		this.restore();
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		this.setAxiomWriting();
		axiom.getIndividual().accept(this);
		this.write(TYPE);
		axiom.getClassExpression().accept(this);
		this.restore();
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(EQUIVALENT_TO, axiom.getClassExpressions(),
				EQUIVALENT_CLASSES);
		this.restore();
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubject().accept(this);
		this.write(" ");
		axiom.getProperty().accept(this);
		this.write(" ");
		axiom.getObject().accept(this);
		this.restore();
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(TRANSITIVE);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(IRREFLEXIVE);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLSubDataPropertyOfAxiom axiom) {
		this.setAxiomWriting();
		axiom.getSubProperty().accept(this);
		this.writeSectionKeyword(SUB_PROPERTY_OF);
		axiom.getSuperProperty().accept(this);
		this.restore();
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.setAxiomWriting();
		this.writeSectionKeyword(INVERSE_FUNCTIONAL);
		axiom.getProperty().accept(this);
		this.restore();
	}

	public void visit(OWLSameIndividualAxiom axiom) {
		this.setAxiomWriting();
		this.writeBinaryOrNaryList(SAME_AS, axiom.getIndividuals(), SAME_INDIVIDUAL);
		this.restore();
	}

	public void visit(OWLSubPropertyChainOfAxiom axiom) {
		this.setAxiomWriting();
		for (Iterator<OWLObjectPropertyExpression> it = axiom.getPropertyChain()
				.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.write(" o ");
			}
		}
		this.write(SUB_PROPERTY_OF);
		axiom.getSuperProperty().accept(this);
		this.restore();
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		this.setAxiomWriting();
		axiom.getFirstProperty().accept(this);
		this.write(INVERSE_OF);
		axiom.getSecondProperty().accept(this);
		this.restore();
	}

	public void visit(SWRLRule rule) {
		this.setAxiomWriting();
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
		this.restore();
	}

	public void visit(OWLHasKeyAxiom axiom) {
		this.setAxiomWriting();
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
	public void visit(SWRLClassAtom node) {
		node.getPredicate().accept(this);
		this.write("(");
		node.getArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDataRangeAtom node) {
		node.getPredicate().accept(this);
		this.write("(");
		node.getArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLObjectPropertyAtom node) {
		node.getPredicate().accept(this);
		this.write("(");
		node.getFirstArgument().accept(this);
		this.write(", ");
		node.getSecondArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDataPropertyAtom node) {
		node.getPredicate().accept(this);
		this.write("(");
		node.getFirstArgument().accept(this);
		this.write(", ");
		node.getSecondArgument().accept(this);
		this.write(")");
	}

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

	public void visit(SWRLVariable node) {
		this.write("?");
		this.write(node.getIRI().toQuotedString());
	}

	public void visit(SWRLIndividualArgument node) {
		node.getIndividual().accept(this);
	}

	public void visit(SWRLLiteralArgument node) {
		node.getLiteral().accept(this);
	}

	public void visit(SWRLSameIndividualAtom node) {
		this.write(SAME_AS);
		this.write("(");
		node.getFirstArgument().accept(this);
		this.write(", ");
		node.getSecondArgument().accept(this);
		this.write(")");
	}

	public void visit(SWRLDifferentIndividualsAtom node) {
		this.write(DIFFERENT_FROM);
		this.write("(");
		node.getFirstArgument().accept(this);
		this.write(", ");
		node.getSecondArgument().accept(this);
		this.write(")");
	}

	@SuppressWarnings("unused")
	public void visit(OWLDatatypeDefinitionAxiom axiom) {}

	protected void writeAnnotations(Set<OWLAnnotation> annos) {
		if (annos.isEmpty()) {
			return;
		}
		this.writeNewLine();
		this.write(ANNOTATIONS.toString());
		this.write(": ");
		this.pushTab(this.getIndent());
		for (Iterator<OWLAnnotation> annoIt = annos.iterator(); annoIt.hasNext();) {
			OWLAnnotation anno = annoIt.next();
			// if (!anno.getAnnotations().isEmpty()) {
			// writeAnnotations(anno.getAnnotations());
			// }
			anno.accept(this);
			if (annoIt.hasNext()) {
				this.write(", ");
				this.writeNewLine();
			}
		}
		this.writeNewLine();
		this.writeNewLine();
		this.popTab();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Ontology
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public void visit(OWLOntology ontology) {}
}
