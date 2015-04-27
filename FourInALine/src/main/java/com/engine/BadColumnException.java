package com.engine;

public class BadColumnException extends Exception {

	/**
	 * Throw this exception when a column is full of column number is not in [0..6]
	 */
	private static final long serialVersionUID = 1L;

	public BadColumnException(){}
}
