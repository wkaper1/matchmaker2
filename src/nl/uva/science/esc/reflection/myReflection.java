package nl.uva.science.esc.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is a library of static methods to enable easy reflection.
 * It will be used to create two factories who's configuration is made more
 * DRY using reflection: ProblemFactory and TechniquesFactory
 * 
 * Reflection is made easy mainly by doing all the trying and catching here.
 * The simplifying assumption is that calling non-existing classes or 
 * methods is fatal, so we want to see a stack trace.
 * 
 * A second simplification follows from only class and method NAMES being
 * available as Strings in our (factory) application, therefore method invocation 
 * and class instantiation using only such names is made easy.
 * 
 * It is not a complete reflection library (even given the simplifying assumptions)
 * it contains only what I need here. But I should be able to enlarge it when
 * similar simplifications are profitable elsewhere.
 * @author kaper
 *
 */
public class myReflection {
	
	/**
	 * Get a Class object, given the fully qualified name of the class
	 * @param classname
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClass(String classname) {
		Class cls = null;
		try {
			cls = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}//end try		
		return cls;
	}//end getClass
	
	/**
	 * Get a method object given method name and Class object
	 * @param cls
	 * @param methodname
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Method getMethodFromClass(Class cls, String methodname) {
		Method m = null;
		try {
			m = cls.getMethod(methodname);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();			
		}//end try
		return m;
	}///end getMethod
	
	/**
	 * Get a constructor given class name and argument types
	 * @param cls
	 * @param argtypes, an array of Class objects that describe the arguments
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Constructor getConstructorFromClass(Class cls, Class[] argtypes) {
		Constructor c = null;
		try {
			c = cls.getConstructor(argtypes);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();			
		}//end try		
		return c;
	}//end getConstructorFromClass
	
	/**
	 * Convenience: get a Method object given class and method names
	 * @param classname
	 * @param methodname
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Method getMethod(String classname, String methodname) {
		Class cls = getClass(classname);
		return getMethodFromClass(cls, methodname);
	}//end getMethod
		
	/**
	 * Invoke a static method
	 * @param classname, name of class that contains the method
	 * @param methodname
	 * @param args, an array containing the arguments
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object invokeStaticMethod(
		String classname, String methodname, Object[] args
	) {
		Class cls = getClass(classname);
		Method m = getMethodFromClass(cls, methodname);		
		Object o = null;
		try {
			o = m.invoke(null, args);
		} catch (
			Exception e
		) {
			e.printStackTrace();
		}//end try
		return o;
	}//end invokeStaticMethod
	
	/**
	 * Instantiate a class
	 * @param classname, name of class to instantiate
	 * @param argtypes, array of Class objects describing the constructor signature
	 * @param args, the arguments themselves
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object instantiate(
		String classname, Class[] argtypes, Object[] args
	) {
		Object o = null;
		Class cls = getClass(classname);
		if (args.length==0) {
			//we can use the no-arg constructor, using only the Class
			try {
				o = cls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}//end try
		}
		else {
			//we need to get the right constructor-type
			Constructor cs = getConstructorFromClass(cls, argtypes);
			try {
				o = cs.newInstance(args);
			} catch (
				Exception e
			) {
				e.printStackTrace();
			}//end try
		}//end if
		return o;
	}//end instantiate
	
	/**
	 * Convenience: instantiate a class if there is a no-arg constructor
	 * @param classname
	 * @return
	 */
	public static Object instantiateNoArgs(String classname) {
		return instantiate(classname, new Class[] {}, new Object[] {});
	}//end instantiateNoArgs

}//end class
