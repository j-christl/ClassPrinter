package me.inplex.classprinter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ClassPrinter {
	
	/**
	 * 
	 * Prints the given class
	 * 
	 * @param clazz			the class to print
	 * @param comment		if set to true, the class will be commented
	 * 						containing the number of constructors, methods, fields and inner classes
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	
	public static void printClass(Class<?> clazz, boolean comment) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (comment)
			System.out.println("/**\n * " + new GregorianCalendar().getTime().toString() + "\n * " + clazz.getName() + "\n*/\n");
		// Package
		if (clazz.getPackage() != null) // inner class
			System.out.println("package " + clazz.getPackage().getName() + ";\n");
		// Class
		StringBuilder impl = new StringBuilder();
		for (int i = 0; i < clazz.getInterfaces().length; i++) {
			Class<?> cc = clazz.getInterfaces()[i];
			impl.append(cc.getSimpleName());
			if (i + 1 < clazz.getInterfaces().length) {
				impl.append(", ");
			}
		}
		StringBuilder type = new StringBuilder();
		for (int i = 0; i < clazz.getTypeParameters().length; i++) {
			TypeVariable<?> tv = clazz.getTypeParameters()[i];
			type.append(tv.getName());
			if (i + 1 < clazz.getTypeParameters().length) {
				type.append(", ");
			}
		}
		System.out.println((Modifier.isPublic(clazz.getModifiers()) ? "public " : (Modifier.isProtected(clazz.getModifiers()) ? "protected " : "private "))
				+ (Modifier.isAbstract(clazz.getModifiers()) ? "abstract " : "") + (Modifier.isFinal(clazz.getModifiers()) ? "final " : "") + "class "
				+ clazz.getSimpleName() + (type.toString().length() == 0 ? "" : ("<" + type + ">"))
				+ (clazz.getSuperclass().getSimpleName().equals("Object") ? "" : (" extends " + clazz.getSuperclass().getSimpleName()))
				+ (impl.toString().length() == 0 ? "" : " implements " + impl.toString()) + " {");
		System.out.println("");
		// Fields
		if (comment)
			System.out.println("\t/**\n\t * " + clazz.getDeclaredFields().length + " Field" + (clazz.getDeclaredFields().length != 1 ? "s" : "") + "\n\t*/");
		// to find System.out.println default values
		// create an instance using the default constructor
		HashMap<String, Object> defValues = new HashMap<String, Object>();
		boolean defConstructor = false;
		if (!Modifier.isAbstract(clazz.getModifiers())) {
			for (Constructor<?> con : clazz.getDeclaredConstructors()) {
				con.setAccessible(true);
				if (con.getParameterTypes().length == 0) {
					defConstructor = true;
					// Default Constructor with no args
					Object o = con.newInstance();
					for (Field f : clazz.getDeclaredFields()) {
						f.setAccessible(true);
						defValues.put(f.getName(), f.get(o));
					}
					o = null;
				}
			}
			if (!defConstructor) {
				for (Field f : clazz.getDeclaredFields()) {
					f.setAccessible(true);
					if (Modifier.isStatic(f.getModifiers())) {
						if (f.get(null) != null) {
							defValues.put(f.getName(), f.get(null));
						}
					}
				}
			}
		}
		for (Field f : clazz.getDeclaredFields()) {
			boolean _private = Modifier.isPrivate(f.getModifiers());
			boolean _public = Modifier.isPublic(f.getModifiers());
			boolean _protected = Modifier.isProtected(f.getModifiers());
			boolean _static = Modifier.isStatic(f.getModifiers());
			boolean _final = Modifier.isFinal(f.getModifiers());
			boolean _transient = Modifier.isTransient(f.getModifiers());
			boolean _volatile = Modifier.isVolatile(f.getModifiers());
			String mod = (_private ? "private " : (_public ? "public " : (_protected ? "protected " : ""))) + (_static ? "static " : "")
					+ (_final ? "final " : "") + (_transient ? "transient " : "") + (_volatile ? "volatile " : "");
			System.out.println("\t" + mod + f.getType().getSimpleName() + " " + f.getName() + ""
					+ (defValues.containsKey(f.getName()) ? (defValues.get(f.getName()) != null ? " = " + defValues.get(f.getName()) : "") : "")
					+ ";");
		}
		System.out.println("");
		// Constructors
		if (comment)
			System.out.println("\t/**\n\t * " + clazz.getDeclaredConstructors().length + " Constructor"
					+ (clazz.getDeclaredConstructors().length != 1 ? "s" : "") + "\n\t*/");
		for (Constructor<?> co : clazz.getDeclaredConstructors()) {
			boolean _private = Modifier.isPrivate(clazz.getModifiers());
			boolean _public = Modifier.isPublic(clazz.getModifiers());
			boolean _protected = Modifier.isProtected(clazz.getModifiers());
			String mod = (_private ? "private " : (_public ? "public " : (_protected ? "protected " : "")));
			StringBuilder par = new StringBuilder();
			for (int i = 0; i < co.getParameterTypes().length; i++) {
				Class<?> cl = co.getParameterTypes()[i];
				par.append(cl.getSimpleName() + " arg" + i);
				if (i + 1 < co.getParameterTypes().length) {
					par.append(", ");
				}
			}
			StringBuilder exc = new StringBuilder();
			for (int i = 0; i < co.getExceptionTypes().length; i++) {
				Class<?> cl = co.getExceptionTypes()[i];
				exc.append(cl.getSimpleName());
				if (i + 1 < co.getExceptionTypes().length) {
					exc.append(", ");
				}
			}
			System.out.println("\t" + mod + co.getDeclaringClass().getSimpleName() + "(" + par + ")"
					+ (exc.toString().length() == 0 ? "" : (" throws " + exc)) + ";");
		}
		System.out.println("");
		// Methods
		if (comment)
			System.out.println("\t/**\n\t * " + clazz.getDeclaredMethods().length + " Method" + (clazz.getDeclaredMethods().length != 1 ? "s" : "")
					+ "\n\t*/");
		for (Method m : clazz.getDeclaredMethods()) {
			boolean _private = Modifier.isPrivate(m.getModifiers());
			boolean _public = Modifier.isPublic(m.getModifiers());
			boolean _protected = Modifier.isProtected(m.getModifiers());
			boolean _static = Modifier.isStatic(m.getModifiers());
			boolean _abstract = Modifier.isAbstract(m.getModifiers());
			boolean _native = Modifier.isNative(m.getModifiers());
			boolean _synchronized = Modifier.isSynchronized(m.getModifiers());
			boolean _strict = Modifier.isStrict(m.getModifiers());
			boolean _final = Modifier.isFinal(m.getModifiers());
			String mod = (_private ? "private " : (_public ? "public " : (_protected ? "protected " : ""))) + (_static ? "static " : "")
					+ (_abstract ? "abstract " : "") + (_native ? "native " : "") + (_synchronized ? "synchronized " : "")
					+ (_strict ? "strictpfp " : "") + (_final ? "final " : "");
			StringBuilder par = new StringBuilder();
			for (int i = 0; i < m.getParameterTypes().length; i++) {
				Class<?> cl = m.getParameterTypes()[i];
				par.append(cl.getSimpleName() + " arg" + i);
				if (i + 1 < m.getParameterTypes().length) {
					par.append(", ");
				}
			}
			StringBuilder exc = new StringBuilder();
			for (int i = 0; i < m.getExceptionTypes().length; i++) {
				Class<?> cl = m.getExceptionTypes()[i];
				exc.append(cl.getSimpleName());
				if (i + 1 < m.getExceptionTypes().length) {
					exc.append(", ");
				}
			}
			for (int i = 0; i < m.getDeclaredAnnotations().length; i++) {
				Annotation a = m.getDeclaredAnnotations()[i];
				System.out.println("\t@" + a.annotationType().getSimpleName());
			}
			System.out.println("\t" + mod + m.getReturnType().getSimpleName() + " " + m.getName() + "(" + par + ")"
					+ (exc.toString().length() == 0 ? "" : (" throws " + exc)) + ";");
		}
		// Inner Classes
		if (comment)
			System.out.println("\n\t/**\n\t * " + clazz.getDeclaredClasses().length + " Inner Class" + (clazz.getDeclaredMethods().length != 1 ? "es" : "")
					+ "\n\t*/");
		for (int i = 0; i < clazz.getDeclaredClasses().length; i++) {
			Class<?> ic = clazz.getDeclaredClasses()[i];
			boolean _private = Modifier.isPrivate(ic.getModifiers());
			boolean _protected = Modifier.isProtected(ic.getModifiers());
			boolean _public = Modifier.isPublic(ic.getModifiers());
			boolean _abstract = Modifier.isAbstract(ic.getModifiers());
			boolean _final = Modifier.isFinal(ic.getModifiers());
			System.out.println("\t" + (_private ? "private " : "") + (_public ? "public " : "") + (_protected ? "protected " : "")
					+ (_abstract ? "abstract " : "") + (_final ? "final " : "") + "class " + ic.getSimpleName() + ";");
		}

		System.out.println("}");
	}
}