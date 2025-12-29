/*
 * Copyright (C) 2025 Caleb Brandt
 *
 * This file is part of Overloadable Setters.
 *
 * Overloadable Setters is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overloadable Setters is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.cbrandt77.kt.overloadablesetters.fir

import io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.checkers.declaration.property.FirChecker_AnnotatedPropertyAllowsAnnotation
import io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.checkers.declaration.function.FirChecker_SetterFunctionLinter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

class FirOverloadableSettersCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
	override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
		override val propertyCheckers: Set<FirPropertyChecker>
			get() = setOf(FirChecker_AnnotatedPropertyAllowsAnnotation)
		
		override val functionCheckers: Set<FirFunctionChecker>
			get() = setOf(FirChecker_SetterFunctionLinter)
	}
	
//	override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
//		override val variableAssignmentCheckers: Set<FirVariableAssignmentChecker>
//			get() = super.variableAssignmentCheckers
//	}
}