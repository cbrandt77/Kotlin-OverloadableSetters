package cb77.lang.plugins.kt.overloadablesetters.fir

import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.OverloadedSetterAnnotatedPropertyChecker
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

class FirOverloadableSettersCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
	override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
		override val propertyCheckers: Set<FirPropertyChecker>
			get() = setOf(OverloadedSetterAnnotatedPropertyChecker)
	}
	
//	override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
//		override val variableAssignmentCheckers: Set<FirVariableAssignmentChecker>
//			get() = super.variableAssignmentCheckers
//	}
}