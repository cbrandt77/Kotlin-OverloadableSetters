package cb77.lang.plugins.kt.overloadablesetters.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.mapToSetOrEmpty

val FirSession.setterOverloadFinderService: FirSetterOverloadFinderService by FirSession.sessionComponentAccessor()

class FirSetterOverloadFinderService(pSession: FirSession, val propertyAnnotationIds: Set<ClassId>) : FirExtensionSessionComponent(pSession) {
	companion object {
		fun getFactory(annotations: Collection<String>): Factory {
			return Factory { session ->
				FirSetterOverloadFinderService(session, annotations.mapToSetOrEmpty { ClassId.topLevel(FqName(it)) })
			}
		}
	}
	
	private val cache_propertySupportsOverloadedSetters: FirCache<FirPropertySymbol, Boolean, Nothing?> = session.firCachesFactory.createCache { symbol, _ ->
		propertyAnnotationIds.any { symbol.hasAnnotation(it, session) }
	}
	
	fun propertySupportsOverloadedSetters(symbol: FirPropertySymbol): Boolean {
		return cache_propertySupportsOverloadedSetters.getValue(symbol)
	}
}


