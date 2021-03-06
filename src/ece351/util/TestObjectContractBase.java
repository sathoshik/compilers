package ece351.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Some basic controls for the object contract tests.
 * Do not edit this file: edit the subclass instead.
 */
public abstract class TestObjectContractBase {

	/**
	 * Test to see if the object contract check methods are working properly.
	 */
	@Test
	public void testControls() {
		assertTrue(checkNotEqualsNull("foo"));
		assertFalse(checkNotEqualsNull(constructAlwaysTrue()));

		assertTrue(checkEqualsIsReflexive("foo"));
		assertFalse(checkEqualsIsReflexive(constructAlwaysFalse()));

		assertTrue(checkEqualsIsSymmetric("foo", "foo"));
		assertTrue(checkEqualsIsSymmetric("foo", "bar"));
		final Object toggler = constructToggler();
		assertFalse(checkEqualsIsSymmetric(toggler, toggler));
		
		assertTrue(checkEqualsIsTransitive("foo", "foo", "foo"));
		assertFalse(checkEqualsIsTransitive("foo", "foo", "bar"));
		assertFalse(checkEqualsIsTransitive("foo", "bar", "foo"));
	}
	
	/*
	 * These methods are to be implemented in the subclass. 
	 */
	abstract Object constructAlwaysFalse();
	abstract Object constructAlwaysTrue();
	abstract Object constructToggler();

	abstract boolean checkNotEqualsNull(final Object obj);
	abstract boolean checkEqualsIsReflexive(final Object obj);
	abstract boolean checkEqualsIsSymmetric(final Object o1, final Object o2);
	abstract boolean checkEqualsIsTransitive(final Object o1, final Object o2, final Object o3);

}
