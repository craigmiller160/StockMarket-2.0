package io.craigmiller160.stockmarket;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.craigmiller160.mvp.listener.AbstractListenerController;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Performs the post-processing initialization for the GUI
 * beans in this program.
 * <p>
 * This post-processor only works if spring-context-controller.xml and spring-context-model.xml
 * are in the same Spring container. Otherwise, <tt>BeansExceptions</tt> will be thrown.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread-safe. The only state field, the 
 * <tt>ApplicationContext</tt>, is synchronized. In addition, the actual use of this
 * class should only be during the IOC container initialization, so it should only
 * be accessed by one thread anyway.
 * 
 * @author craig
 * @version 2.3
 */
@ThreadSafe
public class GuiPostProcessor implements BeanPostProcessor, ApplicationContextAware {

	/**
	 * The <tt>ApplicationContext</tt> object, used to access the Spring container
	 * during post-processing.
	 */
	@GuardedBy("this")
	private ApplicationContext context;
	
	@Override
	public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		AbstractListenerController controller = context.getBean("controller", AbstractListenerController.class);
		if(controller == null){
			throw new FatalBeanException("Controller class not available for post processing");
		}
		else{
			if(bean instanceof AbstractListenerView){
				controller.addView((AbstractListenerView) bean);
			}
		}
		
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
