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
package org.coode.distance.entityrelevance.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.distance.entityrelevance.AbstractRankingRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.Ranking;
import org.coode.metrics.RankingSlot;
import org.coode.metrics.owl.OWLEntityPopularityRanking;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author eleni */
public final class ByKindOWLEntityPopularityBasedRelevantPolicy
    implements RelevancePolicy<OWLEntity> {
    private final class GroupRanking implements Ranking<OWLEntity> {
        public GroupRanking() {}

        @Override
        public double getTopValue() {
            return getMaxValue();
        }

        @Override
        public double getBottomValue() {
            // XXX this mymics a bug in the base ranking slot
            return getMaxValue();
        }

        @Override
        public OWLEntity[] getTop() {
            return getMaxValues();
        }

        @Override
        public OWLEntity[] getBottom() {
            // XXX this mymics a bug in the base ranking slot
            return getMaxValues();
        }

        @Override
        public double[] getValues() {
            Set<Double> values = new HashSet<>();
            if (owlClassesPopularityRanking != null) {
                owlClassesPopularityRanking.getRanking().collect(values);
            }
            if (owlObjectPropertiesPopularityRanking != null) {
                owlObjectPropertiesPopularityRanking.getRanking().collect(values);
            }
            if (owlDataPropertiesPopularityRanking != null) {
                owlDataPropertiesPopularityRanking.getRanking().collect(values);
            }
            if (owlAnnotationPropertiesPopularityRanking != null) {
                owlAnnotationPropertiesPopularityRanking.getRanking().collect(values);
            }
            if (owlNamedIndividualsPopularityRanking != null) {
                owlNamedIndividualsPopularityRanking.getRanking().collect(values);
            }
            if (owlDatatypesPopularityRanking != null) {
                owlDatatypesPopularityRanking.getRanking().collect(values);
            }
            double[] doubles = new double[] {values.size()};
            Iterator<Double> it = values.iterator();
            int i = 0;
            while (it.hasNext()) {
                doubles[i++] = it.next();
            }
            return doubles;
        }

        public OWLEntity[] getMaxValues() {
            double d = getMaxValue();
            if (owlClassesPopularityRanking != null) {
                if (d == owlClassesPopularityRanking.getRanking().getTopValue()) {
                    return owlClassesPopularityRanking.getRanking().getTop();
                }
            }
            if (owlObjectPropertiesPopularityRanking != null) {
                if (d == owlObjectPropertiesPopularityRanking.getRanking().getTopValue()) {
                    return owlObjectPropertiesPopularityRanking.getRanking().getTop();
                }
            }
            if (owlDataPropertiesPopularityRanking != null) {
                if (d == owlDataPropertiesPopularityRanking.getRanking().getTopValue()) {
                    return owlDataPropertiesPopularityRanking.getRanking().getTop();
                }
            }
            if (owlAnnotationPropertiesPopularityRanking != null) {
                if (d == owlAnnotationPropertiesPopularityRanking.getRanking().getTopValue()) {
                    return owlAnnotationPropertiesPopularityRanking.getRanking().getTop();
                }
            }
            if (owlNamedIndividualsPopularityRanking != null) {
                if (d == owlNamedIndividualsPopularityRanking.getRanking().getTopValue()) {
                    return owlNamedIndividualsPopularityRanking.getRanking().getTop();
                }
            }
            if (owlDatatypesPopularityRanking != null) {
                if (d == owlDatatypesPopularityRanking.getRanking().getTopValue()) {
                    return owlDatatypesPopularityRanking.getRanking().getTop();
                }
            }
            return null;
        }

        private double getMaxValue() {
            double d = 0d;
            if (owlClassesPopularityRanking != null) {
                d = owlClassesPopularityRanking.getRanking().getTopValue();
            }
            if (owlObjectPropertiesPopularityRanking != null) {
                double v = owlObjectPropertiesPopularityRanking.getRanking().getTopValue();
                if (d < v) {
                    d = v;
                }
            }
            if (owlDataPropertiesPopularityRanking != null) {
                double v = owlDataPropertiesPopularityRanking.getRanking().getTopValue();
                if (d < v) {
                    d = v;
                }
            }
            if (owlAnnotationPropertiesPopularityRanking != null) {
                double v = owlAnnotationPropertiesPopularityRanking.getRanking().getTopValue();
                if (d < v) {
                    d = v;
                }
            }
            if (owlNamedIndividualsPopularityRanking != null) {
                double v = owlNamedIndividualsPopularityRanking.getRanking().getTopValue();
                if (d < v) {
                    d = v;
                }
            }
            if (owlDatatypesPopularityRanking != null) {
                double v = owlDatatypesPopularityRanking.getRanking().getTopValue();
                if (d < v) {
                    d = v;
                }
            }
            return d;
        }

        @Override
        public boolean isAverageable() {
            return false;
        }

        @Override
        public double getAverageValue() {
            return 0;
        }

        @Override
        public List<RankingSlot<OWLEntity>> getUnorderedRanking() {
            List<RankingSlot<OWLEntity>> toReturn = new ArrayList<>();
            if (owlClassesPopularityRanking != null) {
                toReturn.addAll(owlClassesPopularityRanking.getRanking().getUnorderedRanking());
            }
            if (owlObjectPropertiesPopularityRanking != null) {
                toReturn.addAll(
                    owlObjectPropertiesPopularityRanking.getRanking().getUnorderedRanking());
            }
            if (owlDataPropertiesPopularityRanking != null) {
                toReturn
                    .addAll(owlDataPropertiesPopularityRanking.getRanking().getUnorderedRanking());
            }
            if (owlAnnotationPropertiesPopularityRanking != null) {
                toReturn.addAll(
                    owlAnnotationPropertiesPopularityRanking.getRanking().getUnorderedRanking());
            }
            if (owlNamedIndividualsPopularityRanking != null) {
                toReturn.addAll(
                    owlNamedIndividualsPopularityRanking.getRanking().getUnorderedRanking());
            }
            if (owlDatatypesPopularityRanking != null) {
                toReturn.addAll(owlDatatypesPopularityRanking.getRanking().getUnorderedRanking());
            }
            return toReturn;
        }

        @Override
        public List<RankingSlot<OWLEntity>> getSortedRanking() {
            List<RankingSlot<OWLEntity>> toReturn = getUnorderedRanking();
            Collections.sort(toReturn,
                Collections.reverseOrder(new Comparator<RankingSlot<OWLEntity>>() {
                    @Override
                    public int compare(RankingSlot<OWLEntity> o1, RankingSlot<OWLEntity> o2) {
                        double difference = o1.getValue() - o2.getValue();
                        return difference == 0 ? o1.getMembersHashCode() - o2.getMembersHashCode()
                            : (int) Math.signum(difference);
                    }
                }));
            return toReturn;
        }
    }

    protected final AbstractRankingRelevancePolicy<OWLEntity> owlClassesPopularityRanking;
    protected final AbstractRankingRelevancePolicy<OWLEntity> owlObjectPropertiesPopularityRanking;
    protected final AbstractRankingRelevancePolicy<OWLEntity> owlDataPropertiesPopularityRanking;
    protected final AbstractRankingRelevancePolicy<OWLEntity> owlAnnotationPropertiesPopularityRanking;
    protected final AbstractRankingRelevancePolicy<OWLEntity> owlNamedIndividualsPopularityRanking;
    protected final AbstractRankingRelevancePolicy<OWLEntity> owlDatatypesPopularityRanking;

    /**
     * @param objects objects
     * @param ontologies ontologies
     */
    public ByKindOWLEntityPopularityBasedRelevantPolicy(Collection<OWLEntity> objects,
        Collection<OWLOntology> ontologies) {
        if (objects == null) {
            throw new NullPointerException("The collection of obejcts cannot be null");
        }
        if (ontologies == null) {
            throw new NullPointerException("The ontology collection cannot be null");
        }
        Set<OWLEntity> owlClasses = new HashSet<>();
        Set<OWLEntity> owlObjectProperties = new HashSet<>();
        Set<OWLEntity> owlDataProperties = new HashSet<>();
        Set<OWLEntity> owlAnnotationProperties = new HashSet<>();
        Set<OWLEntity> owlNamedIndividuals = new HashSet<>();
        Set<OWLEntity> owlDatatypes = new HashSet<>();
        for (OWLEntity owlEntity : objects) {
            if (owlEntity.isOWLClass()) {
                owlClasses.add(owlEntity.asOWLClass());
            }
            if (owlEntity.isOWLObjectProperty()) {
                owlObjectProperties.add(owlEntity.asOWLObjectProperty());
            }
            if (owlEntity.isOWLDataProperty()) {
                owlDataProperties.add(owlEntity.asOWLDataProperty());
            }
            if (owlEntity.isOWLAnnotationProperty()) {
                owlAnnotationProperties.add(owlEntity.asOWLAnnotationProperty());
            }
            if (owlEntity.isOWLNamedIndividual()) {
                owlNamedIndividuals.add(owlEntity.asOWLNamedIndividual());
            }
            if (owlEntity.isOWLDatatype()) {
                owlDatatypes.add(owlEntity.asOWLDatatype());
            }
        }
        owlClassesPopularityRanking = owlClasses.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlClasses, ontologies));
        owlObjectPropertiesPopularityRanking = owlObjectProperties.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlObjectProperties, ontologies));
        owlDataPropertiesPopularityRanking = owlDataProperties.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlDataProperties, ontologies));
        owlAnnotationPropertiesPopularityRanking = owlAnnotationProperties.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlAnnotationProperties, ontologies));
        owlNamedIndividualsPopularityRanking = owlNamedIndividuals.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlNamedIndividuals, ontologies));
        owlDatatypesPopularityRanking = owlDatatypes.isEmpty() ? null
            : AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(owlDatatypes, ontologies));
    }

    @Override
    public boolean isRelevant(OWLEntity object) {
        return object.accept(new OWLEntityVisitorEx<Boolean>() {
            @Override
            public Boolean visit(OWLClass cls) {
                return owlClassesPopularityRanking != null
                    && owlClassesPopularityRanking.computeIsRelevant(cls);
            }

            @Override
            public Boolean visit(OWLObjectProperty property) {
                return owlObjectPropertiesPopularityRanking != null
                    && owlObjectPropertiesPopularityRanking.computeIsRelevant(property);
            }

            @Override
            public Boolean visit(OWLDataProperty property) {
                return owlDataPropertiesPopularityRanking != null
                    && owlDataPropertiesPopularityRanking.computeIsRelevant(property);
            }

            @Override
            public Boolean visit(OWLNamedIndividual individual) {
                return owlNamedIndividualsPopularityRanking != null
                    && owlNamedIndividualsPopularityRanking.computeIsRelevant(individual);
            }

            @Override
            public Boolean visit(OWLDatatype datatype) {
                return owlDatatypesPopularityRanking != null
                    && owlDatatypesPopularityRanking.computeIsRelevant(datatype);
            }

            @Override
            public Boolean visit(OWLAnnotationProperty property) {
                return owlAnnotationPropertiesPopularityRanking != null
                    && owlAnnotationPropertiesPopularityRanking.computeIsRelevant(property);
            }
        });
    }

    /** @return ranking */
    public Ranking<OWLEntity> getRanking() {
        return new GroupRanking();
    }

    /** @return the owlClassesPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOWLClassesPopularityRanking() {
        return owlClassesPopularityRanking;
    }

    /** @return the owlObjectPropertiesPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOWLObjectPropertiesPopularityRanking() {
        return owlObjectPropertiesPopularityRanking;
    }

    /** @return the owlDataPropertiesPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOWLDataPropertiesPopularityRanking() {
        return owlDataPropertiesPopularityRanking;
    }

    /** @return the owlAnnotationPropertiesPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOwlAnnotationPropertiesPopularityRanking() {
        return owlAnnotationPropertiesPopularityRanking;
    }

    /** @return the owlNamedIndividualsPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOWLNamedIndividualsPopularityRanking() {
        return owlNamedIndividualsPopularityRanking;
    }

    /** @return the owlDatatypesPopularityRanking */
    public AbstractRankingRelevancePolicy<OWLEntity> getOWLDatatypesPopularityRanking() {
        return owlDatatypesPopularityRanking;
    }
}
