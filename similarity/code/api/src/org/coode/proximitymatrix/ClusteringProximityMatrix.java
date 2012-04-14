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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.linear.NonSquareMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixChangingVisitor;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.linear.RealVector;
import org.coode.distance.TableDistance;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;

public class ClusteringProximityMatrix<O> implements
		ProximityMatrix<Collection<? extends O>> {
	private final ProximityMatrix<Collection<? extends O>> delegate;
	private final ProximityMeasureFactory proximityMeasureFactory;
	private final History<Collection<? extends O>> history;
	private HistoryItemFactory<Collection<? extends O>> historyItemFactory;

	public static <P> ClusteringProximityMatrix<P> build(
			ProximityMatrix<P> initialMatrix,
			ProximityMeasureFactory proximityMeasureFactory,
			PairFilter<Collection<? extends P>> filter,
			Comparator<? super Pair<Collection<? extends P>>> comparator,
			HistoryItemFactory<Collection<? extends P>> historyItemFactory) {
		return build(initialMatrix, proximityMeasureFactory, filter, comparator,
				new History<Collection<? extends P>>(), historyItemFactory);
	}

	public static <P> ClusteringProximityMatrix<P> build(
			ProximityMatrix<P> initialMatrix,
			ProximityMeasureFactory proximityMeasureFactory,
			PairFilter<Collection<? extends P>> filter,
			Comparator<? super Pair<Collection<? extends P>>> comparator,
			History<Collection<? extends P>> history,
			HistoryItemFactory<Collection<? extends P>> historyItemFactory) {
		if (history == null) {
			throw new NullPointerException("The history cannot be null");
		}
		Set<Collection<? extends P>> newObjects = new LinkedHashSet<Collection<? extends P>>();
		for (P object : initialMatrix.getObjects()) {
			newObjects.add(Collections.singletonList(object));
		}
		ProximityMatrix<Collection<? extends P>> newDelegate = new SimpleProximityMatrix<Collection<? extends P>>(
				newObjects, initialMatrix.getData(), filter, comparator);
		return new ClusteringProximityMatrix<P>(newDelegate, proximityMeasureFactory,
				history, historyItemFactory);
	}

	/**
	 * @param delegate
	 */
	public ClusteringProximityMatrix(ProximityMatrix<Collection<? extends O>> delegate,
			ProximityMeasureFactory proximityMeasureFactory,
			History<Collection<? extends O>> history,
			HistoryItemFactory<Collection<? extends O>> historyItemFactory) {
		if (delegate == null) {
			throw new NullPointerException("The delegate matrix cannot be null");
		}
		if (history == null) {
			throw new NullPointerException("The history cannot be null");
		}
		if (proximityMeasureFactory == null) {
			throw new NullPointerException("The proximity measure factory cannot be null");
		}
		if (historyItemFactory == null) {
			throw new NullPointerException("The history item factory cannot be null");
		}
		this.delegate = delegate;
		this.proximityMeasureFactory = proximityMeasureFactory;
		this.history = history;
		this.historyItemFactory = historyItemFactory;
	}

	public ProximityMeasureFactory getProximityMeasureFactory() {
		return this.proximityMeasureFactory;
	}

	public ClusteringProximityMatrix<O> agglomerate(
			PairFilter<Collection<? extends O>> filter) {
		Set<Collection<? extends O>> objects = this.getObjects();
		Pair<Collection<? extends O>> minimumDistancePair = this.getMinimumDistancePair();
		List<Collection<? extends O>> elementList = new ArrayList<Collection<? extends O>>(
				minimumDistancePair.getElements());
		Collection<? extends O> a = elementList.get(0);
		Collection<? extends O> b = elementList.get(1);
		objects.removeAll(minimumDistancePair.getElements());
		List<Collection<? extends O>> newObjects = new ArrayList<Collection<? extends O>>(
				objects);
		List<O> merger = new ArrayList<O>();
		// int firstMergedIndex = this.getRowIndex(a);
		// int secondMergedIndex = this.getRowIndex(b);
		merger.addAll(minimumDistancePair.getFirst());
		merger.addAll(minimumDistancePair.getSecond());
		newObjects.add(merger);
		double[][] newDistances = new double[newObjects.size()][newObjects.size()];
		int i = 0;
		final int size = newObjects.size();
		for (int index = 0; index < size; index++) {
			Collection<? extends O> aCollection = newObjects.get(index);
			// int rowIndex = aCollection.equals(merger) ? -1
			// : i < firstMergedIndex ? i
			// : i + 1 < secondMergedIndex ? i + 1 : i + 2;
			int rowIndex = aCollection.equals(merger) ? -1 : this
					.getRowIndex(aCollection);
			int j = 0;
			for (Collection<? extends O> anotherCollection : newObjects) {
				// int columnIndex = anotherCollection.equals(merger) ? -1
				// : j < firstMergedIndex ? j
				// : j + 1 < secondMergedIndex ? j + 1 : j + 2;
				int columnIndex = anotherCollection.equals(merger) ? -1 : this
						.getColumnIndex(anotherCollection);
				if (i == j) {
					newDistances[i][j] = 0;
				} else {
					if (rowIndex != -1 && columnIndex != -1) {
						newDistances[i][j] = this.getEntry(rowIndex, columnIndex);
					} else {
						// Apply the formula
						Collection<? extends O> q = rowIndex != -1 ? aCollection
								: anotherCollection;
						double distanceAB = this.getDistance(a, b);
						double distanceAQ = this.getDistance(a, q);
						double distanceBQ = this.getDistance(b, q);
						double newProximity = this.getProximityMeasureFactory()
								.getProximityMeasure(a.size(), b.size(), q.size())
								.distance(distanceAQ, distanceBQ, distanceAB);
						newDistances[i][j] = newProximity;
					}
				}
				j++;
			}
			i++;
		}
		SimpleProximityMatrix<Collection<? extends O>> simpleProximityMatrix = new SimpleProximityMatrix<Collection<? extends O>>(
				newObjects, newDistances, filter, PairFilterBasedComparator.build(filter,
						newObjects, new TableDistance<Collection<? extends O>>(
								newObjects, newDistances)));
		History<Collection<? extends O>> newHistory = this.getHistory();
		HistoryItem<Collection<? extends O>> newItem = this.getHistoryItemFactory()
				.create(minimumDistancePair, newObjects);
		newHistory.add(newItem);
		ClusteringProximityMatrix<O> toReturn = new ClusteringProximityMatrix<O>(
				simpleProximityMatrix, this.getProximityMeasureFactory(), newHistory,
				this.getHistoryItemFactory());
		return toReturn;
	}

	public List<Integer> getColumns(Pair<Collection<? extends O>> pair) {
		return this.delegate.getRows(pair);
	}

	public List<Integer> getRows(Pair<Collection<? extends O>> pair) {
		return this.getColumns(pair);
	}

	public ClusteringProximityMatrix<O> reduce(PairFilter<Collection<? extends O>> filter) {
		ProximityMatrix<Collection<? extends O>> reduced = this.delegate.reduce(filter);
		return new ClusteringProximityMatrix<O>(reduced,
				this.getProximityMeasureFactory(), this.getHistory(),
				this.getHistoryItemFactory());
	}

	/**
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getObjects()
	 */
	public Set<Collection<? extends O>> getObjects() {
		return this.delegate.getObjects();
	}

	/**
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getMinimumDistancePair()
	 */
	public Pair<Collection<? extends O>> getMinimumDistancePair() {
		return this.delegate.getMinimumDistancePair();
	}

	/**
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getMinimumDistance()
	 */
	public double getMinimumDistance() {
		return this.delegate.getMinimumDistance();
	}

	/**
	 * @param o
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getRowIndex(java.lang.Object)
	 */
	public int getRowIndex(Collection<? extends O> o) {
		return this.delegate.getRowIndex(o);
	}

	/**
	 * @param o
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getColumnIndex(java.lang.Object)
	 */
	public int getColumnIndex(Collection<? extends O> o) {
		return this.delegate.getColumnIndex(o);
	}

	/**
	 * @param anObject
	 * @param anotherObject
	 * @return
	 * @see org.coode.proximitymatrix.ProximityMatrix#getDistance(java.lang.Object,
	 *      java.lang.Object)
	 */
	public double getDistance(Collection<? extends O> anObject,
			Collection<? extends O> anotherObject) {
		return this.delegate.getDistance(anObject, anotherObject);
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.AnyMatrix#isSquare()
	 */
	public boolean isSquare() {
		return this.delegate.isSquare();
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.AnyMatrix#getRowDimension()
	 */
	public int getRowDimension() {
		return this.delegate.getRowDimension();
	}

	/**
	 * @param rowDimension
	 * @param columnDimension
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#createMatrix(int, int)
	 */
	public RealMatrix createMatrix(int rowDimension, int columnDimension) {
		return this.delegate.createMatrix(rowDimension, columnDimension);
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.AnyMatrix#getColumnDimension()
	 */
	public int getColumnDimension() {
		return this.delegate.getColumnDimension();
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#copy()
	 */
	public ClusteringProximityMatrix<O> copy() {
		return new ClusteringProximityMatrix<O>(this.delegate.copy(),
				this.getProximityMeasureFactory(), this.getHistory(),
				this.getHistoryItemFactory());
	}

	/**
	 * @param m
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#add(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix add(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.add(m);
	}

	/**
	 * @param m
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#subtract(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix subtract(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.subtract(m);
	}

	/**
	 * @param d
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#scalarAdd(double)
	 */
	public RealMatrix scalarAdd(double d) {
		return this.delegate.scalarAdd(d);
	}

	/**
	 * @param d
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#scalarMultiply(double)
	 */
	public RealMatrix scalarMultiply(double d) {
		return this.delegate.scalarMultiply(d);
	}

	/**
	 * @param m
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#multiply(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix multiply(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.multiply(m);
	}

	/**
	 * @param m
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(org.apache.commons.math.linear.RealMatrix)
	 */
	public RealMatrix preMultiply(RealMatrix m) throws IllegalArgumentException {
		return this.delegate.preMultiply(m);
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#getData()
	 */
	public double[][] getData() {
		return this.delegate.getData();
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#getNorm()
	 */
	public double getNorm() {
		return this.delegate.getNorm();
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#getFrobeniusNorm()
	 */
	public double getFrobeniusNorm() {
		return this.delegate.getFrobeniusNorm();
	}

	/**
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getSubMatrix(int, int,
	 *      int, int)
	 */
	public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn,
			int endColumn) throws MatrixIndexException {
		return this.delegate.getSubMatrix(startRow, endRow, startColumn, endColumn);
	}

	/**
	 * @param selectedRows
	 * @param selectedColumns
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getSubMatrix(int[], int[])
	 */
	public RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
			throws MatrixIndexException {
		return this.delegate.getSubMatrix(selectedRows, selectedColumns);
	}

	/**
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @param destination
	 * @throws MatrixIndexException
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#copySubMatrix(int, int,
	 *      int, int, double[][])
	 */
	public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn,
			double[][] destination) throws MatrixIndexException, IllegalArgumentException {
		this.delegate
				.copySubMatrix(startRow, endRow, startColumn, endColumn, destination);
	}

	/**
	 * @param selectedRows
	 * @param selectedColumns
	 * @param destination
	 * @throws MatrixIndexException
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#copySubMatrix(int[],
	 *      int[], double[][])
	 */
	public void copySubMatrix(int[] selectedRows, int[] selectedColumns,
			double[][] destination) throws MatrixIndexException, IllegalArgumentException {
		this.delegate.copySubMatrix(selectedRows, selectedColumns, destination);
	}

	/**
	 * @param subMatrix
	 * @param row
	 * @param column
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#setSubMatrix(double[][],
	 *      int, int)
	 */
	public void setSubMatrix(double[][] subMatrix, int row, int column)
			throws MatrixIndexException {
		this.delegate.setSubMatrix(subMatrix, row, column);
	}

	/**
	 * @param row
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getRowMatrix(int)
	 */
	public RealMatrix getRowMatrix(int row) throws MatrixIndexException {
		return this.delegate.getRowMatrix(row);
	}

	/**
	 * @param row
	 * @param matrix
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setRowMatrix(int,
	 *      org.apache.commons.math.linear.RealMatrix)
	 */
	public void setRowMatrix(int row, RealMatrix matrix) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRowMatrix(row, matrix);
	}

	/**
	 * @param column
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getColumnMatrix(int)
	 */
	public RealMatrix getColumnMatrix(int column) throws MatrixIndexException {
		return this.delegate.getColumnMatrix(column);
	}

	/**
	 * @param column
	 * @param matrix
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setColumnMatrix(int,
	 *      org.apache.commons.math.linear.RealMatrix)
	 */
	public void setColumnMatrix(int column, RealMatrix matrix)
			throws MatrixIndexException, InvalidMatrixException {
		this.delegate.setColumnMatrix(column, matrix);
	}

	/**
	 * @param row
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getRowVector(int)
	 */
	public RealVector getRowVector(int row) throws MatrixIndexException {
		return this.delegate.getRowVector(row);
	}

	/**
	 * @param row
	 * @param vector
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setRowVector(int,
	 *      org.apache.commons.math.linear.RealVector)
	 */
	public void setRowVector(int row, RealVector vector) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRowVector(row, vector);
	}

	/**
	 * @param column
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getColumnVector(int)
	 */
	public RealVector getColumnVector(int column) throws MatrixIndexException {
		return this.delegate.getColumnVector(column);
	}

	/**
	 * @param column
	 * @param vector
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setColumnVector(int,
	 *      org.apache.commons.math.linear.RealVector)
	 */
	public void setColumnVector(int column, RealVector vector)
			throws MatrixIndexException, InvalidMatrixException {
		this.delegate.setColumnVector(column, vector);
	}

	/**
	 * @param row
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getRow(int)
	 */
	public double[] getRow(int row) throws MatrixIndexException {
		return this.delegate.getRow(row);
	}

	/**
	 * @param row
	 * @param array
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setRow(int, double[])
	 */
	public void setRow(int row, double[] array) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setRow(row, array);
	}

	/**
	 * @param column
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getColumn(int)
	 */
	public double[] getColumn(int column) throws MatrixIndexException {
		return this.delegate.getColumn(column);
	}

	/**
	 * @param column
	 * @param array
	 * @throws MatrixIndexException
	 * @throws InvalidMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#setColumn(int, double[])
	 */
	public void setColumn(int column, double[] array) throws MatrixIndexException,
			InvalidMatrixException {
		this.delegate.setColumn(column, array);
	}

	/**
	 * @param row
	 * @param column
	 * @return
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#getEntry(int, int)
	 */
	public double getEntry(int row, int column) throws MatrixIndexException {
		return this.delegate.getEntry(row, column);
	}

	/**
	 * @param row
	 * @param column
	 * @param value
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#setEntry(int, int, double)
	 */
	public void setEntry(int row, int column, double value) throws MatrixIndexException {
		this.delegate.setEntry(row, column, value);
	}

	/**
	 * @param row
	 * @param column
	 * @param increment
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#addToEntry(int, int,
	 *      double)
	 */
	public void addToEntry(int row, int column, double increment)
			throws MatrixIndexException {
		this.delegate.addToEntry(row, column, increment);
	}

	/**
	 * @param row
	 * @param column
	 * @param factor
	 * @throws MatrixIndexException
	 * @see org.apache.commons.math.linear.RealMatrix#multiplyEntry(int, int,
	 *      double)
	 */
	public void multiplyEntry(int row, int column, double factor)
			throws MatrixIndexException {
		this.delegate.multiplyEntry(row, column, factor);
	}

	/**
	 * @return
	 * @see org.apache.commons.math.linear.RealMatrix#transpose()
	 */
	public RealMatrix transpose() {
		return this.delegate.transpose();
	}

	/**
	 * @return
	 * @throws InvalidMatrixException
	 * @deprecated
	 * @see org.apache.commons.math.linear.RealMatrix#inverse()
	 */
	@Deprecated
	public RealMatrix inverse() throws InvalidMatrixException {
		return this.delegate.inverse();
	}

	/**
	 * @return
	 * @deprecated
	 * @see org.apache.commons.math.linear.RealMatrix#getDeterminant()
	 */
	@Deprecated
	public double getDeterminant() {
		return this.delegate.getDeterminant();
	}

	/**
	 * @return
	 * @deprecated
	 * @see org.apache.commons.math.linear.RealMatrix#isSingular()
	 */
	@Deprecated
	public boolean isSingular() {
		return this.delegate.isSingular();
	}

	/**
	 * @return
	 * @throws NonSquareMatrixException
	 * @see org.apache.commons.math.linear.RealMatrix#getTrace()
	 */
	public double getTrace() throws NonSquareMatrixException {
		return this.delegate.getTrace();
	}

	/**
	 * @param v
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#operate(double[])
	 */
	public double[] operate(double[] v) throws IllegalArgumentException {
		return this.delegate.operate(v);
	}

	/**
	 * @param v
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#operate(org.apache.commons.math.linear.RealVector)
	 */
	public RealVector operate(RealVector v) throws IllegalArgumentException {
		return this.delegate.operate(v);
	}

	/**
	 * @param v
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(double[])
	 */
	public double[] preMultiply(double[] v) throws IllegalArgumentException {
		return this.delegate.preMultiply(v);
	}

	/**
	 * @param v
	 * @return
	 * @throws IllegalArgumentException
	 * @see org.apache.commons.math.linear.RealMatrix#preMultiply(org.apache.commons.math.linear.RealVector)
	 */
	public RealVector preMultiply(RealVector v) throws IllegalArgumentException {
		return this.delegate.preMultiply(v);
	}

	/**
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInRowOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor);
	}

	/**
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInRowOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInRowOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInRowOrder(visitor);
	}

	/**
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInColumnOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor);
	}

	/**
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInColumnOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInColumnOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInColumnOrder(visitor);
	}

	/**
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor)
	 */
	public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor);
	}

	/**
	 * @param visitor
	 * @return
	 * @throws MatrixVisitorException
	 * @see org.apache.commons.math.linear.RealMatrix#walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor)
	 */
	public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor)
			throws MatrixVisitorException {
		return this.delegate.walkInOptimizedOrder(visitor);
	}

	/**
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param visitor
	 * @param startRow
	 * @param endRow
	 * @param startColumn
	 * @param endColumn
	 * @return
	 * @throws MatrixIndexException
	 * @throws MatrixVisitorException
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
	 * @param b
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InvalidMatrixException
	 * @deprecated
	 * @see org.apache.commons.math.linear.RealMatrix#solve(double[])
	 */
	@Deprecated
	public double[] solve(double[] b) throws IllegalArgumentException,
			InvalidMatrixException {
		return this.delegate.solve(b);
	}

	/**
	 * @param b
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InvalidMatrixException
	 * @deprecated
	 * @see org.apache.commons.math.linear.RealMatrix#solve(org.apache.commons.math.linear.RealMatrix)
	 */
	@Deprecated
	public RealMatrix solve(RealMatrix b) throws IllegalArgumentException,
			InvalidMatrixException {
		return this.delegate.solve(b);
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

	/**
	 * @return the history
	 */
	public History<Collection<? extends O>> getHistory() {
		try {
			return this.history.clone();
		} catch (CloneNotSupportedException e) {
			return this.history;
		}
	}

	/**
	 * @return the historyItemFactory
	 */
	public HistoryItemFactory<Collection<? extends O>> getHistoryItemFactory() {
		return this.historyItemFactory;
	}

	/**
	 * @param historyItemFactory
	 *            the historyItemFactory to set
	 */
	public void setHistoryItemFactory(
			HistoryItemFactory<Collection<? extends O>> historyItemFactory) {
		this.historyItemFactory = historyItemFactory;
	}
}
