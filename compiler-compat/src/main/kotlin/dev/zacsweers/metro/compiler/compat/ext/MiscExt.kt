package dev.zacsweers.metro.compiler.compat.ext

import dev.zacsweers.metro.compiler.compat.CompatApi
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneType

/**
 * Returns the FirRegularClass associated with this
 * or null of something goes wrong.
 */
@CompatApi(
		since="2.3.20-ij253-45",
		reason=CompatApi.Reason.ABI_CHANGE,
		message="Moved to different file."
)
fun FirTypeRef.toRegularClassSymbol(session: FirSession): FirRegularClassSymbol? {
	return coneType.toRegularClassSymbol(session)
}