So the problem is: FIR doesn't actually have type information for function calls during the assignment expression.  That sucks.

So then maybe the best we can do is "if the target class has ANY annotated setter with the right name, we set it to setBar with the fake symbol as a marker."
"then when we get to the later FIR phase where everything is resolvable, check if it's actually the same type as the property and change it back to a regular 'set' if so"
or maybe even just add a setBar(original type) function to the class and always change the assignment to the setBar function ://///// that's stupid tho



Ok, I actually think I'm going to do the "stupid" one.

Strategy:
- Assignment:
  1. Check if the property being set is annotated
  2. If it is, change the set to a `set{PropertyName}({RHS})` call
- Class:
  1. For every property that is annotated:
     - Create a `set{PropertyName}(PropertyType)` method that links straight to the default setter
     - How do we resolve setter name conflicts?
       - Make it `@JvmName("$$OverloadableSetters$set{PropertyName}")`?

There's no way to get all extension properties and methods for a class because that's resolved via import, so maybe don't even do name verification and just have it link to an arbitrary `set-bar` that could be imported if need-be

Ok genuinely: should we require setter methods to be annotated? 
Reasons to:
- It'll let us lint the declaration without requiring that the name be right first
- It'll stop linting on things that aren't intended to be setters.
- Overloaded setters are infrequent enough that it won't be intrusive
Reasons not to:
- We don't know the type during the invocation, so we're still setting it blindly either way
- It adds more moving parts
- If we aren't doing lookups each time, it might make people think their setter is fine if they shadow the original property setter

# Diagnostics:
## Function Declaration
### Acts on:
- Functions that match the setter name scheme

### Errors:
- Its parameter is the same as the original property's setter 

### Warnings: 
(should they be warnings? or should they be errors? I don't want to bind the entire codebase to my schema. I think it should just be unresolvable on the callsite-end if it doesn't match the schema.)
- The name doesn't reference a real property (should be suppressable)
  - This is fine actually. If it's an in-class method, it would be weird to reference an inaccessible extension property.
  - Still, we should use scope lookup instead of class-member lookup just to be sure.
- The name doesn't reference an _annotated_ property
- It doesn't have exactly one parameter
- It has context or type parameters
- It doesn't return Unit
