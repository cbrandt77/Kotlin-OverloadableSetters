package cb77.lang.plugins.kt.overloadablesetters.fir.extensions.generators

import cb77.lang.plugins.kt.overloadablesetters.fir.setterOverloadFinderService
import dev.zacsweers.metro.compiler.compat.CompatContext
import dev.zacsweers.metro.compiler.compat.CompatContext.Companion.fakeElement
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
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
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.toEffectiveVisibility
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.resolve.deprecation.DeprecationLevelValue
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.utils.addToStdlib.runIf

/**
 * Adds a `set-{propName}` function with the property's original type to the class,
 * so we can blindly remap `foo.bar = baz` to `foo.set-bar(baz)`
 */
class FirSetterDefaultSetterGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
	object OverloadableSettersDeclarationKey : GeneratedDeclarationKey()
	
	override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
		return getDefaultSettersForClass(classSymbol.classId)
		       ?: emptySet()
	}
	
	override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
		val owningClass = context?.owner
		                  ?: return emptyList()
		
		val propForSetter = getPropertyForSetter(callableId)
		                    ?: return emptyList()
		
		return listOf(makeDefaultSetterStub(owningClass, propForSetter, callableId).symbol)
	}
	
	private fun getDefaultSettersForClass(owner: ClassId): Set<Name>? {
		return session.setterOverloadFinderService.allAnnotatedProperties[owner]?.keys
	}
	
	private fun getPropertyForSetter(id: CallableId): FirPropertySymbol? {
		val classid = id.classId ?: return null;
		return session.setterOverloadFinderService.allAnnotatedProperties[classid]?.get(id.callableName)
	}
	
	/**
	 * Given `bar: String`, make a `setBar(String)` function with JvmName "$$OverloadableSetters$setBar" to not conflict with the actual property setter
	 */
	private fun makeDefaultSetterStub(owningClass: FirClassLikeSymbol<*>, prop: FirPropertySymbol, callableId: CallableId): FirSimpleFunction {
		val ourSource = prop.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
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
			
			// JvmName to not conflict with actual property's setter TODO figure out how to mangle names
			annotations += buildAnnotation {
				source = ourSource
				annotationTypeRef = buildResolvedTypeRef {
					source = ourSource
					coneType = StandardClassIds.Annotations.jvmName.constructClassLikeType()
				}
				argumentMapping = buildAnnotationArgumentMapping {
					mapping[StandardClassIds.Annotations.ParameterNames.parameterNameName] = buildLiteralExpression(ourSource, ConstantValueKind.String, getJvmNameForSetter(callableId.callableName.asString()), setType=true)
				}
			}
			
			val paramName = StandardNames.DEFAULT_VALUE_PARAMETER
			
			val parameter = buildValueParameter {
				source = ourSource
				containingDeclarationSymbol = this@buildSimpleFunction.symbol
				moduleData = session.moduleData
				origin = OverloadableSettersDeclarationKey.origin
				returnTypeRef = prop.resolvedReturnTypeRef
				name = paramName
				symbol = CompatContext.newFirValueParameterSymbol(paramName)
				
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
							name = parameter.name
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

