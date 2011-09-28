package eu.stratosphere.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OverloadedContructor<DeclaringClass> extends
		OverloadedInvokable<Constructor<DeclaringClass>, DeclaringClass, DeclaringClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3686535870776881782L;

	private Class<?> declaringClass;

	public OverloadedContructor() {
		super("<init>");
	}

	@Override
	public void addSignature(Constructor<DeclaringClass> member) {
		super.addSignature(member);
		if (this.declaringClass == null)
			this.declaringClass = member.getDeclaringClass();
		else if (member.getDeclaringClass() != this.declaringClass)
			this.declaringClass = member.getDeclaringClass().isAssignableFrom(this.declaringClass) ? this.declaringClass
				: member.getDeclaringClass();
	}

	@Override
	protected boolean isVarargs(Constructor<DeclaringClass> member) {
		return member.isVarArgs();
	}

	@Override
	protected Class<?>[] getParameterTypes(Constructor<DeclaringClass> member) {
		return member.getParameterTypes();
	}

	@Override
	protected DeclaringClass invokeDirectly(Constructor<DeclaringClass> member, Object context, Object[] params)
			throws IllegalAccessException, InvocationTargetException, IllegalArgumentException, InstantiationException {
		return member.newInstance(params);
	}

	public DeclaringClass invoke(Object... params) {
		return super.invoke(null, params);
	}

	@Override
	protected Constructor<DeclaringClass> findMember(Class<DeclaringClass> clazz, Class<?>[] parameterTypes)
			throws NoSuchMethodException {
		return clazz.getDeclaredConstructor(parameterTypes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<DeclaringClass> getReturnType() {
		return (Class<DeclaringClass>) this.declaringClass;
	}

	@SuppressWarnings("unchecked")
	public static <C> OverloadedContructor<C> valueOf(Class<C> clazz) {
		OverloadedContructor<C> ctor = new OverloadedContructor<C>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors())
			ctor.addSignature((Constructor<C>) constructor);
		return ctor;
	}
}
