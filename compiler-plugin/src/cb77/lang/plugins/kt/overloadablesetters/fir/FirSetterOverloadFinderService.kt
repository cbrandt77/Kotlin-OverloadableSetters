package cb77.lang.plugins.kt.overloadablesetters.fir

import cb77.lang.plugins.kt.overloadablesetters.util.makeSetterNameId
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
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
	
	val pred_shouldRemapPropertySets: LookupPredicate
	val pred_selfHasOverloadAnnotation: LookupPredicate
	
	init {
		val annotationFqNames = propertyAnnotationIds.map { it.asSingleFqName() }
		with (LookupPredicate.BuilderContext) {
			pred_selfHasOverloadAnnotation = annotated(annotationFqNames)
			pred_shouldRemapPropertySets = pred_selfHasOverloadAnnotation or ancestorAnnotated(annotationFqNames)
		}
	}
	
	override fun FirDeclarationPredicateRegistrar.registerPredicates() {
		register(pred_selfHasOverloadAnnotation, pred_shouldRemapPropertySets)
	}
	
	/**
	 * Map of class ids to its properties that have custom setters, keyed by the name of the setter it would have
	 */
	private val _allAnnotatedProperties: FirCache<Unit, Map<ClassId, Map<Name, FirPropertySymbol>>, Nothing?> = session.firCachesFactory.createCache { _, _ ->
		val settersByClass = HashMap<ClassId, MutableMap<Name, FirPropertySymbol>>()
		
		for (prop in session.predicateBasedProvider.getSymbolsByPredicate(pred_selfHasOverloadAnnotation)) {
			if (prop !is FirPropertySymbol || prop.isLocal || !prop.isVar || prop.dispatchReceiverType == null)
				continue;
			
			val setterName = makeSetterNameId(prop.name)
			val containingClassId = prop.dispatchReceiverType?.classId ?: continue;
			settersByClass.computeIfAbsent(containingClassId) { HashMap() }.put(setterName, prop)
		}
		
		// TODO compress map of maps. Neither kotlinx.ImmutableMap nor guava.ImmutableMap work in prod - kotlinx actually doesn't compile in dev, and guava doesn't work in prod fsr
		settersByClass
	}

	
	val allAnnotatedProperties: Map<ClassId, Map<Name, FirPropertySymbol>>
		get() = _allAnnotatedProperties.getValue(Unit)
	
	@OptIn(SymbolInternals::class)
	fun FirPropertySymbol.hasOverloadedSetterAnnotation(): Boolean {
		return propertyAnnotationIds.any { it in this.resolvedAnnotationClassIds }
	}
	
	/**
	 * True if the assignment alterer should remap any `inst.foo = bar` to `inst.set-foo(bar)`, false otherwise.
	 */
	fun shouldRemapPropertySets(symbol: FirPropertySymbol): Boolean {
		return session.predicateBasedProvider.matches(pred_shouldRemapPropertySets, symbol)
	}
	
	/**
	 * True if `symbol` is decorated with whatever annotation means it supports overloaded setters provided for it, false otherwise.
	 */
	fun propertyDeclarationSupportsOverloads(symbol: FirPropertySymbol): Boolean {
		return session.predicateBasedProvider.matches(pred_selfHasOverloadAnnotation, symbol)
	}
}

/**
 * True if the assignment alterer should remap any `inst.foo = bar` to `inst.set-foo(bar)`, false otherwise.
 */
fun FirPropertySymbol.shouldRemapPropertySets(session: FirSession): Boolean {
	return session.setterOverloadFinderService.shouldRemapPropertySets(this)
}

fun FirPropertySymbol.declarationSupportsOverloads(session: FirSession): Boolean {
	return session.setterOverloadFinderService.propertyDeclarationSupportsOverloads(this)
}