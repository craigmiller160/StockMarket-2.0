package io.craigmiller160.stockmarket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.craigmiller160.mvp.core.AbstractPropertyModel;
import io.craigmiller160.mvp.core.PropertyChangeView;
import io.craigmiller160.stockmarket.controller.StockMarketController;

/**
 * Performs post-processing initialization actions on the controller
 * for this program. Primarily focused on adding all views/models to
 * the controller for it to manage at runtime.
 * 
 * @author craig
 * @version 2.1
 */
public class InitController implements BeanPostProcessor, ApplicationContextAware {

	/**
	 * The <tt>ApplicationContext</tt> object, used to access the Spring container
	 * during post-processing.
	 */
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		//If bean is controller, add models and views to it.
		if(bean instanceof StockMarketController){
			StockMarketController controller = (StockMarketController) bean;
			controller.addPropertyModel((AbstractPropertyModel) context.getBean("guiStateModel"));
			controller.addPropertyModel((AbstractPropertyModel) context.getBean("stockDisplayModel"));
			controller.addView((PropertyChangeView) context.getBean("frame"));
			controller.addView((PropertyChangeView) context.getBean("menuBar"));
			controller.addView((PropertyChangeView) context.getBean("toolBar"));
			controller.addView((PropertyChangeView) context.getBean("portfolioPanel"));
			controller.addView((PropertyChangeView) context.getBean("stockDisplayPanel"));
			controller.addView((PropertyChangeView) context.getBean("searchPanel"));
			controller.addView((PropertyChangeView) context.getBean("buySellPanel"));
			controller.addView((PropertyChangeView) context.getBean("stockDetailsPanel"));
			controller.addView((PropertyChangeView) context.getBean("stockHistoryPanel"));
		}
		
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
