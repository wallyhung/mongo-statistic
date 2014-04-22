package com.jukuad.statistic.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
	
    public static void main(String[] args) 
    {
//        ctx = SpringApplication.run(Application.class, args);
//    	ApplicationContext  ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//		String[] ss = ctx.getBeanDefinitionNames();
//		for (String s : ss) {
//			System.out.println(s);
//		}
    	
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    	context.register(SpringMongoConfig.class);
    	context.refresh();
    }

}
