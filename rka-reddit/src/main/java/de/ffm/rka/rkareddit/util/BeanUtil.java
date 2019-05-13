package de.ffm.rka.rkareddit.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.config.AppConfig;

@Service
public class BeanUtil implements ApplicationContextAware{

	private static ApplicationContext appContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;	
	}

	public static  <T> T getBeanFromContext(Class <T> bean) {
		return  appContext.getBean(bean);
	}
	
}
