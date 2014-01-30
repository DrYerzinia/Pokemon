package com.dryerzinia.pokemon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MultiIterator<E> implements Iterator<E> {

	int currentIterator;
	ArrayList<Iterator<E>> iterators;

	/**
	 * Create a new MultiCollectionIterator with a specified capacity
	 * @param capacity
	 */
	public MultiIterator(int capacity){

		currentIterator = 0;
		iterators = new ArrayList<Iterator<E>>(capacity);

	}

	/**
	 * Add a collection to the collection list to be iterated
	 * @param collection Collection to add
	 */
	public void addIterator(Iterator<E> iterator){
		iterators.add(iterator);
	}

	@Override
	public boolean hasNext() {

		/*
		 * Current iterator has next so we have next
		 */
		if(iterators.get(currentIterator).hasNext()) return true;

		/*
		 * We try each successive iterator until we find a next or run out
		 */
		while(currentIterator < iterators.size() - 1){
			currentIterator++;
			if(iterators.get(currentIterator).hasNext()) return true;
		}

		return false;

	}

	@Override
	public E next() {

		if(hasNext())
			return iterators.get(currentIterator).next();

		throw new NoSuchElementException("We have nothing left to iterate!");

	}

	@Override
	public void remove() {

		iterators.get(currentIterator).remove();

	}

}
