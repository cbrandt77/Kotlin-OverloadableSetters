package cb77.lang.plugins.kt.overloadablesetters.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.FirSessionComponent
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.mapToSetOrEmpty

internal val FirSession.setterOverloadPredicates: Predicates by FirSession.sessionComponentAccessor()

internal class Predicates(session: FirSession, private val propertyAnnotations: Set<FqName>) : FirExtensionSessionComponent(session) {
	companion object {
		fun getFactory(propertyAnnotations: Collection<String>): Factory {
			return Factory { session ->
				Predicates(session, propertyAnnotations.mapToSetOrEmpty { FqName(it) })
			}
		}
	}
	
	override fun FirDeclarationPredicateRegistrar.registerPredicates() {
		register(hasCustomSetters)
	}
	
	val hasCustomSetters = LookupPredicate.create {
		annotated(propertyAnnotations)
	}
}