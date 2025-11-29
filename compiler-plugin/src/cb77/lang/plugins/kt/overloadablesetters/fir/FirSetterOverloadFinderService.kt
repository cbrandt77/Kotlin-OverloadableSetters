package cb77.lang.plugins.kt.overloadablesetters.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.utils.mapToSetOrEmpty

val FirSession.setterOverloadFinderService: FirSetterOverloadFinderService by FirSession.sessionComponentAccessor()

class FirSetterOverloadFinderService(session: FirSession, val propertyAnnotationIds: Set<ClassId>) : FirExtensionSessionComponent(session) {
	companion object {
		fun getFactory(annotations: Collection<String>): Factory {
			return Factory { session ->
				FirSetterOverloadFinderService(session, annotations.mapToSetOrEmpty(ClassId::fromString))
			}
		}
	}
	
	private val cache_propertySupportsOverloadedSetters: FirCache<FirPropertySymbol, Boolean, FirSession> = session.firCachesFactory.createCache { symbol, session ->
		propertyAnnotationIds.any { symbol.hasAnnotation(it, session) }
	}
	
	fun propertySupportsOverloadedSetters(symbol: FirPropertySymbol, session: FirSession): Boolean {
		return cache_propertySupportsOverloadedSetters.getValue(symbol, session)
	}
}


