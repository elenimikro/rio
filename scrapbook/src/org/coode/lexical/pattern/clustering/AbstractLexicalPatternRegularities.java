package org.coode.lexical.pattern.clustering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.coode.oppl.utils.OWLObjectExtractor;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.owl.generalise.UnwrappedOWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.LexicalClusterModel;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.ShortFormProvider;

import experiments.ExperimentHelper;

public class AbstractLexicalPatternRegularities {

	private final OWLOntology onto;
	private final MultiMap<String, OWLEntity> keyMap = new MultiMap<String, OWLEntity>();
	private final LexicalClusterModel model;

	public AbstractLexicalPatternRegularities(OWLOntology ontology,
			MultiMap<String, OWLEntity> lexicalPatternMap) {
		this.onto = ontology;
		this.keyMap.putAll(lexicalPatternMap);
		model = new LexicalClusterModel(keyMap, onto);
	}

	public AbstractLexicalPatternRegularities(OWLOntology ontology,
			Set<String> lexicalPatterns) {
		this.onto = ontology;
		buildKeyMap(lexicalPatterns);
		model = new LexicalClusterModel(keyMap, onto);
	}

	private void buildKeyMap(Set<String> lexicalPatterns) {
		for (String keyword : lexicalPatterns) {
			keyMap.putAll(keyword, extractKeywordEntities(keyword));
		}

	}

	public Collection<OWLEntity> extractKeywordEntities(String keyword) {
		ManchesterSyntaxRenderer renderer = ExperimentHelper
				.setManchesterSyntaxWithLabelRendering(onto
						.getOWLOntologyManager());
		Set<OWLEntity> target = new HashSet<OWLEntity>();
		Set<OWLEntity> entities = onto.getSignature();
		for (OWLEntity e : entities) {
			if (renderer.render(e).toLowerCase().indexOf(keyword) != -1) {
				target.add(e);
			}
		}
		return target;
	}

	public LexicalClusterModel getAxiomRegularitiesFromLexicalPatterns() {
		OPPLFactory opplfactory = new OPPLFactory(onto.getOWLOntologyManager(),
				onto, null);
		ConstraintSystem constraintSystem = opplfactory
				.createConstraintSystem();
		ManchesterSyntaxRenderer renderer = enableLabelRendering(onto
				.getOWLOntologyManager());
		final Map<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>> toReturn = new HashMap<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>>();
		try {
			Set<BindingNode> bindings = preloadConstants(constraintSystem);
			RuntimeExceptionHandler runtimeExceptionHandler = new QuickFailRuntimeExceptionHandler();

			Set<OWLAxiom> axioms = extractLexicalPatternsUsage();
			OWLObjectGeneralisation owlObjectGeneralisation = getOWLObjectGeneralisation(
					constraintSystem, renderer, bindings);
			for (String keyword : keyMap.keySet()) {
				Collection<OWLEntity> cluster = keyMap.get(keyword);

				MultiMap<OWLAxiom, OWLAxiomInstantiation> map = buildGeneralisationMap(
						cluster, axioms, owlObjectGeneralisation,
						runtimeExceptionHandler);
				MultiMap<OWLAxiom, OWLAxiomInstantiation> sortedGeneralisationMap = sortGeneralisations(map);
				toReturn.put(keyword, sortedGeneralisationMap);
				model.put(keyword, sortedGeneralisationMap);
			}

		} catch (OPPLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	private Set<BindingNode> preloadConstants(ConstraintSystem constraintSystem)
			throws OPPLException {
		// I need to preload all the constants into a variable before I
		// start
		Set<BindingNode> bindings = new HashSet<BindingNode>(keyMap
				.getAllValues().size());
		Set<OWLLiteral> constants = new HashSet<OWLLiteral>();
		for (OWLOntology ontology : onto.getImportsClosure()) {
			Set<OWLAxiom> axioms = ontology.getAxioms();
			for (OWLAxiom axiom : axioms) {
				constants.addAll(OWLObjectExtractor.getAllOWLLiterals(axiom));
			}
		}
		if (!constants.isEmpty()) {
			String constantVariableName = "?constant";
			InputVariable<?> constantVariable = constraintSystem
					.createVariable(constantVariableName,
							VariableTypeFactory.getCONSTANTVariableType(), null);
			for (OWLLiteral owlLiteral : constants) {
				BindingNode bindingNode = BindingNode
						.createNewEmptyBindingNode();
				bindingNode.addAssignment(new Assignment(constantVariable,
						owlLiteral));
				bindings.add(bindingNode);
			}
		}
		return bindings;
	}

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> sortGeneralisations(
			final MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		// order the generalisation map according to size
		MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		Comparator<OWLAxiom> size_comparator = new Comparator<OWLAxiom>() {
			@Override
			public int compare(OWLAxiom axiom, OWLAxiom otherAxiom) {
				Collection<OWLAxiomInstantiation> axiomInstatiations = map
						.get(axiom);
				Collection<OWLAxiomInstantiation> otherAxiomInstantiations = map
						.get(otherAxiom);
				return axiomInstatiations.size()
						- otherAxiomInstantiations.size();
			}
		};

		Set<OWLAxiom> orderedGenSet = new TreeSet<OWLAxiom>(
				Collections.reverseOrder(size_comparator));
		orderedGenSet.addAll(map.keySet());

		for (OWLAxiom ax : orderedGenSet) {
			toReturn.putAll(ax, map.get(ax));
		}
		return toReturn;
	}

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
			Collection<OWLEntity> cluster, Set<OWLAxiom> axioms,
			OWLObjectGeneralisation generalisation,
			RuntimeExceptionHandler runtimeExceptionHandler) {
		MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		// OWLOntologyAnnotationClusterDetector visitor = new
		// OWLOntologyAnnotationClusterDetector(
		// cluster, ontologies);

		// TODO: put a cache here.
		for (OWLAxiom axiom : axioms) {
			if (axiom.getAxiomType() != AxiomType.DECLARATION
					&& axiom.getAxiomType() != AxiomType.ANNOTATION_ASSERTION
					&& axiom.getAxiomType() != AxiomType.ANNOTATION_PROPERTY_DOMAIN
					&& axiom.getAxiomType() != AxiomType.ANNOTATION_PROPERTY_RANGE) {
				Set<OWLEntity> signature = new HashSet<OWLEntity>(
						axiom.getSignature());
				signature.retainAll(cluster);
				if (!signature.isEmpty()) {
					generalisation.clearSubstitutions();
					OWLAxiom generalised = (OWLAxiom) axiom
							.accept(generalisation);
					generalisationMap.put(
							generalised,
							new OWLAxiomInstantiation(axiom, generalisation
									.getSubstitutions()));
				}
			}
		}
		return generalisationMap;
	}

	private Set<OWLAxiom> extractLexicalPatternsUsage() {
		Set<OWLAxiom> usage = new HashSet<OWLAxiom>();
		for (OWLObject o : keyMap.getAllValues()) {
			usage.addAll(onto.getReferencingAxioms((OWLEntity) o));
		}
		return usage;
	}

	public OWLObjectGeneralisation getOWLObjectGeneralisation(
			ConstraintSystem constraintSystem,
			ManchesterSyntaxRenderer renderer, Set<BindingNode> bindings)
			throws OPPLException {

		for (String keyword : keyMap.keySet()) {
			Collection<OWLEntity> cluster = keyMap.get(keyword);
			if (!cluster.isEmpty()) {
				String name = keyword;
				// String name = getLCSVariableName(constraintSystem, renderer,
				// names, rootNames, axiomProvider, cluster);
				OWLObject object = cluster.iterator().next();
				VariableType<?> variableType = VariableTypeFactory
						.getVariableType(object);
				if (variableType != null) {
					String variableName = String
							.format("?%s", name.replaceAll("\\?", "_"))
							.replaceAll(
									ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP,
									"_");
					InputVariable<?> variable = constraintSystem
							.createVariable(variableName, variableType, null);
					for (OWLObject o : cluster) {
						BindingNode bindingNode = BindingNode
								.createNewEmptyBindingNode();
						if (VariableTypeFactory.getVariableType(o) == variable
								.getType()) {
							bindingNode.addAssignment(new Assignment(variable,
									o));
							bindings.add(bindingNode);
						}
					}
				}
			}
		}
		return new UnwrappedOWLObjectGeneralisation(bindings, constraintSystem);
	}

	private static ManchesterSyntaxRenderer enableLabelRendering(
			OWLOntologyManager manager) {
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		ManchesterSyntaxRenderer renderer = new ManchesterSyntaxRenderer();

		ShortFormProvider shortFormProvider = new AnnotationValueShortFormProvider(
				Arrays.asList(dataFactory.getRDFSLabel()),
				Collections.<OWLAnnotationProperty, List<String>> emptyMap(),
				manager);
		renderer.setShortFormProvider(shortFormProvider);
		// //
		// ToStringRenderer.getInstance().setRenderer(renderer);
		return renderer;
	}

}
