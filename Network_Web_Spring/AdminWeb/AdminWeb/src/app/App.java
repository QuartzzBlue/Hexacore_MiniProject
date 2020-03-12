package app;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.frame.Service;
import com.vo.UserVO;

public class App {

	public static void main(String[] args) {
		// Start Spring Container
		AbstractApplicationContext factory
		= new GenericXmlApplicationContext("C:\\spring\\day04\\web\\WEB-INF\\config\\myspring.xml");
		
		Service<String,UserVO> biz = 
			(Service)factory.getBean("uservice");
		UserVO user = 
		new UserVO("id34", "pwd99", "ȫ����");
		try {
			biz.register(user);
			System.out.println("Register OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		factory.close();
	}

}



