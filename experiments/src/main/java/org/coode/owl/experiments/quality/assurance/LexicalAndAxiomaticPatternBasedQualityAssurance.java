package org.coode.owl.experiments.quality.assurance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.basetest.ClusteringHelper;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.RegularitiesDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.MultiMap;

import experiments.ExperimentHelper;

/** @author eleni
 * @param <C>
 *            type */
public class LexicalAndAxiomaticPatternBasedQualityAssurance<C extends Set<OWLEntity>> {
    private final Set<OWLEntity> target = new HashSet<OWLEntity>();
    private final OWLOntology o;
    private final String keyword;
    private final Set<OWLAxiom> usage = new HashSet<OWLAxiom>();

    /** @param keyword
     *            keyword
     * @param o
     *            o */
    public LexicalAndAxiomaticPatternBasedQualityAssurance(String keyword, OWLOntology o) {
        this.o = o;
        this.keyword = keyword.toLowerCase();
    }

    /** @return target entities */
    public Set<OWLEntity> getTargetEntities() {
        if (target.isEmpty()) {
            ManchesterSyntaxRenderer renderer = ExperimentHelper
                    .setManchesterSyntaxWithLabelRendering(o.getOWLOntologyManager());
            Set<OWLClass> classesInSignature = o.getClassesInSignature();
            for (OWLClass c : classesInSignature) {
                if (renderer.render(c).toLowerCase().indexOf(keyword) != -1) {
                    target.add(c);
                }
            }
        }
        return target;
    }

    /** @return target entities usage */
    public Set<OWLAxiom> getTargetEntitiesUsage() {
        if (usage.isEmpty()) {
            for (OWLEntity e : target) {
                usage.addAll(e.getReferencingAxioms(o));
            }
        }
        return usage;
    }

    /** @param clusterList
     *            clusterList
     * @return target entities not in clusters */
    public Set<OWLEntity> getTargetEntitiesExcludedFromClusters(List<C> clusterList) {
        Set<OWLEntity> toReturn = new HashSet<OWLEntity>(target);
        for (C cl : clusterList) {
            toReturn.removeAll(cl);
        }
        return toReturn;
    }

    /** @param list
     *            list
     * @return clusters with target entities */
    public List<C> getClustersIncludingTargetEntities(List<C> list) {
        List<C> toReturn = new ArrayList<C>();
        for (int i = 0; i < list.size(); i++) {
            Set<OWLEntity> copy = new HashSet<OWLEntity>(target);
            copy.retainAll(list.get(i));
            if (!copy.isEmpty()) {
                toReturn.add(list.get(i));
            }
        }
        return toReturn;
    }

    /** @return regularities */
    public ClusterDecompositionModel<OWLEntity> getRegularities() {
        Set<OWLAnnotationAssertionAxiom> annotations = ExperimentHelper
                .stripOntologyFromAnnotationAssertions(o);
        ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
                .getSyntacticPopularityClusterModel(o);
        o.getOWLOntologyManager().addAxioms(o, annotations);
        return model;
    }

    /** @return regularities based on usage */
    public ClusterDecompositionModel<OWLEntity> getRegularitiesBasedOnUsage() {
        ClusterDecompositionModel<OWLEntity> model = null;
        try {
            OWLOntology onto = OWLManager.createOWLOntologyManager()
                    .createOntology(usage);
            Set<OWLAnnotationAssertionAxiom> annotations = ExperimentHelper
                    .stripOntologyFromAnnotationAssertions(onto);
            model = ClusteringHelper.getSyntacticPopularityClusterModel(onto);
            onto.getOWLOntologyManager().addAxioms(onto, annotations);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        return model;
    }

    /** @return keyword */
    public String getLexicalPattern() {
        return keyword;
    }

    /** @param model
     *            model
     * @return stats */
    public ArrayList<SimpleMetric<?>> getQualityAssuranceStats(
            RegularitiesDecompositionModel<C, OWLEntity> model) {
        ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
        Set<OWLEntity> targetEntities = getTargetEntities();
        metrics.add(new SimpleMetric<String>("Keyword", keyword));
        metrics.add(new SimpleMetric<Integer>("# target entities", targetEntities.size()));
        metrics.add(new SimpleMetric<Integer>("# referencing axioms of target entities",
                usage.size()));
        metrics.add(new SimpleMetric<Integer>("# clusters", model.getClusterList().size()));
        // # clusters that include the target entities
        List<C> targetClusters = getClustersIncludingTargetEntities(model
                .getClusterList());
        metrics.add(new SimpleMetric<Integer>(
                "# clusters that include the target entities", targetClusters.size()));
        // generalisations describing the target entities
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = ClusteringHelper
                .extractGeneralisationMap(model);
        metrics.add(new SimpleMetric<Integer>(
                "# generalisations describing the target entities", generalisationMap
                        .keySet().size()));
        // Number of instantiations referring to the target entities
        metrics.add(new SimpleMetric<Integer>(
                "# instantiations describing the target entities", generalisationMap
                        .getAllValues().size()));
        // Number of target entities that were excluded from clusters
        Set<OWLEntity> excludedFromClusters = getTargetEntitiesExcludedFromClusters(model
                .getClusterList());
        metrics.add(new SimpleMetric<Integer>("# target entities excluded from clusters",
                excludedFromClusters.size()));
        ManchesterSyntaxRenderer renderer = ExperimentHelper
                .setManchesterSyntaxWithLabelRendering(o.getOWLOntologyManager());
        Set<String> excludedFromClustersReadable = new HashSet<String>();
        for (OWLEntity e : excludedFromClusters) {
            excludedFromClustersReadable.add(renderer.render(e));
        }
        metrics.add(new SimpleMetric<String>("target entities excluded from clusters",
                excludedFromClustersReadable.toString().replaceAll("\t", " ")));
        return metrics;
    }
}
