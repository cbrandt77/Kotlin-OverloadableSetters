package cb77.lang.plugins.kt.overloadablesetters.fir

import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.checkers.declaration.property.FirChecker_AnnotatedPropertyIsMutable
import cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics.checkers.declaration.function.FirChecker_SetterFunctionLinter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

class FirOverloadableSettersCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
	override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
		override val propertyCheckers: Set<FirPropertyChecker>
			get() = setOf(FirChecker_AnnotatedPropertyIsMutable)
		
		override val functionCheckers: Set<FirFunctionChecker>
			get() = setOf(FirChecker_SetterFunctionLinter)
	}
	
//	override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
//		override val variableAssignmentCheckers: Set<FirVariableAssignmentChecker>
//			get() = super.variableAssignmentCheckers
//	}
}