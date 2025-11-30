package cb77.lang.plugins.kt.overloadablesetters.fir.extensions.generators

import cb77.lang.plugins.kt.overloadablesetters.util.getDeclaredAndInheritedCallables
import cb77.lang.plugins.kt.overloadablesetters.util.makeSetterName
import cb77.lang.plugins.kt.overloadablesetters.util.supportsCustomSetters
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.declarations.utils.canNarrowDownGetterType
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildEnumEntryDeserializedAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildVariableAssignment
import org.jetbrains.kotlin.fir.expressions.impl.FirSingleExpressionBlock
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.toEffectiveVisibility
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.resolve.deprecation.DeprecationLevelValue
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.utils.addToStdlib.runIf
import org.jetbrains.kotlin.utils.mapToSetOrEmpty

/**
 * Adds a `set-{propName}` function with the property's original type to the class,
 * so we can blindly remap `foo.bar = baz` to `foo.set-bar(baz)`
 */
class FirSetterDefaultSetterGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
	
	object OverloadableSettersDeclarationKey : GeneratedDeclarationKey()
	
	/**
	 * Given a class, gets all of its properties that support custom setters, mapped to their setter names.
	 *
	 * For example, given:
	 * ```
	 * class Foo {
	 *    @HasCustomSetters
	 *    val bar: Int
	 * }
	 * ```
	 * Calling `cache.get(Foo::class)` would essentially return `{ "setBar": Foo::bar }`.
	 *
	 * The map is because we can't just do the functions, we have to first say what the names of the functions we want to emit are and THEN do the functions, which necessitates two lookups per property.
	 */
	private val annotatedPropertiesByClass: FirCache<FirClassSymbol<*>, Map<Name, FirPropertySymbol>, Nothing?> = session.firCachesFactory.createCache { owningClass, _ ->
		calledFromCache.set(true)
		
		// Only take the properties declared inside this class, not a full scope search. Any supertypes should autogenrate their _own_ `set-bar` functions.
		val ret = owningClass.declaredProperties(session)
			.filter { it.supportsCustomSetters(session) }
			.associateBy { Name.identifier(makeSetterName(it)) }
		
		calledFromCache.set(false)
		
		return@createCache ret
	}
	
	/**
	 * Prevent infinite recursion from `cache#getValue` -> `owningClass#declaredProperties` -> `getCallableNamesForClass` -> `cache#getValue`
 	 */
	private val calledFromCache: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
	
	override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
		if (calledFromCache.get())
			return emptySet()
		
		val owningClass = context.owner
		return annotatedPropertiesByClass.getValue(owningClass)
										  .values
										  .mapToSetOrEmpty { Name.identifier(makeSetterName(it.name)) }
	}
	
	// thankfully the `getCallableNames` method doesn't have to be side-effect-free
	override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
		val owningClass = context?.owner ?: return emptyList()
		val propertyForSetter = annotatedPropertiesByClass.getValue(owningClass)[callableId.callableName]
		                        ?: throw IllegalArgumentException("No property found in class $owningClass for name ${callableId.callableName}")
		
		return listOf(makeDefaultSetterStub(owningClass, propertyForSetter, callableId.callableName).symbol)
	}
	
	/**
	 * Given `bar: String`, make a `setBar(String)` function with JvmName "$$OverloadableSetters$setBar" to not conflict with the actual property setter
	 */
	private fun makeDefaultSetterStub(owningClass: FirClassSymbol<*>, prop: FirPropertySymbol, setterName: Name): FirSimpleFunction {
		fun getSourceForFirDeclaration(): KtSourceElement? {
			return owningClass.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
		}
		
		val callableId = CallableId(owningClass.classId, setterName)
		val ourSource = getSourceForFirDeclaration()
		return buildSimpleFunction {
			resolvePhase = FirResolvePhase.BODY_RESOLVE
			moduleData = session.moduleData
			origin = OverloadableSettersDeclarationKey.origin
			
			source = ourSource
			
			symbol = FirNamedFunctionSymbol(callableId)
			name = callableId.callableName
			
			status = FirResolvedDeclarationStatusImpl(
					Visibilities.Public,
					Modality.FINAL,
					Visibilities.Public.toEffectiveVisibility(owningClass, forClass=true)
			)
			
			dispatchReceiverType = owningClass.defaultType()
			
			returnTypeRef = session.builtinTypes.unitType
			
			annotations += listOf(
					// JvmName TODO figure out how to mangle names
					buildAnnotation {
						source = ourSource
						annotationTypeRef = buildResolvedTypeRef {
							source = ourSource
							coneType = StandardClassIds.Annotations.jvmName.constructClassLikeType()
						}
						argumentMapping = buildAnnotationArgumentMapping {
							mapping[StandardClassIds.Annotations.ParameterNames.parameterNameName] = buildLiteralExpression(ourSource, ConstantValueKind.String, getJvmNameForSetter(setterName.asString()), setType=true)
						}
					},
//					// Deprecated(HIDDEN) TODO figure out how to hide the declaration from IDE autocomplete
//					buildAnnotation {
//						source = ourSource
//						annotationTypeRef = buildResolvedTypeRef {
//							source = ourSource
//							coneType = StandardClassIds.Annotations.Deprecated.constructClassLikeType()
//						}
//						argumentMapping = buildAnnotationArgumentMapping {
//							mapping[StandardClassIds.Annotations.ParameterNames.deprecatedMessage] = buildLiteralExpression(ourSource, ConstantValueKind.String, "Use actual property setter", setType=true)
//							mapping[StandardClassIds.Annotations.ParameterNames.deprecatedLevel] = buildEnumEntryDeserializedAccessExpression {
//								enumClassId = StandardClassIds.DeprecationLevel
//								enumEntryName = Name.identifier(DeprecationLevelValue.HIDDEN.name)
//							}
//						}
//					}
			)
			
			val parameter = buildValueParameter {
				source = ourSource
				containingDeclarationSymbol = this@buildSimpleFunction.symbol
				moduleData = session.moduleData
				origin = OverloadableSettersDeclarationKey.origin
				returnTypeRef = prop.resolvedReturnTypeRef
				name = SpecialNames.IMPLICIT_SET_PARAMETER
				symbol = FirValueParameterSymbol()
				isCrossinline = false
				isNoinline = false
				isVararg = false
			}
			
			valueParameters += parameter
			
			body = FirSingleExpressionBlock(
				buildVariableAssignment {
					lValue = buildPropertyAccessExpression {
						source = ourSource
						calleeReference = buildResolvedNamedReference {
							source = ourSource
							name = prop.name
							resolvedSymbol = prop
						}
						dispatchReceiver = buildThisReceiverExpression {
							calleeReference = buildImplicitThisReference {
								boundSymbol = owningClass
							}
							coneTypeOrNull = owningClass.defaultType()
						}
						coneTypeOrNull = runIf(prop.canNarrowDownGetterType) { prop.resolvedReturnType }
					}
					rValue = buildPropertyAccessExpression {
						source = ourSource
						calleeReference = buildResolvedNamedReference {
							source = ourSource
							name = SpecialNames.IMPLICIT_SET_PARAMETER
							resolvedSymbol = parameter.symbol
						}
						coneTypeOrNull = parameter.returnTypeRef.coneTypeOrNull
					}
				}
			)
		}
	}
}

private fun getJvmNameForSetter(setterName: String): String {
	return $$$"$$OverloadedSetters$" + setterName
}

