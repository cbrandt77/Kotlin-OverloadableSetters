package dev.zacsweers.metro.compiler.compat

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class NonNullAssert<T : Any>(val matcher: Matcher<T>) : BaseMatcher<T?>() {
	override fun matches(p0: Any?): Boolean {
		return p0 != null && matcher.matches(p0)
	}
	
	override fun describeMismatch(item: Any?, description: Description) {
		if (isNotNull(item, description)) {
			if (!matcher.matches(item)) {
				matcher.describeMismatch(item, description)
			}
		}
	}
	
	override fun describeTo(p0: Description) {
		p0.appendDescriptionOf(matcher)
	}
}

fun <T : Any> notNullAnd(matcher: Matcher<T>) : Matcher<T?> {
	return NonNullAssert(matcher)
}