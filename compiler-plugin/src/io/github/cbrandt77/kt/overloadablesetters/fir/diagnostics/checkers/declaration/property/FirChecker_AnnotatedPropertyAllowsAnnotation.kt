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

package io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.checkers.declaration.property

import io.github.cbrandt77.kt.overloadablesetters.fir.declarationSupportsOverloads
import io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
import dev.zacsweers.metro.compiler.compat.ext.isLocal
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
object FirChecker_AnnotatedPropertyAllowsAnnotation : FirPropertyChecker(MppCheckerKind.Common) {
	context(context: CheckerContext, reporter: DiagnosticReporter)
	override fun check(declaration: FirProperty) {
		if (declaration.origin != FirDeclarationOrigin.Source || !declaration.symbol.declarationSupportsOverloads(context.session))
			return;
		
		if (!declaration.isVar)
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_ANNOTATED_PROP_NOT_MUTABLE)
		if (declaration.isLocal)
			reporter.reportOn(declaration.source, FirOverloadableSetters_ErrorTypes.SETTER_ANNOTATED_PROP_INVALID, "local property")
	}
}