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

public final class ByKindOWLEntityPopularityBasedRelevantPolicy implements
		RelevancePolicy<OWLEntity> {
	private final AbstractRankingRelevancePolicy<OWLEntity> owlClassesPopularityRanking;
	private final AbstractRankingRelevancePolicy<OWLEntity> owlObjectPropertiesPopularityRanking;
	private final AbstractRankingRelevancePolicy<OWLEntity> owlDataPropertiesPopularityRanking;
	private final AbstractRankingRelevancePolicy<OWLEntity> owlAnnotationPropertiesPopularityRanking;
	private final AbstractRankingRelevancePolicy<OWLEntity> owlNamedIndividualsPopularityRanking;
	private final AbstractRankingRelevancePolicy<OWLEntity> owlDatatypesPopularityRanking;

	public ByKindOWLEntityPopularityBasedRelevantPolicy(
			Collection<? extends OWLEntity> objects,
			Collection<? extends OWLOntology> ontologies) {
		if (objects == null) {
			throw new NullPointerException("The collection of obejcts cannot be null");
		}
		if (ontologies == null) {
			throw new NullPointerException("The ontology collection cannot be null");
		}
		Set<OWLClass> owlClasses = new HashSet<OWLClass>();
		Set<OWLObjectProperty> owlObjectProperties = new HashSet<OWLObjectProperty>();
		Set<OWLDataProperty> owlDataProperties = new HashSet<OWLDataProperty>();
		Set<OWLAnnotationProperty> owlAnnotationProperties = new HashSet<OWLAnnotationProperty>();
		Set<OWLNamedIndividual> owlNamedIndividuals = new HashSet<OWLNamedIndividual>();
		Set<OWLDatatype> owlDatatypes = new HashSet<OWLDatatype>();
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
		this.owlClassesPopularityRanking = owlClasses.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlClasses, ontologies));
		this.owlObjectPropertiesPopularityRanking = owlObjectProperties.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlObjectProperties, ontologies));
		this.owlDataPropertiesPopularityRanking = owlDataProperties.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlDataProperties, ontologies));
		this.owlAnnotationPropertiesPopularityRanking = owlAnnotationProperties.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlAnnotationProperties, ontologies));
		this.owlNamedIndividualsPopularityRanking = owlNamedIndividuals.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlNamedIndividuals, ontologies));
		this.owlDatatypesPopularityRanking = owlDatatypes.isEmpty() ? null
				: AbstractRankingRelevancePolicy
						.getAbstractRankingRelevancePolicy(new OWLEntityPopularityRanking(
								owlDatatypes, ontologies));
	}

	public boolean isRelevant(OWLEntity object) {
		return object.accept(new OWLEntityVisitorEx<Boolean>() {
			public Boolean visit(OWLClass cls) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking
								.computeIsRelevant(cls);
			}

			public Boolean visit(OWLObjectProperty property) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking
								.computeIsRelevant(property);
			}

			public Boolean visit(OWLDataProperty property) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking
								.computeIsRelevant(property);
			}

			public Boolean visit(OWLNamedIndividual individual) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking
								.computeIsRelevant(individual);
			}

			public Boolean visit(OWLDatatype datatype) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking
								.computeIsRelevant(datatype);
			}

			public Boolean visit(OWLAnnotationProperty property) {
				return ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking != null
						&& ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking
								.computeIsRelevant(property);
			}
		});
	}

	public Ranking<OWLEntity, Double> getRanking() {
		return new Ranking<OWLEntity, Double>() {
			public Double getTopValue() {
				List<RankingSlot<OWLEntity, Double>> ranking = this.getRanking();
				return ranking.isEmpty() ? null : ranking.get(0).getValue();
			}

			public Double getBottomValue() {
				List<RankingSlot<OWLEntity, Double>> ranking = this.getRanking();
				return ranking.isEmpty() ? null : ranking.get(ranking.size() - 1)
						.getValue();
			}

			public Set<OWLEntity> getTop() {
				List<RankingSlot<OWLEntity, Double>> ranking = this.getRanking();
				return ranking.isEmpty() ? null : ranking.get(0).getMembers();
			}

			public Set<OWLEntity> getBottom() {
				List<RankingSlot<OWLEntity, Double>> ranking = this.getRanking();
				return ranking.isEmpty() ? null : ranking.get(ranking.size() - 1)
						.getMembers();
			}

			public Set<Double> getValues() {
				Set<Double> values = new HashSet<Double>();
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking
							.getRanking().getValues());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking
							.getRanking().getValues());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking
							.getRanking().getValues());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking
							.getRanking().getValues());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking
							.getRanking().getValues());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking != null) {
					values.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking
							.getRanking().getValues());
				}
				return values;
			}

			public boolean isAverageable() {
				return false;
			}

			public Double getAverageValue() {
				return null;
			}

			public List<RankingSlot<OWLEntity, Double>> getRanking() {
				List<RankingSlot<OWLEntity, Double>> toReturn = new ArrayList<RankingSlot<OWLEntity, Double>>();
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlClassesPopularityRanking
							.getRanking().getRanking());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlObjectPropertiesPopularityRanking
							.getRanking().getRanking());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDataPropertiesPopularityRanking
							.getRanking().getRanking());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlAnnotationPropertiesPopularityRanking
							.getRanking().getRanking());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlNamedIndividualsPopularityRanking
							.getRanking().getRanking());
				}
				if (ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking != null) {
					toReturn.addAll(ByKindOWLEntityPopularityBasedRelevantPolicy.this.owlDatatypesPopularityRanking
							.getRanking().getRanking());
				}
				Collections.sort(toReturn, Collections
						.reverseOrder(new Comparator<RankingSlot<OWLEntity, Double>>() {
							public int compare(RankingSlot<OWLEntity, Double> o1,
									RankingSlot<OWLEntity, Double> o2) {
								double difference = o1.getValue() - o2.getValue();
								return difference == 0 ? o1.getMembers().hashCode()
										- o2.getMembers().hashCode() : (int) Math
										.signum(difference);
							}
						}));
				return toReturn;
			}
		};
	}

	/**
	 * @return the owlClassesPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOWLClassesPopularityRanking() {
		return this.owlClassesPopularityRanking;
	}

	/**
	 * @return the owlObjectPropertiesPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOWLObjectPropertiesPopularityRanking() {
		return this.owlObjectPropertiesPopularityRanking;
	}

	/**
	 * @return the owlDataPropertiesPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOWLDataPropertiesPopularityRanking() {
		return this.owlDataPropertiesPopularityRanking;
	}

	/**
	 * @return the owlAnnotationPropertiesPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOwlAnnotationPropertiesPopularityRanking() {
		return this.owlAnnotationPropertiesPopularityRanking;
	}

	/**
	 * @return the owlNamedIndividualsPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOWLNamedIndividualsPopularityRanking() {
		return this.owlNamedIndividualsPopularityRanking;
	}

	/**
	 * @return the owlDatatypesPopularityRanking
	 */
	public AbstractRankingRelevancePolicy<OWLEntity> getOWLDatatypesPopularityRanking() {
		return this.owlDatatypesPopularityRanking;
	}
}
