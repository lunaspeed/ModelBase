
package com.lunary.util.factory;

/**
 * A simple Factory interface for creating object.
 * 
 * @author Steven
 * 
 * @param <E>
 */
public interface Factory<E> {

    /**
     * 
     * Create <E> based on input objects
     * 
     * @param objects
     * @return <E>
     */
    public E create(Object... objects);
}
