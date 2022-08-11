package com.example.client;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.concurrent.ExecutorService;


@SpringBootApplication
public class ClientApplication implements InitializingBean, DisposableBean {
    @Autowired
	ExecutorService fixedThreadPool;
	@Autowired
	ExecutorService singleThreadExecutor;

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void destroy(){
		System.out.println("destroy");
		if(!fixedThreadPool.isShutdown()) {
			fixedThreadPool.shutdown();
		}
		if(!singleThreadExecutor.isShutdown()) {
			singleThreadExecutor.shutdown();
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
