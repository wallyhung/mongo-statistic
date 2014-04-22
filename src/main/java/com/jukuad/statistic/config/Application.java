package com.jukuad.statistic.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@ComponentScan
public class Application {
	
	public static ApplicationContext ctx;
    public static void main(String[] args) 
    {
//        ctx = SpringApplication.run(Application.class, args);
    	ApplicationContext  ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		String[] ss = ctx.getBeanDefinitionNames();
		for (String s : ss) {
			System.out.println(s);
		}
    }

}
