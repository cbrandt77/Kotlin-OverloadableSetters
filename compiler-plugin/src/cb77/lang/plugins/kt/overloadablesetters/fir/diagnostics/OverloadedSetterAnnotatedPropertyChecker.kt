package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadedSetterErrors.PROP_NOT_MUTABLE
import cb77.lang.plugins.kt.overloadablesetters.util.supportsCustomSetters
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirProperty

/**
 * Make sure an annotated property is actually mutable
 */
object OverloadedSetterAnnotatedPropertyChecker : FirPropertyChecker(MppCheckerKind.Common) {
	context(context: CheckerContext, reporter: DiagnosticReporter)
	override fun check(declaration: FirProperty) {
		if (declaration.origin != FirDeclarationOrigin.Source || !declaration.symbol.supportsCustomSetters(context.session))
			return;
		
		if (!declaration.isVar)
			reporter.reportOn(declaration.source, PROP_NOT_MUTABLE)
	}
}