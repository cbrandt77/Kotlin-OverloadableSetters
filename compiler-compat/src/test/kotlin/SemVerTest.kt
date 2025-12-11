package dev.zacsweers.metro.compiler.compat

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.*

class SemVerTest {
	fun SemVer.assertGreaterThan(other: SemVer) {
		assertThat("compareTo() > 0", this.compareTo(other), greaterThan(0))
		assertThat("compareVersionNoSuffix() >= 0", this.compareVersionNoSuffix(other), greaterThanOrEqualTo(0))
		assertThat("compareSuffixes() >= 0", this.compareSuffixes(other), notNullAnd(greaterThanOrEqualTo(0)))
	}
	
	fun SemVer.assertLessThan(other: SemVer) {
		assertThat("compareTo() < 0", this.compareTo(other), lessThan(0))
		assertThat("compareVersionNoSuffix() <= 0", this.compareVersionNoSuffix(other), lessThanOrEqualTo(0))
		assertThat("compareSuffixes() <= 0", this.compareSuffixes(other), notNullAnd(lessThanOrEqualTo(0)))
	}
	
	@Test
	fun `constructor - parses version information`() {
		val testver = SemVer("2.2.20-ij252-24")
		assertContentEquals(intArrayOf(2,2,20), testver.version)
		assertContentEquals(arrayOf(SemVer.Suffix("ij252"), SemVer.Suffix("24")), testver.suffix)
	}
	
	@Test
	fun `compares number versions`() {
		val testVer = SemVer("2.2.20")
		
		val patchHigher = SemVer("2.2.20.1")
		
		val incHigherVer = SemVer("2.2.21")
		val incLowerVer = SemVer("2.2.19")
		
		val higherMinorVer = SemVer("2.3.19")
		val lowerMinorVer = SemVer("2.1.21")
		
		val higherMajorVer = SemVer("3.0.0")
		val lowerMajorVer = SemVer("1.0.0")
		
		
		assertAll(
				{
					patchHigher.assertGreaterThan(testVer)
				},
				{
					incHigherVer.assertGreaterThan(testVer)
					incLowerVer.assertLessThan(testVer)
				},
				{
					higherMinorVer.assertGreaterThan(testVer)
					lowerMinorVer.assertLessThan(testVer)
				},
				{
					higherMajorVer.assertGreaterThan(testVer)
					lowerMajorVer.assertLessThan(testVer)
				}
		)
	}
	
	@Test
	fun `compares suffixes against release`() {
		val testVer = SemVer("2.2.20")
		
		val downstream1 = SemVer("2.2.20-ij252-24")
		val downstream2 = SemVer("2.2.20-dev-4")
		val upstream1 = SemVer("2.2.20-beta1")
		
		assertAll(
				{
					assertThat(downstream1.compareVersionNoSuffix(testVer), equalTo(0))
					assertThat(downstream1.compareTo(testVer), greaterThan(0))
				},
				{
					downstream2.assertGreaterThan(testVer)
				},
				{
					upstream1.assertLessThan(testVer)
				}
		)
	}
	
	@Test
	fun `compares suffixes against each other`() {
		val testver = SemVer("2.2.20-ij252-24")
		
		val later = SemVer("2.2.20-ij252-25")
		val later2 = SemVer("2.2.20-ij253-24")
		
		later.assertGreaterThan(testver)
		later2.assertGreaterThan(testver)
		
		val incompatible = SemVer("2.2.20-beta1")
		
		assertThat(incompatible.compareVersionNoSuffix(testver), equalTo(0))
		assertThat(incompatible.compareSuffixes(testver), nullValue())
	}
}

class SuffixTest {
	@Test
	fun `parsing - word-number`() {
		val testver = SemVer.Suffix("ij252")
		assertEquals("ij", testver.prefix)
		assertEquals(252, testver.number)
	}
	@Test
	fun `parsing - number only`() {
		val testver = SemVer.Suffix("5437")
		assertEquals(null, testver.prefix)
		assertEquals(5437, testver.number)
	}
	@Test
	fun `parsing - word only`() {
		val testver = SemVer.Suffix("dev")
		assertEquals("dev", testver.prefix)
		assertEquals(0, testver.number)
	}
	
	fun SemVer.Suffix.assertGreaterThan(other: SemVer.Suffix) {
		assertThat(this.compareTo(other), notNullAnd(greaterThan(0)))
	}
	
	fun SemVer.Suffix.assertLessThan(other: SemVer.Suffix) {
		assertThat(this.compareTo(other), notNullAnd(lessThan(0)))
	}
	
	fun SemVer.Suffix.assertIncompatibleWith(other: SemVer.Suffix) {
		assertThat(this.compareTo(other), nullValue())
	}
	
	@Test
	fun `comparison - same prefix`() {
		val testver = SemVer.Suffix("ij252")
		
		val later = SemVer.Suffix("ij253")
		val earlier = SemVer.Suffix("ij251")
		
		later.assertGreaterThan(testver)
		earlier.assertLessThan(testver)
	}
	
	@Test
	fun `comparison - different prefix`() {
		val testver = SemVer.Suffix("ij252")
		
		val beta = SemVer.Suffix("beta1")
		val dev = SemVer.Suffix("dev")
		val numOnly = SemVer.Suffix("44")
		
		beta.assertIncompatibleWith(testver)
		dev.assertIncompatibleWith(testver)
		numOnly.assertIncompatibleWith(testver)
	}
	
	@Test
	fun `comparison - against blank`() {
		val testver = SemVer.Suffix("")
		
		val beta = SemVer.Suffix("beta1")
		val dev = SemVer.Suffix("dev")
		val ij = SemVer.Suffix("ij242")
		
		beta.assertLessThan(testver)
		dev.assertGreaterThan(testver)
		ij.assertGreaterThan(testver)
	}
}