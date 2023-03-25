package nl.uva.science.esc.reflection;

import java.lang.reflect.Method;

@SuppressWarnings("rawtypes")
public class ReflectionWrapper {
	protected Method method;   //the method to call, as found by reflection
	protected Class[] parameterTypes; //in invocation order
	
	/**
	 * Describe the method we'll be wrapping: class name fully qualified, method name, parameter types
	 * given as an array of Class objects.
	 */
	public ReflectionWrapper(String classname, String methodname, Class[] parameterTypes) {
		Class cls = myReflection.getClass(classname);
		this.method = myReflection.getMethodFromClass(cls, methodname, parameterTypes);
		this.parameterTypes = parameterTypes;		
	}
	
	/**
	 * Shortcut constructor: Use this if the caller is using reflection itself and already has a 
	 * Method method available
	 */
	public ReflectionWrapper(Method method, Class[] parameterTypes) {
		this.method = method;
		this.parameterTypes = parameterTypes;		
	}
	
	/**
	 * Shortcut: if you already have the Class at hand but not the method, use this one!
	 */
	public ReflectionWrapper(Class cls, String methodname, Class[] parameterTypes) {
		this.method = myReflection.getMethodFromClass(cls, methodname, parameterTypes);
		this.parameterTypes = parameterTypes;		
	}
	
	/**
	 * Run the wrapped method
	 */
	public Object run(Object[] args) {
		return myReflection.invokeStaticMethod(method, args);
	}
}
