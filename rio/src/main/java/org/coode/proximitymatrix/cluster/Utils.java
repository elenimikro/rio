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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.OPPLShortFormProvider;
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
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.History;
import org.coode.proximitymatrix.HistoryItem;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.coode.proximitymatrix.ui.ClusterStatisticsTableModel;
import org.coode.utils.EntityComparator;
import org.coode.utils.owl.LeastCommonSubsumer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
//import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;
/** @author ignazio */
public class Utils {
    private final static class OWLOntologyAnnotationClusterDetector
        implements OWLObjectVisitorEx<Boolean> {
        private final List<OWLOntology> ontologies = new ArrayList<>();
        private final Set<OWLEntity> cluster = new HashSet<>();

        /**
         * @param cluster cluster
         * @param ontologies ontologies
         */
        public OWLOntologyAnnotationClusterDetector(Collection<OWLEntity> cluster,
            Collection<OWLOntology> ontologies) {
            if (ontologies == null) {
                throw new NullPointerException("The ontology cannot be null");
            }
            ontologies.addAll(ontologies);
            cluster.addAll(cluster);
        }

        @Override
        public <T> Boolean doDefault(T object) {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
            return axiom.getSubject().accept(this);
        }

        @Override
        public Boolean visit(IRI iri) {
            return Boolean.valueOf(ontologies.stream().flatMap(o -> o.entitiesInSignature(iri))
                .allMatch(cluster::contains));
        }
    }

    @SuppressWarnings("unused")
    private static class Handler extends DefaultHandler {
        private OWLOntologyManager manager;
        protected Set<Set<OWLEntity>> clusters = new LinkedHashSet<Set<OWLEntity>>();

        /**
         * @param manager manager
         */
        public Handler(OWLOntologyManager manager) {
            if (manager == null) {
                throw new NullPointerException("The manager cannot be null");
            }
            this.manager = manager;
        }

        private final LinkedList<Set<OWLEntity>> stack = new LinkedList<Set<OWLEntity>>();
        private final LinkedList<Collection<OWLEntity>> pairStack =
            new LinkedList<Collection<OWLEntity>>();
        private final LinkedList<Set<OWLEntity>> itemsStack = new LinkedList<Set<OWLEntity>>();
        private final History<Collection<OWLEntity>> history = new History<Collection<OWLEntity>>();

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.compareToIgnoreCase("Cluster") == 0) {
                if (!stack.isEmpty()) {
                    clusters.add(stack.pop());
                }
            } else if (qName.compareToIgnoreCase("HistoryItem") == 0) {
                Collection<OWLEntity> second = pairStack.pop();
                Collection<OWLEntity> first = pairStack.pop();
                Pair<Collection<OWLEntity>> mergedPair =
                    new SimplePair<Collection<OWLEntity>>(first, second);
                Collection<? extends Collection<OWLEntity>> historyItemClusters =
                    new ArrayList<Collection<OWLEntity>>(itemsStack);
                HistoryItem<Collection<OWLEntity>> newItem =
                    new HistoryItem<Collection<OWLEntity>>(mergedPair, historyItemClusters);
                history.add(newItem);
            }
        }

        private Set<OWLEntity> getOWLEntities(IRI iri) {
            return asSet(manager.ontologies().flatMap(o -> o.entitiesInSignature(iri)));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
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

    /**
     * @param set set
     * @param ontologies ontologies
     * @param constraintSystem constraintSystem
     * @param <O> type
     * @return generalisation
     * @throws OPPLException OPPLException
     */
    public static <O extends OWLObject> OWLObjectGeneralisation getOWLObjectGeneralisation(
        Collection<? extends Collection<? extends O>> set, Collection<OWLOntology> ontologies,
        ConstraintSystem constraintSystem) throws OPPLException {
        // int i = 0;
        Set<BindingNode> bindings = new HashSet<>(set.size());
        // I need to preload all the constants into a variable before I start
        Set<OWLLiteral> constants = asSet(ontologies.stream().flatMap(OWLOntology::axioms)
            .flatMap(OWLObjectExtractor::getAllOWLLiterals));
        if (!constants.isEmpty()) {
            String constantVariableName = "?constant";
            InputVariable<?> constantVariable = constraintSystem.createVariable(
                constantVariableName, VariableTypeFactory.getCONSTANTVariableType(), null);
            for (OWLLiteral owlLiteral : constants) {
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                bindingNode.addAssignment(new Assignment(constantVariable, owlLiteral));
                bindings.add(bindingNode);
            }
        }
        Set<String> names = new HashSet<>();
        Set<String> rootNames = new HashSet<>(Arrays.asList(null, "Thing", "topObjectProperty",
            "topDataProperty", "topAnnotationProperty"));
        OWLAxiomProvider axiomProvider =
            new OWLOntologyManagerBasedOWLAxiomProvider(constraintSystem.getOntologyManager());
        for (Collection<? extends O> cluster : set) {
            checkForPuns(cluster);
            if (!cluster.isEmpty()) {
                LeastCommonSubsumer<O, ?> lcs = LeastCommonSubsumer.build(cluster, axiomProvider,
                    constraintSystem.getOntologyManager().getOWLDataFactory());
                String name;
                ManchesterSyntaxRenderer renderer =
                    constraintSystem.getOPPLFactory().getManchesterSyntaxRenderer(constraintSystem);
                // the next condition seems to create problems
                if (lcs != null) {
                    OWLObject x = lcs.get(cluster);
                    x.accept(renderer);
                    name = createName(renderer.toString(), names, rootNames);
                } else {
                    name = createName(null, names, rootNames);
                }
                O object = cluster.iterator().next();
                VariableType<?> variableType = VariableTypeFactory.getVariableType(object);
                if (variableType != null) {
                    String variableName = String.format("?%s", name.replaceAll("\\?", "_"))
                        .replaceAll(ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP, "_");
                    InputVariable<?> variable =
                        constraintSystem.createVariable(variableName, variableType, null);
                    for (O o : cluster) {
                        BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                        if (VariableTypeFactory.getVariableType(o) == variable.getType()) {
                            bindingNode.addAssignment(new Assignment(variable, o));
                            bindings.add(bindingNode);
                        }
                    }
                }
                // i++;
            }
        }
        return new OWLObjectGeneralisation(bindings, constraintSystem);
    }

    /**
     * @param string string
     * @param names names
     * @param rootNames rootNames
     * @return name
     */
    public static String createName(String string, Set<String> names, Set<String> rootNames) {
        if (!rootNames.contains(string)) {
            if (names.contains(string)) {
                String[] split = string.split("_");
                if (split != null && split.length >= 2) {
                    try {
                        return createName(String.format("%s_%d", split[0],
                            Integer.valueOf(split[split.length - 1] + 1)), names, rootNames);
                    } catch (NumberFormatException e) {
                        return createName(String.format("%s_%d", string, Integer.valueOf(1)), names,
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

    /**
     * @param cluster cluster
     * @param <O> type
     */
    public static <O extends OWLObject> void checkForPuns(Collection<? extends O> cluster) {
        // String name = cluster.iterator().next().getClass().getName();
        Set<String> otherNames = new HashSet<>();
        for (OWLObject o : cluster) {
            otherNames.add(o.getClass().getName());
        }
        if (otherNames.size() > 1) {
            purgePuns((Collection<? extends OWLEntity>) cluster);
        }
    }

    /**
     * @param clusters clusters
     * @param ontologies ontologies
     * @param renderer renderer
     * @param generalisation generalisation
     * @param <O> type
     * @return document
     * @throws ParserConfigurationException ParserConfigurationException
     */
    public static <O extends OWLEntity> Document toXML(Collection<? extends Cluster<O>> clusters,
        Collection<OWLOntology> ontologies, OWLObjectRenderer renderer,
        OWLObjectGeneralisation generalisation) throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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
            clusterNode.setAttribute("size", Integer.toString(cluster.size()));
            Element generalisations = document.createElement("Generalisations");
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap =
                buildGeneralisationMap((Collection<OWLEntity>) cluster, ontologies, generalisation);
            for (OWLAxiom generalisedAxiom : generalisationMap.keySet()) {
                Element generalisedAxiomNode = document.createElement("Generalisation");
                generalisedAxiomNode.setAttribute("axiom", renderer.render(generalisedAxiom));
                Collection<OWLAxiomInstantiation> generalisationChildren =
                    generalisationMap.get(generalisedAxiom);
                generalisedAxiomNode.setAttribute("count",
                    Integer.toString(generalisationChildren.size()));
                generalisedAxiomNode.setAttribute("instantiationStats",
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
        return document;
    }

    /**
     * @param in in
     * @param manager manager
     * @return set of entities
     * @throws ParserConfigurationException ParserConfigurationException
     * @throws SAXException SAXException
     * @throws IOException IOException
     */
    public static Set<Set<OWLEntity>> readFromXML(InputStream in, OWLOntologyManager manager)
        throws ParserConfigurationException, SAXException, IOException {
        Comparator<Set<OWLEntity>> sizeComparator = (o1, o2) -> {
            int difference = o1.size() - o2.size();
            return difference != 0 ? difference
                : o1.toString().hashCode() - o2.toString().hashCode();
        };
        Set<Set<OWLEntity>> toReturn = new TreeSet<>(Collections.reverseOrder(sizeComparator));
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        Handler handler = new Handler(manager);
        parser.parse(new InputSource(in), handler);
        toReturn.addAll(handler.clusters);
        for (Set<OWLEntity> set : toReturn) {
            purgePuns(set);
        }
        return toReturn;
    }

    /**
     * @param in in
     * @param manager manager
     * @return history
     * @throws ParserConfigurationException ParserConfigurationException
     * @throws SAXException SAXException
     * @throws IOException IOException
     */
    public static History<Collection<OWLEntity>> readHistoryFromXML(InputStream in,
        OWLOntologyManager manager) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        Handler handler = new Handler(manager);
        parser.parse(new InputSource(in), handler);
        return handler.getHistory();
    }

    private static void purgePuns(Collection<? extends OWLEntity> collection) {
        Class<?> predominantType = null;
        int max = -1;
        OWLEntityVisitorEx<Class<?>> owlEntityClassExtractor = new OWLEntityVisitorEx<Class<?>>() {
            @Override
            public Class<?> visit(OWLClass cls) {
                return OWLClass.class;
            }

            @Override
            public Class<?> visit(OWLObjectProperty property) {
                return OWLObjectProperty.class;
            }

            @Override
            public Class<?> visit(OWLDataProperty property) {
                return OWLDataProperty.class;
            }

            @Override
            public Class<?> visit(OWLNamedIndividual individual) {
                return OWLNamedIndividual.class;
            }

            @Override
            public Class<?> visit(OWLDatatype datatype) {
                return OWLDatatype.class;
            }

            @Override
            public Class<?> visit(OWLAnnotationProperty property) {
                return OWLAnnotationProperty.class;
            }
        };
        for (OWLEntity owlEntity : collection) {
            Class<?> variableType = owlEntity.accept(owlEntityClassExtractor);
            int count = 1;
            for (OWLEntity anotherEntity : collection) {
                if (anotherEntity != owlEntity
                    && variableType.equals(anotherEntity.accept(owlEntityClassExtractor))) {
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

    /**
     * @param cluster cluster
     * @param ontologies ontologies
     * @param generalisation generalisation
     * @return generalisation map
     */
    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
        Collection<OWLEntity> cluster, Collection<OWLOntology> ontologies,
        OWLObjectGeneralisation generalisation) {
        Set<OWLAxiom> ontologyAxioms = asSet(ontologies.stream().flatMap(OWLOntology::axioms)
            .filter(ax -> !ax.getAxiomType().equals(AxiomType.ANNOTATION_ASSERTION))
            .filter(ax -> !ax.getAxiomType().equals(AxiomType.DECLARATION)));
        return buildGeneralisationMap(cluster, ontologies, generalisation, ontologyAxioms);
    }

    private static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
        Collection<OWLEntity> cluster, Collection<OWLOntology> ontologies,
        OWLObjectGeneralisation generalisation, Set<OWLAxiom> ontologyAxioms) {
        OWLOntologyAnnotationClusterDetector visitor =
            new OWLOntologyAnnotationClusterDetector(cluster, ontologies);
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
        for (OWLAxiom axiom : ontologyAxioms) {
            boolean intersection = axiom.signature().anyMatch(cluster::contains);
            if (intersection || axiom.accept(visitor).booleanValue()) {
                generalisation.clearSubstitutions();
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(generalised,
                    new OWLAxiomInstantiation(axiom, generalisation.getSubstitutions()));
            }
        }
        return generalisationMap;
    }

    /**
     * @param cluster cluster
     * @param generalisation generalisation
     * @param axioms axioms
     * @return generalisation map
     */
    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildKnowledgeExplorerGeneralisationMap(
        Collection<? extends OWLEntity> cluster, OWLObjectGeneralisation generalisation,
        Set<OWLAxiom> axioms) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
        for (OWLAxiom axiom : axioms) {
            boolean intersection = axiom.signature().anyMatch(cluster::contains);
            if (intersection) {
                generalisation.clearSubstitutions();
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(generalised,
                    new OWLAxiomInstantiation(axiom, generalisation.getSubstitutions()));
            }
        }
        return generalisationMap;
    }

    // public static MultiMap<OWLAxiom, OWLAxiomInstantiation>
    // buildGeneralisationMap(
    // Collection<? extends OWLEntity> cluster,
    // Collection<? extends OWLOntology> ontologies,
    // OWLObjectGeneralisation generalisation,
    // RuntimeExceptionHandler runtimeExceptionHandler) {
    // MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new
    // MultiMap<OWLAxiom, OWLAxiomInstantiation>();
    // Set<OWLAxiom> ontologyAxioms = new HashSet<OWLAxiom>();
    // for (OWLOntology ontology : ontologies) {
    // ontologyAxioms.addAll(ontology.getAxioms());
    // }
    // for (OWLAxiom axiom : ontologyAxioms) {
    // if (axiom.getAxiomType() != AxiomType.DECLARATION) {
    // OWLOntologyAnnotationClusterDetector visitor = new
    // OWLOntologyAnnotationClusterDetector(
    // cluster, ontologies);
    // Set<OWLEntity> signature = new HashSet<OWLEntity>(axiom.getSignature());
    // signature.retainAll(cluster);
    // if (!signature.isEmpty() || axiom.accept(visitor)) {
    // generalisation.clearSubstitutions();
    // OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
    // generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
    // generalisation.getSubstitutions()));
    // }
    // }
    // }
    // return generalisationMap;
    // }
    /**
     * @param cluster cluster
     * @param ontologies ontologies
     * @param axioms axioms
     * @param generalisation generalisation
     * @return generalisation map
     */
    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
        Collection<OWLEntity> cluster, Collection<OWLOntology> ontologies, Stream<OWLAxiom> axioms,
        OWLObjectGeneralisation generalisation) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
        axioms.forEach(axiom -> generaliseAxiom(cluster, ontologies, generalisation,
            generalisationMap, axiom));
        return generalisationMap;
    }

    protected static void generaliseAxiom(Collection<OWLEntity> cluster,
        Collection<OWLOntology> ontologies, OWLObjectGeneralisation generalisation,
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap, OWLAxiom axiom) {
        if (axiom.getAxiomType() != AxiomType.DECLARATION
            && axiom.getAxiomType() != AxiomType.ANNOTATION_ASSERTION) {
            OWLOntologyAnnotationClusterDetector visitor =
                new OWLOntologyAnnotationClusterDetector(cluster, ontologies);
            boolean intersection = axiom.signature().anyMatch(cluster::contains);
            if (intersection || axiom.accept(visitor).booleanValue()) {
                generalisation.clearSubstitutions();
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(generalised,
                    new OWLAxiomInstantiation(axiom, generalisation.getSubstitutions()));
            }
        }
    }

    /**
     * @param owlObject owlObject
     * @param instantiations instantiations
     * @return true if appears
     */
    public static boolean appears(OWLObject owlObject,
        Collection<? extends OWLAxiomInstantiation> instantiations) {
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

    /**
     * @param cluster cluster
     * @param generalisationMap generalisationMap
     * @return cluster breakdown
     */
    public static MultiMap<OWLObject, OWLAxiom> buildClusterAppearanceBreakdown(
        Collection<? extends OWLObject> cluster,
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        MultiMap<OWLObject, OWLAxiom> toReturn = new MultiMap<>();
        Set<OWLAxiom> keySet = generalisationMap.keySet();
        for (OWLObject owlObject : cluster) {
            for (OWLAxiom owlAxiom : keySet) {
                Collection<OWLAxiomInstantiation> instantiations = generalisationMap.get(owlAxiom);
                if (appears(owlObject, instantiations)) {
                    toReturn.put(owlObject, owlAxiom);
                }
            }
        }
        return toReturn;
    }

    /**
     * @param constraintSystem constraintSystem
     * @param runtimeExceptionHandler runtimeExceptionHandler
     * @param generalisationMap generalisationMap
     */
    public static void pruneGeneralisationMap(ConstraintSystem constraintSystem,
        RuntimeExceptionHandler runtimeExceptionHandler,
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        Set<OWLAxiom> generalisations = new HashSet<>(generalisationMap.keySet());
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
            new ManchesterOWLSyntaxOWLObjectRendererImpl();
        ToStringRenderer.setRenderer(() -> renderer);
        for (OWLAxiom axiom : generalisations) {
            // if (axiom.toString().contains("cluster_1 ")) {
            // System.out.println(axiom);
            // }
            Collection<OWLAxiomInstantiation> instantiations = generalisationMap.get(axiom);
            if (instantiations.size() == 1) {
                // A single instantiation is not a generalisation hence I
                // will
                // remove this from the map
                generalisationMap.remove(axiom);
            } else {
                Map<Variable<?>, Set<OWLObject>> map = new HashMap<>();
                for (OWLAxiomInstantiation instantiation : instantiations) {
                    AssignmentMap substitutions = instantiation.getSubstitutions();
                    for (Variable<?> variable : substitutions.keySet()) {
                        Set<OWLObject> values = substitutions.get(variable);
                        if (values.size() == 1) {
                            Set<OWLObject> set = map.get(variable);
                            if (set == null) {
                                set = new HashSet<>();
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
                        bindingNode
                            .addAssignment(new Assignment(variable, values.iterator().next()));
                    }
                }
                if (!bindingNode.getAssignments().isEmpty()) {
                    constraintSystem.setLeaves(Collections.singleton(bindingNode));
                    PartialOWLObjectInstantiator instantiator =
                        new PartialOWLObjectInstantiator(new SimpleValueComputationParameters(
                            constraintSystem, bindingNode, runtimeExceptionHandler));
                    OWLAxiom instantiation = (OWLAxiom) axiom.accept(instantiator);
                    VariableExtractor variableExtractor =
                        new VariableExtractor(constraintSystem, false);
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
                            AssignmentMap substitutions = oldInstantiation.getSubstitutions();
                            bindingNode.assignedVariables().forEach(substitutions::remove);
                            generalisationMap.put(instantiation, new OWLAxiomInstantiation(
                                oldInstantiation.getAxiom(), substitutions));
                        }
                    }
                    // Clean up the constraint system
                    constraintSystem.setLeaves(null);
                }
            }
        }
    }

    /**
     * Flattens out a collection of collections of items turning it into the union of its members.
     * 
     * @param <P> type
     * @param collections collections The collection of collections. Cannot be {@code null}.
     * @return A Set.
     * @throws NullPointerException NullPointerException if the input is {@code null}.
     */
    public static <P extends OWLObject> Set<P> flatten(
        Collection<? extends Collection<? extends P>> collections) {
        if (collections == null) {
            throw new NullPointerException("The starting colleciton of collections cannot be null");
        }
        Set<P> toReturn = new HashSet<>();
        for (Collection<? extends P> collection : collections) {
            toReturn.addAll(collection);
        }
        return toReturn;
    }

    /**
     * @param assignmentMap assignmentMap
     * @return rendering
     */
    public static String renderInstantiationsStats(AssignmentMap assignmentMap) {
        Formatter formatter = new Formatter();
        Iterator<Variable<?>> iterator = assignmentMap.keySet().iterator();
        while (iterator.hasNext()) {
            Variable<?> v = iterator.next();
            formatter.format("%s count: %d%s", v.getName(),
                Integer.valueOf(assignmentMap.get(v).size()), iterator.hasNext() ? "; " : "");
        }
        return formatter.toString();
    }

    /**
     * @param instantiations instantiations
     * @return assignment map
     */
    public static AssignmentMap buildAssignmentMap(
        Collection<? extends OWLAxiomInstantiation> instantiations) {
        AssignmentMap toReturn = new AssignmentMap(Collections.<BindingNode>emptySet());
        for (OWLAxiomInstantiation instantiation : instantiations) {
            AssignmentMap substitutions = instantiation.getSubstitutions();
            substitutions.variables()
                .forEach(variable -> toReturn.computeIfAbsent(variable, x -> new HashSet<>())
                    .addAll(substitutions.get(variable)));
        }
        return toReturn;
    }

    /**
     * @param instantiations instantiations
     * @return axioms
     */
    public static Set<OWLAxiom> extractAxioms(
        Collection<? extends OWLAxiomInstantiation> instantiations) {
        if (instantiations == null) {
            throw new NullPointerException("The instantiation collection cannot be null");
        }
        Set<OWLAxiom> toReturn = new HashSet<>(instantiations.size());
        for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
            toReturn.add(owlAxiomInstantiation.getAxiom());
        }
        return toReturn;
    }

    /**
     * @param model model
     * @param <C> type
     * @return generalisation map
     */
    public static <C extends Set<OWLEntity>> MultiMap<OWLAxiom, OWLAxiomInstantiation> extractGeneralisationMap(
        RegularitiesDecompositionModel<C, OWLEntity> model) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<>();
        for (C c : model.getClusterList()) {
            toReturn.putAll(model.get(c));
        }
        return toReturn;
    }

    /**
     * @param cluster cluster
     * @param generalisationMap generalisationMap
     * @return variable
     */
    public static Variable<?> getVariable(Collection<? extends OWLEntity> cluster,
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        boolean found = false;
        Variable<?> toReturn = null;
        Iterator<OWLAxiom> iterator = generalisationMap.keySet().iterator();
        while (!found && iterator.hasNext()) {
            OWLAxiom generalisation = iterator.next();
            Iterator<OWLAxiomInstantiation> anotherIterator =
                generalisationMap.get(generalisation).iterator();
            while (!found && anotherIterator.hasNext()) {
                OWLAxiomInstantiation owlAxiomInstantiation = anotherIterator.next();
                AssignmentMap substitutions = owlAxiomInstantiation.getSubstitutions();
                Iterator<Variable<?>> yetAnotherIterator = substitutions.keySet().iterator();
                while (!found && yetAnotherIterator.hasNext()) {
                    Variable<?> variable = yetAnotherIterator.next();
                    Set<OWLObject> values = new HashSet<>(substitutions.get(variable));
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

    /**
     * @param cluster cluster
     * @return rendering
     */
    public static String render(Collection<? extends DistanceTableObject<OWLEntity>> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends DistanceTableObject<OWLEntity>> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
                new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next().getObject();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
    }

    /**
     * @param cluster cluster
     * @return rendering
     */
    public static String renderManchester(
        Collection<? extends DistanceTableObject<OWLEntity>> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends DistanceTableObject<OWLEntity>> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
                new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next().getObject();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
    }

    /**
     * @param clusteringMatrix clusteringMatrix
     * @param distanceMatrix distanceMatrix
     * @param equivalenceClasses equivalenceClasses
     * @param <P> type
     * @return clusters
     */
    public static <P> Set<Cluster<P>> buildClusters(
        ClusteringProximityMatrix<DistanceTableObject<P>> clusteringMatrix,
        ProximityMatrix<P> distanceMatrix, MultiMap<P, P> equivalenceClasses) {
        Collection<Collection<? extends DistanceTableObject<P>>> objects =
            clusteringMatrix.getObjects();
        Set<Cluster<P>> clusters = new HashSet<>(objects.size());
        for (Collection<? extends DistanceTableObject<P>> collection : objects) {
            clusters.add(new SimpleCluster<>(Utility.unwrapObjects(collection, equivalenceClasses),
                distanceMatrix));
        }
        for (P key : equivalenceClasses.keySet()) {
            Collection<P> set = equivalenceClasses.get(key);
            if (set.size() > 1) {
                boolean found = false;
                Iterator<Cluster<P>> iterator = clusters.iterator();
                while (!found && iterator.hasNext()) {
                    Cluster<P> cluster = iterator.next();
                    found = cluster.contains(key);
                }
                if (!found) {
                    clusters.add(new SimpleCluster<>(set, distanceMatrix));
                }
            }
        }
        Set<Cluster<P>> toReturn = new HashSet<>();
        for (Cluster<P> c : clusters) {
            if (c.size() > 1) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }

    /**
     * @param model model
     * @param file file
     * @param <P> type
     * @param <C> set
     * @throws ParserConfigurationException ParserConfigurationException
     * @throws TransformerFactoryConfigurationError TransformerFactoryConfigurationError
     * @throws TransformerException TransformerException
     */
    public static <C extends Set<P>, P extends OWLEntity> void saveToXML(
        RegularitiesDecompositionModel<C, P> model, File file) throws ParserConfigurationException,
        TransformerFactoryConfigurationError, TransformerException {
        List<C> clusterList = model.getClusterList();
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
            new ManchesterOWLSyntaxOWLObjectRendererImpl();
        renderer.setShortFormProvider(new OPPLShortFormProvider(new SimpleShortFormProvider()));
        Document document = toXML(model, clusterList, renderer);
        Transformer t = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(file);
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        t.transform(new DOMSource(document), result);
        System.out.println(String.format("Results saved in %s", file.getAbsolutePath()));
    }

    private static <C extends Set<P>, P extends OWLEntity> Document toXML(
        RegularitiesDecompositionModel<C, P> model, List<C> clusterList,
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer) throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("Clusters");
        document.appendChild(root);
        for (C cluster : clusterList) {
            Element clusterNode = document.createElement("Cluster");
            root.appendChild(clusterNode);
            for (P o : cluster) {
                Element itemNode = document.createElement("Item");
                itemNode.setAttribute("iri", o.getIRI().toString());
                clusterNode.appendChild(itemNode);
            }
            clusterNode.setAttribute("size", Integer.toString(cluster.size()));
            Element generalisations = document.createElement("Generalisations");
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = model.get(cluster);
            for (OWLAxiom generalisedAxiom : generalisationMap.keySet()) {
                Element generalisedAxiomNode = document.createElement("Generalisation");
                generalisedAxiomNode.setAttribute("axiom", renderer.render(generalisedAxiom));
                Collection<OWLAxiomInstantiation> generalisationChildren =
                    generalisationMap.get(generalisedAxiom);
                generalisedAxiomNode.setAttribute("count",
                    Integer.toString(generalisationChildren.size()));
                generalisedAxiomNode.setAttribute("instantiationStats",
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
        return document;
    }

    /**
     * It saves the clusters in an xml. In this version the history is not saved.
     * 
     * @param _clusters _clusters
     * @param manager manager
     * @param file file
     * @param <P> type
     */
    public static <P extends OWLEntity> void save(Collection<? extends Cluster<P>> _clusters,
        OWLOntologyManager manager, File file) {
        try {
            List<Cluster<P>> sortedClusters = new ArrayList<>(_clusters.size());
            for (Cluster<P> c : _clusters) {
                if (c.size() > 1) {
                    sortedClusters.add(c);
                }
            }
            Collections.sort(sortedClusters, ClusterStatisticsTableModel.SIZE_COMPARATOR);
            List<OWLOntology> ontologies = asList(manager.ontologies());
            OWLOntology ontology = ontologies.get(0);
            ConstraintSystem constraintSystem =
                new OPPLFactory(manager, ontology, null).createConstraintSystem();
            OWLObjectGeneralisation generalisation =
                Utils.getOWLObjectGeneralisation(sortedClusters, ontologies, constraintSystem);
            printExtraStats(file, sortedClusters);
            Document xml = Utils.toXML(sortedClusters, ontologies,
                new ManchesterOWLSyntaxOWLObjectRendererImpl(), generalisation);
            Transformer t = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(file);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            t.transform(new DOMSource(xml), result);
            System.out.println(String.format("Results saved in %s", file.getAbsolutePath()));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (OPPLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file file
     * @param sortedClusters sortedClusters
     * @param <P> type
     */
    public static <P> void printExtraStats(File file, Collection<Cluster<P>> sortedClusters) {
        try (PrintStream out = new PrintStream(file.getName() + ".csv")) {
            int index = 0;
            out.println("cluster index, cluster size, "
                + "average internal distance, average external distance, "
                + "max internal distance, min internal distance, "
                + "max external distance, min external distance, homogeneity");
            for (Cluster<P> cluster : sortedClusters) {
                ClusterStatistics<P> stats = ClusterStatistics.buildStatistics(cluster);
                out.print("cluster " + index);
                index++;
                out.println("," + cluster.size() + "," + stats.getAverageInternalDistance() + ","
                    + stats.getAverageExternalDistance() + "," + stats.getMaxInternalDistance()
                    + "," + stats.getMinInternalDistance() + "," + stats.getMaxExternalDistance()
                    + "," + stats.getMinExternalDistance() + ","
                    + (1 - stats.getAverageInternalDistance()));
            }
        } catch (IOException e) {
            System.out.println(
                "AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll.save() Cannot save extra metrics for "
                    + file.getName());
            e.printStackTrace(System.out);
        }
    }

    /**
     * Builds the cluster decomposition model when detecting syntactic regularities
     * 
     * @param _clusters _clusters
     * @param ontologies ontologies
     * @param generalisation generalisation
     * @param <P> type
     * @return cluster decomposition model
     */
    public static <P extends OWLEntity> ClusterDecompositionModel<P> toClusterDecompositionModel(
        Collection<? extends Cluster<P>> _clusters, Collection<OWLOntology> ontologies,
        OWLObjectGeneralisation generalisation) {
        ClusterDecompositionModel<P> model = new ClusterDecompositionModel<>(_clusters, ontologies);
        for (Cluster<P> cluster : _clusters) {
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap =
                Utils.buildGeneralisationMap((Collection<OWLEntity>) cluster, ontologies,
                    generalisation);
            model.put(cluster, generalisationMap);
        }
        return model;
    }

    /**
     * @param _clusters _clusters
     * @param ontologies ontologies
     * @param axioms axioms
     * @param generalisation generalisation
     * @param <P> type
     * @return cluster decomposition model
     */
    public static <P extends OWLEntity> ClusterDecompositionModel<P> toKnowledgeExplorerClusterDecompositionModel(
        Collection<? extends Cluster<P>> _clusters, Collection<? extends OWLOntology> ontologies,
        Set<OWLAxiom> axioms, OWLObjectGeneralisation generalisation) {
        ClusterDecompositionModel<P> model = new ClusterDecompositionModel<>(_clusters, ontologies);
        for (Cluster<P> cluster : _clusters) {
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap =
                Utils.buildKnowledgeExplorerGeneralisationMap(cluster, generalisation, axioms);
            model.put(cluster, generalisationMap);
        }
        return model;
    }

    /**
     * @param o o
     * @return signature
     */
    public static List<OWLEntity> getSortedSignature(OWLOntology o) {
        return asList(o.signature(Imports.INCLUDED).distinct().sorted(new EntityComparator()));
    }

    /**
     * @param axioms axioms
     * @return signature
     */
    public static List<OWLEntity> getSortedSignature(Set<OWLAxiom> axioms) {
        return asList(
            axioms.stream().flatMap(OWLAxiom::signature).distinct().sorted(new EntityComparator()));
    }

    /**
     * @param _entities _entities
     * @return sgnature
     */
    public static Set<OWLEntity> sortSignature(Set<OWLEntity> _entities) {
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        entities.addAll(_entities);
        return entities;
    }

    /**
     * @param manager manager
     * @return renderer
     */
    public static org.coode.utils.owl.ManchesterSyntaxRenderer enableLabelRendering(
        OWLOntologyManager manager) {
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        ShortFormProvider shortFormProvider = new OPPLShortFormProvider(
            new AnnotationValueShortFormProvider(Arrays.asList(dataFactory.getRDFSLabel()),
                Collections.<OWLAnnotationProperty, List<String>>emptyMap(), manager));
        org.coode.utils.owl.ManchesterSyntaxRenderer renderer =
            new org.coode.utils.owl.ManchesterSyntaxRenderer();
        // ManchesterOWLSyntaxObjectRenderer renderer = new
        // ManchesterOWLSyntaxObjectRenderer(
        // new OutputStreamWriter(System.out),
        // new AnnotationValueShortFormProvider(Arrays.asList(dataFactory
        // .getRDFSLabel()), Collections
        // .<OWLAnnotationProperty, List<String>> emptyMap(),
        // manager));
        //
        // ToStringRenderer.getInstance().setRenderer(renderer);
        renderer.setShortFormProvider(shortFormProvider);
        return renderer;
    }
}
