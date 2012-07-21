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
package org.coode.proximitymatrix.cluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.PartialOWLObjectInstantiator;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.coode.oppl.function.SimpleValueComputationParameters;
import org.coode.oppl.rendering.ManchesterSyntaxRenderer;
import org.coode.oppl.utils.OWLObjectExtractor;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLAxiomProvider;
import org.coode.owl.wrappers.OWLOntologyManagerBasedOWLAxiomProvider;
import org.coode.pair.Pair;
import org.coode.pair.SimplePair;
import org.coode.proximitymatrix.History;
import org.coode.proximitymatrix.HistoryItem;
import org.coode.utils.owl.LeastCommonSubsumer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class Utils {
    private final static class OWLOntologyAnnotationClusterDetector extends
            OWLObjectVisitorExAdapter<Boolean> {
        private final List<OWLOntology> ontologies = new ArrayList<OWLOntology>();
        private final Set<OWLEntity> cluster = new HashSet<OWLEntity>();

        // private final Set<IRI> iris = new HashSet<IRI>();
        /** @param ontologies */
        public OWLOntologyAnnotationClusterDetector(
                final Collection<? extends OWLEntity> cluster,
                final Collection<? extends OWLOntology> ontologies) {
            super(false);
            if (ontologies == null) {
                throw new NullPointerException("The ontology cannot be null");
            }
            this.ontologies.addAll(ontologies);
            this.cluster.addAll(cluster);
        }

        @Override
        public Boolean visit(final OWLAnnotationAssertionAxiom axiom) {
            return axiom.getSubject().accept(this);
        }

        @Override
        public Boolean visit(final IRI iri) {
            for (OWLOntology ontology : ontologies) {
                if (!cluster.containsAll(ontology.getEntitiesInSignature(iri))) {
                    return false;
                }
            }
            return true;
        }
    }

    private final static class Handler extends DefaultHandler {
        private final OWLOntologyManager manager;
        private final Set<Set<OWLEntity>> clusters = new LinkedHashSet<Set<OWLEntity>>();

        /** @param manager */
        public Handler(final OWLOntologyManager manager) {
            if (manager == null) {
                throw new NullPointerException("The manager cannot be null");
            }
            this.manager = manager;
        }

        private final LinkedList<Set<OWLEntity>> stack = new LinkedList<Set<OWLEntity>>();
        private final LinkedList<Collection<OWLEntity>> pairStack = new LinkedList<Collection<OWLEntity>>();
        private final LinkedList<Set<OWLEntity>> itemsStack = new LinkedList<Set<OWLEntity>>();
        private final History<Collection<OWLEntity>> history = new History<Collection<OWLEntity>>();

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) throws SAXException {
            if (qName.compareToIgnoreCase("Cluster") == 0) {
                if (!stack.isEmpty()) {
                    clusters.add(stack.pop());
                }
            } else if (qName.compareToIgnoreCase("HistoryItem") == 0) {
                Collection<OWLEntity> second = pairStack.pop();
                Collection<OWLEntity> first = pairStack.pop();
                Pair<Collection<OWLEntity>> mergedPair = new SimplePair<Collection<OWLEntity>>(
                        first, second);
                Collection<? extends Collection<OWLEntity>> historyItemClusters = new ArrayList<Collection<OWLEntity>>(
                        itemsStack);
                HistoryItem<Collection<OWLEntity>> newItem = new HistoryItem<Collection<OWLEntity>>(
                        mergedPair, historyItemClusters);
                history.add(newItem);
            }
        }

        private Set<OWLEntity> getOWLEntities(final IRI iri) {
            Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
            Iterator<OWLOntology> iterator = manager.getOntologies().iterator();
            while (iterator.hasNext()) {
                OWLOntology owlOntology = iterator.next();
                Set<OWLEntity> entitiesInSignature = owlOntology
                        .getEntitiesInSignature(iri);
                if (!entitiesInSignature.isEmpty()) {
                    toReturn.addAll(entitiesInSignature);
                }
            }
            return toReturn;
        }

        @Override
        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes) throws SAXException {
            if (qName.compareToIgnoreCase("Cluster") == 0) {
                Set<OWLEntity> cluster = new HashSet<OWLEntity>();
                stack.push(cluster);
            } else if (qName.compareToIgnoreCase("Item") == 0) {
                if (!stack.isEmpty()) {
                    Set<OWLEntity> currentCluster = stack.peek();
                    String iriString = attributes.getValue("iri");
                    if (iriString != null) {
                        IRI iri = IRI.create(iriString);
                        currentCluster.addAll(getOWLEntities(iri));
                    }
                }
            } else if (qName.compareToIgnoreCase("HistoryItem") == 0) {
                pairStack.clear();
            } else if (qName.compareToIgnoreCase("First") == 0
                    || qName.compareToIgnoreCase("Second") == 0) {
                pairStack.push(new HashSet<OWLEntity>());
            } else if (qName.compareToIgnoreCase("PairItem") == 0) {
                if (!pairStack.isEmpty()) {
                    Collection<OWLEntity> currentCluster = pairStack.peek();
                    String iriString = attributes.getValue("iri");
                    if (iriString != null) {
                        IRI iri = IRI.create(iriString);
                        currentCluster.addAll(getOWLEntities(iri));
                    }
                }
            } else if (qName.compareToIgnoreCase("HistoryItemClusters") == 0) {
                itemsStack.clear();
            } else if (qName.compareToIgnoreCase("HistoryItemCluster") == 0) {
                itemsStack.push(new HashSet<OWLEntity>());
            } else if (qName.compareToIgnoreCase("HistoryItemClusterItem") == 0) {
                if (!itemsStack.isEmpty()) {
                    Set<OWLEntity> currentCluster = itemsStack.peek();
                    String iriString = attributes.getValue("iri");
                    if (iriString != null) {
                        IRI iri = IRI.create(iriString);
                        currentCluster.addAll(getOWLEntities(iri));
                    }
                }
            }
        }

        /** @return the clusters */
        public Set<Set<OWLEntity>> getClusters() {
            return new LinkedHashSet<Set<OWLEntity>>(clusters);
        }

        /** @return the history */
        public History<Collection<OWLEntity>> getHistory() {
            return history;
        }
    }

    public static <O extends OWLObject> OWLObjectGeneralisation
            getOWLObjectGeneralisation(
                    final Collection<? extends Collection<? extends O>> set,
                    final Collection<? extends OWLOntology> ontologies,
                    final ConstraintSystem constraintSystem) throws OPPLException {
        int i = 0;
        Set<BindingNode> bindings = new HashSet<BindingNode>(set.size());
        // I need to preload all the constants into a variable before I start
        Set<OWLLiteral> constants = new HashSet<OWLLiteral>();
        for (OWLOntology ontology : ontologies) {
            Set<OWLAxiom> axioms = ontology.getAxioms();
            for (OWLAxiom axiom : axioms) {
                constants.addAll(OWLObjectExtractor.getAllOWLLiterals(axiom));
            }
        }
        if (!constants.isEmpty()) {
            String constantVariableName = "?constant";
            InputVariable<?> constantVariable = constraintSystem.createVariable(
                    constantVariableName, VariableTypeFactory.getCONSTANTVariableType(),
                    null);
            for (OWLLiteral owlLiteral : constants) {
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                bindingNode.addAssignment(new Assignment(constantVariable, owlLiteral));
                bindings.add(bindingNode);
            }
        }
        Set<String> names = new HashSet<String>();
        Set<String> rootNames = new HashSet<String>(Arrays.asList(null, "Thing",
                "topObjectProperty", "topDataProperty", "topAnnotationProperty"));
        OWLAxiomProvider axiomProvider = new OWLOntologyManagerBasedOWLAxiomProvider(
                constraintSystem.getOntologyManager());
        for (Collection<? extends O> cluster : set) {
            if (!cluster.isEmpty()) {
                LeastCommonSubsumer<O, ?> lcs = LeastCommonSubsumer.build(cluster,
                        axiomProvider, constraintSystem.getOntologyManager()
                                .getOWLDataFactory());
                String name;
                ManchesterSyntaxRenderer renderer = constraintSystem.getOPPLFactory()
                        .getManchesterSyntaxRenderer(constraintSystem);
                // the next condition seems to create problems
                if (lcs != null) {
                    OWLObject x = lcs.get(cluster);
                    x.accept(renderer);
                    name = createName(renderer.toString(), names, rootNames);
                } else {
                    name = createName(null, names, rootNames);
                }
                O object = cluster.iterator().next();
                VariableType<?> variableType = VariableTypeFactory
                        .getVariableType(object);
                if (variableType != null) {
                    String variableName = String
                            .format("?%s", name.replaceAll("\\?", "_"))
                            .replaceAll(
                                    ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP,
                                    "_");
                    InputVariable<?> variable = constraintSystem.createVariable(
                            variableName, variableType, null);
                    for (O o : cluster) {
                        BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                        if (VariableTypeFactory.getVariableType(o) == variable.getType()) {
                            bindingNode.addAssignment(new Assignment(variable, o));
                            bindings.add(bindingNode);
                        }
                    }
                }
                i++;
            }
        }
        return new OWLObjectGeneralisation(bindings, constraintSystem);
    }

    private static String createName(final String string, final Set<String> names,
            final Set<String> rootNames) {
        if (!rootNames.contains(string)) {
            if (names.contains(string)) {
                String[] split = string.split("_");
                if (split != null && split.length >= 2) {
                    try {
                        return createName(
                                String.format("%s_%d", split[0],
                                        Integer.parseInt(split[split.length - 1]) + 1),
                                names, rootNames);
                    } catch (NumberFormatException e) {
                        return createName(String.format("%s_%d", string, 1), names,
                                rootNames);
                    }
                } else {
                    return createName(String.format("%s_1", string), names, rootNames);
                }
            } else {
                names.add(string);
                return string;
            }
        } else {
            return createName("cluster_1", names, rootNames);
        }
    }

    public static <O extends OWLEntity> Document toXML(
            final Collection<? extends Cluster<O>> clusters,
            final Collection<? extends OWLOntology> ontologies,
            final OWLObjectRenderer renderer,
            final OWLObjectGeneralisation generalisation,
            final RuntimeExceptionHandler runtimeExceptionHandler)
            throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument();
        Element root = document.createElement("Clusters");
        document.appendChild(root);
        for (Cluster<O> cluster : clusters) {
            Element clusterNode = document.createElement("Cluster");
            root.appendChild(clusterNode);
            for (O o : cluster) {
                Element itemNode = document.createElement("Item");
                itemNode.setAttribute("iri", o.getIRI().toString());
                clusterNode.appendChild(itemNode);
            }
            clusterNode.setAttribute("size", String.format("%d", cluster.size()));
            Element generalisations = document.createElement("Generalisations");
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = buildGeneralisationMap(
                    cluster, ontologies, generalisation, runtimeExceptionHandler);
            for (OWLAxiom generalisedAxiom : generalisationMap.keySet()) {
                Element generalisedAxiomNode = document.createElement("Generalisation");
                generalisedAxiomNode.setAttribute("axiom",
                        renderer.render(generalisedAxiom));
                Collection<OWLAxiomInstantiation> generalisationChildren = generalisationMap
                        .get(generalisedAxiom);
                generalisedAxiomNode.setAttribute("count",
                        Integer.toString(generalisationChildren.size()));
                generalisedAxiomNode
                        .setAttribute(
                                "instantiationStats",
                                Utils.renderInstantiationsStats(buildAssignmentMap(generalisationChildren)));
                for (OWLAxiomInstantiation owlAxiomInstantiation : generalisationChildren) {
                    Element axiomNode = document.createElement("Axiom");
                    axiomNode.setAttribute("axiom",
                            renderer.render(owlAxiomInstantiation.getAxiom()));
                    generalisedAxiomNode.appendChild(axiomNode);
                }
                generalisations.appendChild(generalisedAxiomNode);
            }
            clusterNode.appendChild(generalisations);
        }
        // Element historyElement = document.createElement("History");
        // Iterator<HistoryItem<Collection<? extends O>>> iterator =
        // history.iterator();
        // while (iterator.hasNext()) {
        // HistoryItem<Collection<? extends O>> historyItem = iterator.next();
        // Element historyItemElement = document.createElement("HistoryItem");
        // Element mergedPairElement = document.createElement("MergedPair");
        // Collection<? extends O> first = historyItem.getPair().getFirst();
        // Element firstElement = document.createElement("First");
        // for (O o : first) {
        // Element itemNode = document.createElement("PairItem");
        // itemNode.setAttribute("iri", o.getIRI().toString());
        // firstElement.appendChild(itemNode);
        // }
        // mergedPairElement.appendChild(firstElement);
        // Collection<? extends O> second = historyItem.getPair().getSecond();
        // Element secondElement = document.createElement("Second");
        // for (O o : second) {
        // Element itemNode = document.createElement("PairItem");
        // itemNode.setAttribute("iri", o.getIRI().toString());
        // secondElement.appendChild(itemNode);
        // }
        // mergedPairElement.appendChild(secondElement);
        // historyItemElement.appendChild(mergedPairElement);
        // Element historyItemClustersElement =
        // document.createElement("HistoryItemClusters");
        // Set<Collection<? extends O>> items = historyItem.getItems();
        // for (Collection<? extends O> collection : items) {
        // Element historyItemClusterElement =
        // document.createElement("HistoryItemCluster");
        // for (O o : collection) {
        // Element historyItemClusterItem =
        // document.createElement("HistoryItemClusterItem");
        // historyItemClusterItem.setAttribute("iri", o.getIRI().toString());
        // historyItemClusterElement.appendChild(historyItemClusterItem);
        // }
        // historyItemClustersElement.appendChild(historyItemClusterElement);
        // }
        // historyItemElement.appendChild(historyItemClustersElement);
        // historyElement.appendChild(historyItemElement);
        // }
        // root.appendChild(historyElement);
        return document;
    }

    public static Set<Set<OWLEntity>> readFromXML(final InputStream in,
            final OWLOntologyManager manager) throws ParserConfigurationException,
            SAXException, IOException {
        Comparator<Set<OWLEntity>> sizeComparator = new Comparator<Set<OWLEntity>>() {
            public int compare(final Set<OWLEntity> o1, final Set<OWLEntity> o2) {
                int difference = o1.size() - o2.size();
                return difference != 0 ? difference : o1.toString().hashCode()
                        - o2.toString().hashCode();
            }
        };
        Set<Set<OWLEntity>> toReturn = new TreeSet<Set<OWLEntity>>(
                Collections.reverseOrder(sizeComparator));
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        Handler handler = new Handler(manager);
        parser.parse(new InputSource(in), handler);
        toReturn.addAll(handler.getClusters());
        for (Set<OWLEntity> set : toReturn) {
            purgePuns(set);
        }
        return toReturn;
    }

    public static History<Collection<OWLEntity>> readHistoryFromXML(final InputStream in,
            final OWLOntologyManager manager) throws ParserConfigurationException,
            SAXException, IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        Handler handler = new Handler(manager);
        parser.parse(new InputSource(in), handler);
        return handler.getHistory();
    }

    private static void purgePuns(final Collection<? extends OWLEntity> collection) {
        Class<?> predominantType = null;
        int max = -1;
        OWLEntityVisitorEx<Class<?>> owlEntityClassExtractor = new OWLEntityVisitorEx<Class<?>>() {
            public Class<?> visit(final OWLClass cls) {
                return OWLClass.class;
            }

            public Class<?> visit(final OWLObjectProperty property) {
                return OWLObjectProperty.class;
            }

            public Class<?> visit(final OWLDataProperty property) {
                return OWLDataProperty.class;
            }

            public Class<?> visit(final OWLNamedIndividual individual) {
                return OWLNamedIndividual.class;
            }

            public Class<?> visit(final OWLDatatype datatype) {
                return OWLDatatype.class;
            }

            public Class<?> visit(final OWLAnnotationProperty property) {
                return OWLAnnotationProperty.class;
            }
        };
        for (OWLEntity owlEntity : collection) {
            Class<?> variableType = owlEntity.accept(owlEntityClassExtractor);
            int count = 1;
            for (OWLEntity anotherEntity : collection) {
                if (anotherEntity != owlEntity
                        && variableType.equals(anotherEntity
                                .accept(owlEntityClassExtractor))) {
                    count++;
                }
            }
            if (max < count) {
                predominantType = variableType;
                max = count;
            }
        }
        Iterator<? extends OWLEntity> iterator = collection.iterator();
        while (iterator.hasNext()) {
            OWLEntity owlEntity = iterator.next();
            if (!owlEntity.accept(owlEntityClassExtractor).equals(predominantType)) {
                iterator.remove();
            }
        }
    }

    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
            final Collection<? extends OWLEntity> cluster,
            final Collection<? extends OWLOntology> ontologies,
            final OWLObjectGeneralisation generalisation,
            final RuntimeExceptionHandler runtimeExceptionHandler) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        Set<OWLAxiom> ontologyAxioms = new HashSet<OWLAxiom>();
        for (OWLOntology ontology : ontologies) {
            ontologyAxioms.addAll(ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION));
        }
        OWLOntologyAnnotationClusterDetector visitor = new OWLOntologyAnnotationClusterDetector(
                cluster, ontologies);
        for (OWLAxiom axiom : ontologyAxioms) {
            Set<OWLEntity> signature = axiom.getSignature();
            boolean intersection = false;
            Iterator<OWLEntity> it = signature.iterator();
            while (!intersection && it.hasNext()) {
                intersection = cluster.contains(it.next());
            }
            if (intersection || axiom.accept(visitor)) {
                generalisation.clearSubstitutions();
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
                        generalisation.getSubstitutions()));
            }
        }
        return generalisationMap;
    }

    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
            final Collection<? extends OWLEntity> cluster,
            final Collection<? extends OWLOntology> ontologies,
            final Set<OWLAxiom> axioms, final OWLObjectGeneralisation generalisation,
            final RuntimeExceptionHandler runtimeExceptionHandler) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        for (OWLAxiom axiom : axioms) {
            if (axiom.getAxiomType() != AxiomType.DECLARATION) {
                OWLOntologyAnnotationClusterDetector visitor = new OWLOntologyAnnotationClusterDetector(
                        cluster, ontologies);
                Set<OWLEntity> signature = new HashSet<OWLEntity>(axiom.getSignature());
                signature.retainAll(cluster);
                if (!signature.isEmpty() || axiom.accept(visitor)) {
                    generalisation.clearSubstitutions();
                    OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                    generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
                            generalisation.getSubstitutions()));
                }
            }
        }
        return generalisationMap;
    }

    public static boolean appears(final OWLObject owlObject,
            final Collection<? extends OWLAxiomInstantiation> instantiations) {
        if (owlObject == null) {
            throw new NullPointerException("The owlObject cannot be null");
        }
        if (instantiations == null) {
            throw new NullPointerException("The instantiations cannot be null");
        }
        boolean found = false;
        Iterator<? extends OWLAxiomInstantiation> iterator = instantiations.iterator();
        while (!found && iterator.hasNext()) {
            OWLAxiomInstantiation instantiation = iterator.next();
            AssignmentMap substitutions = instantiation.getSubstitutions();
            Iterator<Variable<?>> anotherIterator = substitutions.keySet().iterator();
            while (!found && anotherIterator.hasNext()) {
                Variable<?> variable = anotherIterator.next();
                Set<OWLObject> values = substitutions.get(variable);
                found = values.contains(owlObject);
            }
        }
        return found;
    }

    public static MultiMap<OWLObject, OWLAxiom> buildClusterAppearanceBreakdown(
            final Collection<? extends OWLObject> cluster,
            final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        MultiMap<OWLObject, OWLAxiom> toReturn = new MultiMap<OWLObject, OWLAxiom>();
        Set<OWLAxiom> keySet = generalisationMap.keySet();
        for (OWLObject owlObject : cluster) {
            for (OWLAxiom owlAxiom : keySet) {
                Collection<OWLAxiomInstantiation> instantiations = generalisationMap
                        .get(owlAxiom);
                if (appears(owlObject, instantiations)) {
                    toReturn.put(owlObject, owlAxiom);
                }
            }
        }
        return toReturn;
    }

    /** @param generalisation
     * @param runtimeExceptionHandler
     * @param generalisationMap */
    public static void pruneGeneralisationMap(final ConstraintSystem constraintSystem,
            final RuntimeExceptionHandler runtimeExceptionHandler,
            final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        Set<OWLAxiom> generalisations = new HashSet<OWLAxiom>(generalisationMap.keySet());
        ToStringRenderer.getInstance().setRenderer(
                new ManchesterOWLSyntaxOWLObjectRendererImpl());
        for (OWLAxiom axiom : generalisations) {
            // if (axiom.toString().contains("cluster_1 ")) {
            // System.out.println(axiom);
            // }
            Collection<OWLAxiomInstantiation> instantiations = generalisationMap
                    .get(axiom);
            if (instantiations.size() == 1) {
                // A single instantiation is not a generalisation hence I
                // will
                // remove this from the map
                generalisationMap.remove(axiom);
            } else {
                Map<Variable<?>, Set<OWLObject>> map = new HashMap<Variable<?>, Set<OWLObject>>();
                for (OWLAxiomInstantiation instantiation : instantiations) {
                    AssignmentMap substitutions = instantiation.getSubstitutions();
                    for (Variable<?> variable : substitutions.keySet()) {
                        Set<OWLObject> values = substitutions.get(variable);
                        if (values.size() == 1) {
                            Set<OWLObject> set = map.get(variable);
                            if (set == null) {
                                set = new HashSet<OWLObject>();
                                map.put(variable, set);
                            }
                            set.add(values.iterator().next());
                        }
                    }
                }
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                for (Variable<?> variable : map.keySet()) {
                    Set<OWLObject> values = map.get(variable);
                    if (values.size() == 1) {
                        bindingNode.addAssignment(new Assignment(variable, values
                                .iterator().next()));
                    }
                }
                if (!bindingNode.getAssignments().isEmpty()) {
                    constraintSystem.setLeaves(Collections.singleton(bindingNode));
                    PartialOWLObjectInstantiator instantiator = new PartialOWLObjectInstantiator(
                            new SimpleValueComputationParameters(constraintSystem,
                                    bindingNode, runtimeExceptionHandler));
                    OWLAxiom instantiation = (OWLAxiom) axiom.accept(instantiator);
                    VariableExtractor variableExtractor = new VariableExtractor(
                            constraintSystem, false);
                    // If after replacing the only values of the variables
                    // we
                    // obtain a variable-free axiom we remove it from the
                    // generalisation map otherwise we need to replace it
                    // with
                    // the
                    // instantiated one and remove the replaced variable
                    // instantiations from its instantiations assignment
                    // maps
                    generalisationMap.remove(axiom);
                    if (!variableExtractor.extractVariables(instantiation).isEmpty()) {
                        for (OWLAxiomInstantiation oldInstantiation : instantiations) {
                            AssignmentMap substitutions = oldInstantiation
                                    .getSubstitutions();
                            for (Variable<?> v : bindingNode.getAssignedVariables()) {
                                substitutions.remove(v);
                            }
                            generalisationMap.put(instantiation,
                                    new OWLAxiomInstantiation(
                                            oldInstantiation.getAxiom(), substitutions));
                        }
                    }
                    // Clean up the constraint system
                    constraintSystem.setLeaves(null);
                }
            }
        }
    }

    /** Flattens out a collection of collections of items turning it into the
     * union of its members.
     * 
     * @param <P>
     * @param collections
     *            The collection of collections. Cannot be {@code null}.
     * @return A Set.
     * @throws NullPointerException
     *             if the input is {@code null}. */
    public static <P extends OWLObject> Set<P> flatten(
            final Collection<? extends Collection<? extends P>> collections) {
        if (collections == null) {
            throw new NullPointerException(
                    "The starting colleciton of collections cannot be null");
        }
        Set<P> toReturn = new HashSet<P>();
        for (Collection<? extends P> collection : collections) {
            toReturn.addAll(collection);
        }
        return toReturn;
    }

    public static String renderInstantiationsStats(final AssignmentMap assignmentMap) {
        Formatter formatter = new Formatter();
        Iterator<Variable<?>> iterator = assignmentMap.keySet().iterator();
        while (iterator.hasNext()) {
            Variable<?> v = iterator.next();
            formatter.format("%s count: %d%s", v.getName(), assignmentMap.get(v).size(),
                    iterator.hasNext() ? "; " : "");
        }
        return formatter.toString();
    }

    public static AssignmentMap buildAssignmentMap(
            final Collection<? extends OWLAxiomInstantiation> instantiations) {
        AssignmentMap toReturn = new AssignmentMap(Collections.<BindingNode> emptySet());
        for (OWLAxiomInstantiation instantiation : instantiations) {
            AssignmentMap substitutions = instantiation.getSubstitutions();
            Set<Variable<?>> variables = substitutions.getVariables();
            for (Variable<?> variable : variables) {
                Set<OWLObject> substitutionValuesForVariable = substitutions
                        .get(variable);
                Set<OWLObject> set = toReturn.get(variable);
                if (set == null) {
                    set = new HashSet<OWLObject>();
                    toReturn.put(variable, set);
                }
                set.addAll(substitutionValuesForVariable);
            }
        }
        return toReturn;
    }

    public static Set<OWLAxiom> extractAxioms(
            final Collection<? extends OWLAxiomInstantiation> instantiations) {
        if (instantiations == null) {
            throw new NullPointerException("The instantiation collection cannot be null");
        }
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>(instantiations.size());
        for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
            toReturn.add(owlAxiomInstantiation.getAxiom());
        }
        return toReturn;
    }

    public static Variable<?> getVariable(final Collection<? extends OWLEntity> cluster,
            final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        boolean found = false;
        Variable<?> toReturn = null;
        Iterator<OWLAxiom> iterator = generalisationMap.keySet().iterator();
        while (!found && iterator.hasNext()) {
            OWLAxiom generalisation = iterator.next();
            Iterator<OWLAxiomInstantiation> anotherIterator = generalisationMap.get(
                    generalisation).iterator();
            while (!found && anotherIterator.hasNext()) {
                OWLAxiomInstantiation owlAxiomInstantiation = anotherIterator.next();
                AssignmentMap substitutions = owlAxiomInstantiation.getSubstitutions();
                Iterator<Variable<?>> yetAnotherIterator = substitutions.keySet()
                        .iterator();
                while (!found && yetAnotherIterator.hasNext()) {
                    Variable<?> variable = yetAnotherIterator.next();
                    Set<OWLObject> values = new HashSet<OWLObject>(
                            substitutions.get(variable));
                    values.retainAll(cluster);
                    found = !values.isEmpty();
                    if (found) {
                        toReturn = variable;
                    }
                }
            }
        }
        return toReturn;
    }
}
