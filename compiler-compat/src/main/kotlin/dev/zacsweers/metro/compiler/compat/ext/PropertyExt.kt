@file:JvmMultifileClass @file:JvmName("ExtMethods")

package dev.zacsweers.metro.compiler.compat.ext

import dev.zacsweers.metro.compiler.compat.CompatApi
import org.jetbrains.kotlin.fir.declarations.FirProperty

@CompatApi(
		since = "2.2.20-ij252-24",
		reason = CompatApi.Reason.ABI_CHANGE,
		message = "Moved the function to a different file."
)
val FirProperty.isLocal: Boolean
	get() = this.symbol.isLocal