package org.coode.owl.wrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public abstract class OWLEntityProviderBase implements OWLEntityProvider {
    private final Set<OWLEntity> delegate = new HashSet<OWLEntity>();
    private final OWLOntologyManager ontologyManager;

    public OWLEntityProviderBase(final OWLOntologyManager ontologyManager) {
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
    }

    /** @param e
     * @return
     * @see java.util.Set#add(java.lang.Object) */
    public boolean add(final OWLEntity e) {
        return false;
        // return delegate.add(e);
    }

    /** @param c
     * @return
     * @see java.util.Set#addAll(java.util.Collection) */
    public boolean addAll(final Collection<? extends OWLEntity> c) {
        return false;
        // return delegate.addAll(c);
    }

    /** @see java.util.Set#clear() */
    public void clear() {
        delegate.clear();
    }

    /** @param o
     * @return
     * @see java.util.Set#contains(java.lang.Object) */
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    /** @param c
     * @return
     * @see java.util.Set#containsAll(java.util.Collection) */
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    /** @param o
     * @return
     * @see java.util.Set#equals(java.lang.Object) */
    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    /** @return
     * @see java.util.Set#hashCode() */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /** @return
     * @see java.util.Set#isEmpty() */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /** @return
     * @see java.util.Set#iterator() */
    public Iterator<OWLEntity> iterator() {
        return delegate.iterator();
    }

    /** @param o
     * @return
     * @see java.util.Set#remove(java.lang.Object) */
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    /** @param c
     * @return
     * @see java.util.Set#removeAll(java.util.Collection) */
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    /** @param c
     * @return
     * @see java.util.Set#retainAll(java.util.Collection) */
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    /** @return
     * @see java.util.Set#size() */
    public int size() {
        return delegate.size();
    }

    /** @return
     * @see java.util.Set#toArray() */
    public Object[] toArray() {
        return delegate.toArray();
    }

    /** @param <T>
     * @param a
     * @return
     * @see java.util.Set#toArray(T[]) */
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    /** @return the ontologyManager */
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }
}
