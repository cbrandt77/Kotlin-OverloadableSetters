package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.checkers.expression.assignment

import cb77.lang.plugins.kt.overloadablesetters.util.getPropertyNameFromSetterName
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.diagnostics.FirDiagnosticHolder
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.isError
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeAmbiguityError
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeDiagnosticWithSingleCandidate
import org.jetbrains.kotlin.fir.resolve.diagnostics.ConeUnresolvedNameError

//TODO do I even need this? it should link automatically, and just say "no function `set-bar` found"
object FirChecker_UseSiteAssignment : FirFunctionCallChecker(MppCheckerKind.Common) {
	context(ctx: CheckerContext, reporter: DiagnosticReporter)
	override fun check(expression: FirFunctionCall) {
		if (!expression.couldBeOverloadedSetterCall())
			return;
		
		val referencedPropertyName = expression.getErrorPropertyName()
		
		
	}
	
	private fun FirFunctionCall.couldBeOverloadedSetterCall(): Boolean {
		return source?.kind === KtFakeSourceElementKind.AssignmentPluginAltered && this.arguments.size == 1
	}
	
	private fun FirFunctionCall.getErrorPropertyName(): String? {
		if (!this.calleeReference.isError())
			return null;
		
		val calleeName = when (val diagnostic = (this.calleeReference as FirDiagnosticHolder).diagnostic) {
			is ConeAmbiguityError -> diagnostic.name
			is ConeDiagnosticWithSingleCandidate -> diagnostic.candidate.callInfo.name
			is ConeUnresolvedNameError -> diagnostic.name
			else -> calleeReference.name
		}
		
		return getPropertyNameFromSetterName(calleeName)
	}
}