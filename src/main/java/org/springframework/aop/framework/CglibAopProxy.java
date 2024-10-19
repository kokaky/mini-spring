package org.springframework.aop.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.aop.AdvisedSupport;

import java.lang.reflect.Method;

/**
 * cgli动态代理
 *
 * @author derekyi
 * @date 2020/12/6
 */
public class CglibAopProxy implements AopProxy {

	private final AdvisedSupport advised;

	public CglibAopProxy(AdvisedSupport advised) {
		this.advised = advised;
	}


	@Override
	public Object getProxy() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());
		enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
		enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
		return enhancer.create();
	}

	/**
	 * 注意此处的MethodInterceptor是cglib中的接口，advised中的MethodInterceptor的AOP联盟中定义的接口，因此定义此类做适配
	 */
	private static class DynamicAdvisedInterceptor implements MethodInterceptor {

		private final AdvisedSupport advised;

		private DynamicAdvisedInterceptor(AdvisedSupport advised) {
			this.advised = advised;
		}

		@Override
		/**
		 * object:被代理类
		 * 被代理类的方法
		 * 参数
		 * methodProxy
		 */
		public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
			CglibMethodInvocation methodInvocation = new CglibMethodInvocation(advised.getTargetSource().getTarget(), method, objects, methodProxy);
			if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
				//代理方法
				return advised.getMethodInterceptor().invoke(methodInvocation);
			}
			return methodInvocation.proceed();
		}
	}

	/**
	 * CglibMethodInvocation扩展ReflectiveMethodInvocation 类 来支持CGLIB代理
	 * 主要是覆盖了 invokeJoinpoint（）方法，如果有MethodProxy对象
	 * 则通过调用MethodProxy#invoke方法，否则通过反射调用
	 */
	private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

		private final MethodProxy methodProxy;

		public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
			super(target, method, arguments);
			this.methodProxy = methodProxy;
		}

		@Override
		public Object proceed() throws Throwable {
			return this.methodProxy.invoke(this.target, this.arguments);
		}
	}
}
