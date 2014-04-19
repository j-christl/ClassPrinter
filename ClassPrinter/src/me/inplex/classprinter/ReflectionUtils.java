package me.inplex.classprinter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class ReflectionUtils {
	
	/**
	 * Create a String from the given Object
	 * @param o the object
	 * @return the String created using the Object
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	
	public static String toString(Object o) throws IllegalArgumentException, IllegalAccessException {
		Class<?> c = o.getClass();
		StringBuilder sb = new StringBuilder();
		sb.append(c.getName());
		sb.append("[");
		for (int i = 0; i < c.getFields().length; i++) {
			Field f = c.getFields()[i];
			if ((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL)
				continue;
			sb.append(f.getName());
			sb.append("=");
			Class<?> type = f.getType();
			String sType = type.getName();
			if (type.isPrimitive()) {
				sType = wrapToWrapper(type).getName();
			}
			sb.append(sType);
			sb.append("@");
			sb.append(f.get(o));
			if (i + 1 < c.getFields().length) {
				if (!((c.getFields()[i + 1].getModifiers() & Modifier.FINAL) == Modifier.FINAL))
					sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Create an Object from the given String
	 * @param s the String
	 * @return the Object created using the String
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */

	public static Object fromString(String s) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object o;
		String className = s.split("\\[")[0];
		Class<?> c = Class.forName(className);
		o = c.newInstance();
		String fieldsString = s.replaceFirst(className + "\\[", "").replace("\\]", "");
		if (fieldsString.endsWith("]")) {
			fieldsString = fieldsString.substring(0, fieldsString.length() - 1);

		}
		System.out.println(fieldsString);
		String[] fieldsSplit = fieldsString.split(",");
		for (int i = 0; i < fieldsSplit.length; i++) {
			String fString = fieldsSplit[i];

			String fName = fString.split("=")[0];
			String fTypeAndVal = fString.split("=")[1];
			String fTypeString = fTypeAndVal.split("@")[0];
			String fVal = fTypeAndVal.split("@")[1];
			Object val = null;
			Class<?> fType = Class.forName(fTypeString);
			if (wrapperPrimitiveMap.keySet().contains(fType)) {
				Constructor<?> ctor = Class.forName(fTypeString).getConstructor(wrapToPrimitive(fType));
				System.out.println(ctor.toGenericString());
				val = ctor.newInstance(ctor.newInstance(primitiveToObject(fType, fVal)));
			}
			Field f = c.getField(fName);
			if (!f.isAccessible())
				f.setAccessible(true);
			f.set(o, val);
		}
		return o;
	}

	private static Object primitiveToObject(Class<?> clazz, String value) {
		if (Boolean.class == clazz)
			return Boolean.parseBoolean(value);
		if (Byte.class == clazz)
			return Byte.parseByte(value);
		if (Short.class == clazz)
			return Short.parseShort(value);
		if (Integer.class == clazz)
			return Integer.parseInt(value);
		if (Long.class == clazz)
			return Long.parseLong(value);
		if (Float.class == clazz)
			return Float.parseFloat(value);
		if (Double.class == clazz)
			return Double.parseDouble(value);
		return value;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> wrapToWrapper(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) primitiveWrapperMap.get(c) : c;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> wrapToPrimitive(Class<T> c) {
		return c.isPrimitive() ? c : (Class<T>) wrapperPrimitiveMap.get(c);
	}

	private static HashMap<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapperMap.put(boolean.class, Boolean.class);
		primitiveWrapperMap.put(byte.class, Byte.class);
		primitiveWrapperMap.put(char.class, Character.class);
		primitiveWrapperMap.put(double.class, Double.class);
		primitiveWrapperMap.put(float.class, Float.class);
		primitiveWrapperMap.put(int.class, Integer.class);
		primitiveWrapperMap.put(long.class, Long.class);
		primitiveWrapperMap.put(short.class, Short.class);
		primitiveWrapperMap.put(void.class, Void.class);
	}

	private static HashMap<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<Class<?>, Class<?>>();
	static {
		for (Class<?> c : primitiveWrapperMap.keySet()) {
			wrapperPrimitiveMap.put(primitiveWrapperMap.get(c), c);
		}
	}

}