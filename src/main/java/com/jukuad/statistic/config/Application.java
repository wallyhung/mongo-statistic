package com.jukuad.statistic.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jukuad.statistic.dao.RequestTempDAO;
import com.jukuad.statistic.service.RequestTempService;
import com.jukuad.statistic.util.Constant;

public class Application {
	
    private static ApplicationContext ctx;
	public static void main(String[] args) 
    {
    	ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		String[] ss = ctx.getBeanDefinitionNames();
		for (String s : ss) {
			System.out.println(s);
		}
		RequestTempDAO dao = (RequestTempDAO) ctx.getBean("requestTempDAOImpl");
//		for (int i = 0; i < 100000; i++) {
//			RequestTemp res = new RequestTemp();
//			res.setFid(i + "");
//			dao.save(res);
//		}
		
		RequestTempService service = (RequestTempService) ctx.getBean("requestTempServiceImpl"); 
//		RequestTemp temp = service.findNewest("-time");
//		System.out.println(temp.getFid());
		
		
//		service.copyCollection("request");
		
		service.parseAndSaveAndCopy("2014-04-14-15", Constant.PATH_REQUEST, "d:/bin/logs/");
		
		
    	
//    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//    	context.register(SpringMongoConfig.class);
//    	context.refresh();
    }

}
