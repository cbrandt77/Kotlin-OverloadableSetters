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

import io.github.cbrandt77.kt.overloadablesetters.fir.extensions.transformers.FirSetterAssignmentAlterer
import io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
import io.github.cbrandt77.kt.overloadablesetters.fir.extensions.generators.FirSetterDefaultSetterGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class FirRegistrar_OverloadableSetters(private val annotations: Collection<String> = listOf("io.github.cbrandt77.kt.overloadablesetters.HasCustomSetters")) : FirExtensionRegistrar() {
	override fun ExtensionRegistrarContext.configurePlugin() {
		+FirSetterOverloadFinderService.getFactory(annotations)
		+::FirSetterAssignmentAlterer
		+::FirSetterDefaultSetterGenerator
		+::FirOverloadableSettersCheckersExtension
		
		registerDiagnosticContainers(FirOverloadableSetters_ErrorTypes)
	}
}