package cb77.lang.plugins.kt.overloadablesetters.fir.extensions.transformers

import cb77.lang.plugins.kt.overloadablesetters.util.makeSetterName
import cb77.lang.plugins.kt.overloadablesetters.util.supportsCustomSetters
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.expressions.FirFunctionCallOrigin
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.expressions.buildUnaryArgumentList
import org.jetbrains.kotlin.fir.expressions.calleeReference
import org.jetbrains.kotlin.fir.expressions.dispatchReceiver
import org.jetbrains.kotlin.fir.extensions.FirAssignExpressionAltererExtension
import org.jetbrains.kotlin.fir.references.builder.buildSimpleNamedReference
import org.jetbrains.kotlin.fir.references.toResolvedVariableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.Name

/**
 * For any property `Foo#bar` that is annotated with `@HasCustomSetters`:
 * - Replaces all expressions `foo.bar = baz` with `foo.setBar(baz)`
 */
@OptIn(DirectDeclarationsAccess::class)
class FirSetterAssignmentAlterer(session: FirSession) : FirAssignExpressionAltererExtension(session) {
	/**
	 * Only transforms class or extension properties that are mutable and annotated with [HasCustomSetters]
	 */
	override fun transformVariableAssignment(variableAssignment: FirVariableAssignment): FirStatement? {
		val lSymbol: FirPropertySymbol = variableAssignment.calleeReference?.toResolvedVariableSymbol()
			.takeIf {
				it is FirPropertySymbol && it.isVar && !it.isLocal && it.supportsCustomSetters(session)
			} as FirPropertySymbol? ?: return null;
		
		// can't get the type of the rvalue during this phase. need to just check if it supports custom setters and use the setBar method blindly
		return buildFunctionCall(variableAssignment, lSymbol)
	}
	
	fun buildFunctionCall(variableAssignment: FirVariableAssignment, targetProperty: FirPropertySymbol): FirStatement {
		val rightArgument = variableAssignment.rValue
		
		val setterName = Name.identifier(makeSetterName(targetProperty))
		
		return org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall {
			source = variableAssignment.source?.fakeElement(KtFakeSourceElementKind.AssignmentPluginAltered)
			explicitReceiver = variableAssignment.dispatchReceiver
			argumentList = buildUnaryArgumentList(rightArgument)
			calleeReference = buildSimpleNamedReference {
				source = variableAssignment.source
				name = setterName
			}
			origin = FirFunctionCallOrigin.Regular
		}
	}
}