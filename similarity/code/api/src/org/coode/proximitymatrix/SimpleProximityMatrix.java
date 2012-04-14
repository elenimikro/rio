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
package org.coode.proximitymatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.linear.NonSquareMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixChangingVisitor;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.linear.RealVector;
import org.coode.distance.Distance;
import org.coode.pair.Pair;
import org.coode.pair.SimplePair;
import org.coode.pair.filter.PairFilter;

public final class SimpleProximityMatrix<O> implements ProximityMatrix<O> {
	private final RealMatrix delegate;
	private final List<O> objects = new ArrayList<O>();
	private final Comparator<? super Pair<O>> comparator;
	private double minimumDistance = Double.MAX_VALUE;
	private Pair<O> minimumDistancePair = null;
	private final PairFilter<O> filter;
	private final Map<O, Integer> objectIndex;

	public SimpleProximityMatrix(Collection<? extends O> objects, double[][] distances,
			PairFilter<O> filter, Comparator<? super Pair<O>> comparator) {
		if (objects == null) {
			throw new NullPointerException("The object colleciton cannot be null");
		}
		if (objects.isEmpty()) {
			throw new IllegalArgumentException("The object collection cannot be empty");
		}
		if (distances == null) {
			throw new NullPointerException("The distances cannot be null");
		}
		if (distances.length != objects.size()) {
			throw new IllegalArgumentException(String.format(
					"The object collection size %d != distances dimension %d",
					objects.size(), distances.length));
		}
		if (filter == null) {
			throw new NullPointerException("The filter cannot be null");
		}
		if (comparator == null) {
			throw new NullPointerException("The comparator cannot be null");
		}
		this.comparator = comparator;
		this.objects.addAll(objects);
		this.filter = filter;
		int i = 0;
		this.objectIndex = new HashMap<O, Integer>();
		for (O object : this.objects) {
			int j = 0;
			this.objectIndex.put(object, new Integer(i));
			for (O anotherObject : this.objects) {
				double distanceValue = distances[i][j];
				SimplePair<O> pair = new SimplePair<O>(object, anotherObject);
				if (anotherObject != object
						&& filter.accept(object, anotherObject)
						&& (distanceValue < this.minimumDistance || distanceValue == this.minimumDistance
								&& this.getMinimumDistancePair() != null
								&& this.getComparator().compare(pair,
										this.minimumDistancePair) < 0)) {
					this.minimumDistance = distanceValue;
					this.minimumDistancePair = pair;
				}
				j++;
			}
			i++;
		}
		this.delegate = new Array2DRowRealMatrix(distances);
	}

	public SimpleProximityMatrix(Collection<? extends O> objects, Distance<O> distance) {
		this(objects, distance, new PairFilter<O>() {
			public boolean accept(O first, O second) {
				return true;
			}
		}, new Comparator<Pair<O>>() {
			public int compare(Pair<O> arg0, Pair<O> arg1) {
				return arg0.hashCode() - arg1.hashCode();
			}
		});
	}

	public SimpleProximityMatrix(Collection<? extends O> objects, Distance<O> distance,
			PairFilter<O> filter, Comparator<? super Pair<O>> comparator) {
		if (objects == null) {
			throw new NullPointerException("The object colleciton cannot be null");
		}
		if (objects.isEmpty()) {
			throw new IllegalArgumentException("The object collection cannot be empty");
		}
		if (distance == null) {
			throw new NullPointerException("The distance cannot be null");
		}
		if (filter == null) {
			throw new NullPointerException("The filter cannot be null");
		}
		if (comparator == null) {
			throw new NullPointerException("The comparator cannot be null");
		}
		this.comparator = comparator;
		this.objects.addAll(objects);
		this.filter = filter;
		double[][] distances = new double[objects.size()][objects.size()];
		int i = 0;
		this.objectIndex = new HashMap<O, Integer>();
		for (O object : objects) {
			int j = 0;
			this.objectIndex.put(object, i);
			for (O anotherObject : objects) {
				double distanceValue = distance.getDistance(object, anotherObject);
				distances[i][j] = distanceValue;
				SimplePair<O> pair = new SimplePair<O>(object, anotherObject);
				if (!anotherObject.equals(object)
						&& filter.accept(object, anotherObject)
						&& (distanceValue < this.minimumDistance || distanceValue == this.minimumDistance
								&& this.getMinimumDistancePair() != null
								&& this.getComparator().compare(pair,
										this.minimumDistancePair) < 0)) {
					this.minimumDistance = distanceValue;
					this.minimumDistancePair = pair;
				}
				j++;
			}
			i++;
		}
		this.delegate = new Array2DRowRealMatrix(distances);
	}

	public ProximityMatrix<O> reduce(PairFilter<O> filter) {
		Set<O> reducedObjects = new HashSet<O>();
		Iterator<O> iterator = this.getObjects().iterator();
		boolean found = false;
		while (iterator.hasNext()) {
			Iterator<O> anotherIterator = this.getObjects().iterator();
			O object = iterator.next();
			while (!found && anotherIterator.hasNext()) {
				O anotherObject = anotherIterator.next();
				found = anotherObject != object && filter.accept(object, anotherObject);
			}
			if (found) {
				reducedObjects.add(object);
			}
			found = false;
		}
		double[][] newDistances = new double[reducedObjects.size()][reducedObjects.size()];
		int i = 0;
		for (O a : reducedObjects) {
			int j = 0;
			for (O b : reducedObjects) {
				newDistances[i][j] = a == b ? 0 : this.getDistance(a, b);
				j++;
			}
			i++;
		}
		return new SimpleProximityMatrix<O>(reducedObjects, newDistances,
				this.getFilter(), this.getComparator());
	}

	public final int getRowIndex(O o) {
		Integer index = this.objectIndex.get(o);
		return index == null ? -1 : index;
	}

	public final int getColumnIndex(O o) {
		Integer index = this.objectIndex.get(o);
		return index == null ? -1 : index;
	}

	public Pair<O> getMinimumDistancePair() {
		return this.minimumDistancePair == null ? null : new SimplePair<O>(
				this.minimumDistancePair);
	}

	public double getDistance(O anObject, O anotherObject) {
		int row = this.getRowIndex(anObject);
		if (row == -1) {
			throw new IllegalArgumentException(String.format(
					"The object %s is not contained in this matrix ", anObject));
		}
		int column = this.getColumnIndex(anotherObject);
		if (column == -1) {
			throw new IllegalArgumentException(String.format(
					"The object %s is not contained in this matrix ", anotherObject));
		}
		return this.getEntry(row, column);
	}

	public double getMinimumDistance() {
		return this.minimumDistance;
	}

	public List<Integer> getColumns(Pair<O> pair) {
		Set<O> elements = pair.getElements();
		List<Integer> cols = new ArrayList<Integer>(elements.size());
		for (O o : elements) {
			int columnIndex = this.getColumnIndex(o);
			cols.add(columnIndex);
		}
		return cols;
	}

	public List<Integer> getRows(Pair<O> pair) {
		Set<O> elements = pair.getElements();
		List<Integer> rows = new ArrayList<Integer>(elements.size());
		for (O o : elements) {
			int rowIndex = this.getRowIndex(o);
			rows.add(rowIndex);
		}
		return rows;
	}

	// Start delegation
	/**
	 * @see org.apache.commons.math.linear.RealMatrix#createMatrix(int, int)
	 */
	public RealMatrix createMatrix(int rowDimension, int columnDimension) {
		return this.delegate.createMatrix(rowDimension, columnDimension);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#copy()
	 */
	public ProximityMatrix<O> copy() {
		return new SimpleProximityMatrix<O>(this.getObjects(), this.getData(),
				this.getFilter(), this.getComparator());
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#add(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.add(m);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#subtract(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.subtract(m);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#scalarAdd(double)
	 */
	public RealMatrix scalarAdd(double d) {
		return this.delegate.scalarAdd(d);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#scalarMultiply(double)
	 */
	public RealMatrix scalarMultiply(double d) {
		return this.delegate.scalarMultiply(d);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#multiply(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.multiply(m);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.preMultiply(m);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getData()
	 */
	public double[][] getData() {
		return this.delegate.getData();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getNorm()
	 */
	public double getNorm() {
		return this.delegate.getNorm();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getFrobeniusNorm()
	 */
	public double getFrobeniusNorm() {
		return this.delegate.getFrobeniusNorm();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getSubMatrix(int, int,
	 *      int, int)
	 */
	public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn,
			int endColumn) throws MatrixIndexException {
		return this.delegate.getSubMatrix(startRow, endRow, startColumn, endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getSubMatrix(int[], int[])
	 */
	public RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
			throws MatrixIndexException {
		return this.delegate.getSubMatrix(selectedRows, selectedColumns);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#copySubMatrix(int, int,
	 *      int, int, double[][])
	 */
	public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn,
			double[][] destination) throws MatrixIndexException, IllegalArgumentException {
		this.delegate
				.copySubMatrix(startRow, endRow, startColumn, endColumn, destination);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#copySubMatrix(int[],
	 *      int[], double[][])
	 */
	public void copySubMatrix(int[] selectedRows, int[] selectedColumns,
			double[][] destination) throws MatrixIndexException, IllegalArgumentException {
		this.delegate.copySubMatrix(selectedRows, selectedColumns, destination);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setSubMatrix(double[][],
	 *      int, int)
	 */
	public void setSubMatrix(double[][] subMatrix, int row, int column)
			throws MatrixIndexException {
		this.delegate.setSubMatrix(subMatrix, row, column);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getRowMatrix(int)
	 */
	public RealMatrix getRowMatrix(int row) throws MatrixIndexException {
		return this.delegate.getRowMatrix(row);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setRowMatrix(int,
	 *      org.apache.commons.math.linear.RealMatrix)
	 */
	public void setRowMatrix(int row, RealMatrix matrix) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRowMatrix(row, matrix);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getColumnMatrix(int)
	 */
	public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
		return this.delegate.getColumnMatrix(column);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setColumnMatrix(int,
	 *      org.apache.commons.math.linear.RealMatrix)
	 */
	public void setColumnMatrix(int column, RealMatrix matrix)
			throws MatrixIndexException, InvalidMatrixException {
		this.delegate.setColumnMatrix(column, matrix);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getRowVector(int)
	 */
	public RealVector getRowVector(int row) throws MatrixIndexException {
		return this.delegate.getRowVector(row);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setRowVector(int,
	 *      org.apache.commons.math.linear.RealVector)
	 */
	public void setRowVector(int row, RealVector vector) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRowVector(row, vector);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getColumnVector(int)
	 */
	public RealVector getColumnVector(int column) throws MatrixIndexException {
		return this.delegate.getColumnVector(column);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setColumnVector(int,
	 *      org.apache.commons.math.linear.RealVector)
	 */
	public void setColumnVector(int column, RealVector vector)
			throws MatrixIndexException, InvalidMatrixException {
		this.delegate.setColumnVector(column, vector);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getRow(int)
	 */
	public double[] getRow(int row) throws MatrixIndexException {
		return this.delegate.getRow(row);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setRow(int, double[])
	 */
	public void setRow(int row, double[] array) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRow(row, array);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getColumn(int)
	 */
	public double[] getColumn(int column) throws MatrixIndexException {
		return this.delegate.getColumn(column);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setColumn(int, double[])
	 */
	public void setColumn(int column, double[] array) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setColumn(column, array);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getEntry(int, int)
	 */
	public double getEntry(int row, int column) throws MatrixIndexException {
		return this.delegate.getEntry(row, column);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#setEntry(int, int, double)
	 */
	public void setEntry(int row, int column, double value) throws MatrixIndexException {
		this.delegate.setEntry(row, column, value);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#addToEntry(int, int,
	 *      double)
	 */
	public void addToEntry(int row, int column, double increment)
			throws MatrixIndexException {
		this.delegate.addToEntry(row, column, increment);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#multiplyEntry(int, int,
	 *      double)
	 */
	public void multiplyEntry(int row, int column, double factor)
			throws MatrixIndexException {
		this.delegate.multiplyEntry(row, column, factor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#transpose()
	 */
	public RealMatrix transpose() {
		return this.delegate.transpose();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#inverse()
	 */
	@Deprecated
	public RealMatrix inverse() throws InvalidMatrixException {
		return this.delegate.inverse();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getDeterminant()
	 */
	@Deprecated
	public double getDeterminant() {
		return this.delegate.getDeterminant();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#isSingular()
	 */
	@Deprecated
	public boolean isSingular() {
		return this.delegate.isSingular();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#getTrace()
	 */
	public double getTrace() throws NonSquareMatrixException {
		return this.delegate.getTrace();
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#operate(double[])
	 */
	public double[] operate(double[] v) throws IllegalArgumentException {
		return this.delegate.operate(v);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#operate(org.apache.commons.math.linear.RealVector)
	 */
	public RealVector operate(RealVector v) throws IllegalArgumentException {
		return this.delegate.operate(v);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(double[])
	 */
	public double[] preMultiply(double[] v) throws IllegalArgumentException {
		return this.delegate.preMultiply(v);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(org.apache.commons.math.linear.RealVector)
	 */
	public RealVector preMultiply(RealVector v) throws IllegalArgumentException {
		return this.delegate.preMultiply(v);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInRowOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInRowOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInRowOrder(RealMatrixChangingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInRowOrder(RealMatrixPreservingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInColumnOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInColumnOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInColumnOrder(RealMatrixChangingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInColumnOrder(RealMatrixPreservingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor,
	 *      int, int, int, int)
	 */
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor, int startRow,
			int endRow, int startColumn, int endColumn) throws MatrixIndexException,
			MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor, startRow, endRow, startColumn,
				endColumn);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#solve(double[])
	 */
	@Deprecated
	public double[] solve(double[] b) throws IllegalArgumentException,
			InvalidMatrixException {
		return this.delegate.solve(b);
	}

	/**
	 * @see org.apache.commons.math.linear.RealMatrix#solve(org.apache.commons.math.linear.RealMatrix)
	 */
	@Deprecated
	public RealMatrix solve(RealMatrix b) throws IllegalArgumentException,
			InvalidMatrixException {
		return this.delegate.solve(b);
	}

	/**
	 * @see org.apache.commons.math.linear.AnyMatrix#isSquare()
	 */
	public boolean isSquare() {
		return this.delegate.isSquare();
	}

	/**
	 * @see org.apache.commons.math.linear.AnyMatrix#getRowDimension()
	 */
	public int getRowDimension() {
		return this.delegate.getRowDimension();
	}

	/**
	 * @see org.apache.commons.math.linear.AnyMatrix#getColumnDimension()
	 */
	public int getColumnDimension() {
		return this.delegate.getColumnDimension();
	}

	// END delegation
	/**
	 * @return the objects
	 */
	public Set<O> getObjects() {
		return new LinkedHashSet<O>(this.objects);
	}

	/**
	 * @return the filter
	 */
	public PairFilter<O> getFilter() {
		return this.filter;
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

	/**
	 * @return the comparator
	 */
	public Comparator<? super Pair<O>> getComparator() {
		return this.comparator;
	}
}
