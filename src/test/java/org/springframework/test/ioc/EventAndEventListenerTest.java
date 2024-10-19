package org.springframework.test.ioc;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.common.event.CustomEvent;

/**
 * @author derekyi
 * @date 2020/12/5
 */
public class EventAndEventListenerTest {

	@Test
	public void testEventListener() throws Exception {
		// 容器刷新，会执行刷新的监听器
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:event-and-event-listener.xml");
		// 获取容器后，手动发布一个监听器事件
		applicationContext.publishEvent(new CustomEvent(applicationContext));

		// 使用钩子方法，让当前容器关闭的时候执行关闭的事件监听器
		applicationContext.registerShutdownHook();//或者applicationContext.close()主动关闭容器;
	}
}
