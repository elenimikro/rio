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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author Eleni Mikroyannidi
 * @param <O> type
 * @param <R> type
 */
public abstract class LeastCommonSubsumer<O extends OWLObject, R extends OWLObject> {
    private final OWLAxiomProvider axiomProvider;
    private final MultiMap<O, R> nodeParentIndex = new MultiMap<>();
    private final R defaultRoot;
    private final Set<OWLEntity> signature = new HashSet<>();

    /**
     * @param axiomProvider axiomProvider
     * @param defaultRoot defaultRoot
     */
    public LeastCommonSubsumer(OWLAxiomProvider axiomProvider, R defaultRoot) {
        if (axiomProvider == null) {
            throw new NullPointerException("The axiom provider cannot be null");
        }
        if (defaultRoot == null) {
            throw new NullPointerException("The default root cannot be null");
        }
        this.axiomProvider = axiomProvider;
        this.defaultRoot = defaultRoot;
        add(this.signature, axiomProvider.getSignature());
        this.rebuild();
    }

    protected abstract void rebuild();

    /**
     * @param anObject anObject
     * @param anotherObject anotherObject
     * @param others others
     * @return subsumer
     */
    public R get(O anObject, O anotherObject, O... others) {
        List<O> list = new ArrayList<>();
        list.add(anObject);
        list.add(anotherObject);
        for (O o : others) {
            list.add(o);
        }
        return this.get(list);
    }

    /**
     * @param objects objects
     * @return subsumer
     */
    public abstract R get(Collection<? extends O> objects);

    /** @return the axiomProvider */
    public OWLAxiomProvider getAxiomProvider() {
        return this.axiomProvider;
    }

    /**
     * @param node node
     * @return parents
     */
    public Stream<R> getParents(O node) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        Collection<R> parents = this.nodeParentIndex.get(node);
        // If the parents are null I will return the default root only if the
        // node is in the signature of the axiom provider.
        Stream<R> toReturn;
        if (parents.isEmpty()) {
            if (node.equals(this.getDefaultRoot())) {
                toReturn = Stream.empty();
            } else {
                if (this.signature.contains(node)) {
                    toReturn = Stream.of(this.getDefaultRoot());
                } else {
                    toReturn = Stream.empty();
                }
            }
        } else {
            toReturn = parents.stream();
        }
        return toReturn.filter(o -> !o.equals(node));
    }

    /**
     * @param node node
     * @param parent parent
     */
    public void addParent(O node, R parent) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        this.nodeParentIndex.put(node, parent);
    }

    /**
     * @param node node
     * @param parent parent
     * @return true if parent
     */
    public boolean isParent(O node, R parent) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("The parent cannot be null");
        }
        return this.getParents(node).anyMatch(parent::equals);
    }

    /**
     * @param node node
     * @param parent parent
     * @return true if ancestor
     */
    public boolean isAncestor(O node, R parent) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        if (parent == null) {
            throw new NullPointerException("The parent cannot be null");
        }
        Set<R> ancestors = this.getAncestors(node);
        return ancestors != null && ancestors.contains(parent);
    }

    /**
     * @param node node
     * @return ancestors
     */
    public Set<R> getAncestors(O node) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        Set<R> parents = asSet(this.getParents(node));
        Set<R> ancestors = new HashSet<>(parents);
        for (R r : parents) {
            ancestors.addAll(this.getAncestors((O) r));
        }
        return ancestors;
    }

    /**
     * @param parent parent
     * @param c c
     */
    public void removeChildren(R parent, Collection<? extends O> c) {
        Iterator<? extends O> iterator = c.iterator();
        while (iterator.hasNext()) {
            O o = iterator.next();
            if (this.isParent(o, parent)) {
                iterator.remove();
            }
        }
    }

    /**
     * @param ancestor ancestor
     * @param c c
     */
    public void removeDescendants(R ancestor, Collection<? extends O> c) {
        Iterator<? extends O> iterator = c.iterator();
        while (iterator.hasNext()) {
            O o = iterator.next();
            if (this.isAncestor(o, ancestor)) {
                iterator.remove();
            }
        }
    }

    /**
     * @param node node
     * @param parents parents
     */
    public void addAllParents(O node, Collection<? extends R> parents) {
        if (node == null) {
            throw new NullPointerException("The node cannot be null");
        }
        for (R parent : parents) {
            this.nodeParentIndex.put(node, parent);
        }
    }

    /** @return the defaultRoot */
    public R getDefaultRoot() {
        return this.defaultRoot;
    }

    /**
     * @param set set
     * @param axiomProvider axiomProvider
     * @param dataFactory dataFactory
     * @param <T> type
     * @return lcs
     */
    @SuppressWarnings("unchecked")
    public static <T extends OWLObject> LeastCommonSubsumer<T, ?> build(Collection<? extends T> set,
        final OWLAxiomProvider axiomProvider, final OWLDataFactory dataFactory) {
        if (set == null) {
            throw new NullPointerException("The set cannot be null");
        }
        T t = set.iterator().next();
        return t.accept(new OWLObjectVisitorEx<LeastCommonSubsumer<T, ?>>() {
            @Override
            public LeastCommonSubsumer<T, ?> visit(OWLClass cls) {
                return (LeastCommonSubsumer<T, ?>) new OWLClassLeastCommonSubsumer(axiomProvider,
                    dataFactory);
            }

            @Override
            public LeastCommonSubsumer<T, ?> visit(OWLObjectProperty property) {
                return (LeastCommonSubsumer<T, ?>) new OWLObjectPropertyLeastCommonSubsumer(
                    axiomProvider, dataFactory);
            }

            @Override
            public LeastCommonSubsumer<T, ?> visit(OWLDataProperty property) {
                return (LeastCommonSubsumer<T, ?>) new OWLDataPropertyLeastCommonSubsumer(
                    axiomProvider, dataFactory);
            }

            @Override
            public LeastCommonSubsumer<T, ?> visit(OWLNamedIndividual individual) {
                return (LeastCommonSubsumer<T, ?>) new OWLNamedIndividualLeastCommonSubsumer(
                    axiomProvider, dataFactory);
            }

            @Override
            public LeastCommonSubsumer<T, ?> visit(OWLAnnotationProperty property) {
                return (LeastCommonSubsumer<T, ?>) new OWLAnnotationPropertyLeastCommonSubsumer(
                    axiomProvider, dataFactory);
            }
        });
    }
}
