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

package io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics

import io.github.cbrandt77.kt.overloadablesetters.fir.diagnostics.FirOverloadableSetters_ErrorTypes
import org.jetbrains.kotlin.diagnostics.AbstractKtDiagnosticFactory
import org.junit.jupiter.api.assertAll
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.Test
import kotlin.test.assertNotNull

class ErrorMessages {
	
	@Test
	fun allMessagesHaveRenderers() {
		val rendererFactory = FirOverloadableSetters_ErrorTypes.getRendererFactory()
		val executables: List<() -> Unit> = FirOverloadableSetters_ErrorTypes::class
			.declaredMemberProperties
			.mapNotNull { prop ->
				val errorType = prop.get(FirOverloadableSetters_ErrorTypes) as? AbstractKtDiagnosticFactory ?: return@mapNotNull null
				{ assertNotNull(rendererFactory.MAP[errorType], "${prop.name} does not have a message") }
			}
		
		assertAll(executables)
	}
}