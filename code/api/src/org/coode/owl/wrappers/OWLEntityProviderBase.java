package org.coode.owl.wrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public abstract class OWLEntityProviderBase implements OWLEntityProvider {
    private final Set<OWLEntity> delegate = new HashSet<OWLEntity>();
    private final OWLOntologyManager ontologyManager;

    /** @param ontologyManager
     *            ontologyManager */
    public OWLEntityProviderBase(final OWLOntologyManager ontologyManager) {
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
    }

    @Override
    public boolean add(final OWLEntity e) {
        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends OWLEntity> c) {
        return false;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<OWLEntity> iterator() {
        return delegate.iterator();
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    /** @return the ontologyManager */
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }
}
