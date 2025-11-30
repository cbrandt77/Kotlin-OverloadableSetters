package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.checkers.declaration.property

import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
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
object FirChecker_AnnotatedPropertyIsMutable : FirPropertyChecker(MppCheckerKind.Common) {
	context(context: CheckerContext, reporter: DiagnosticReporter)
	override fun check(declaration: FirProperty) {
		if (declaration.origin != FirDeclarationOrigin.Source || !declaration.symbol.supportsCustomSetters(context.session))
			return;
		
		if (!declaration.isVar)
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_ANNOTATED_PROP_NOT_MUTABLE)
	}
}