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


Diagnostics:
- thing