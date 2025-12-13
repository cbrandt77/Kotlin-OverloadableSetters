package cb77.lang.plugins.kt.overloadablesetters.fir.diagnostics

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