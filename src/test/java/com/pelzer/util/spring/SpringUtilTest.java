package com.pelzer.util.spring;

import com.pelzer.util.spring.testbeans.TestBean;

import junit.framework.TestCase;

public class SpringUtilTest extends TestCase {
	public void testGetSpringUtil(){
		SpringUtil util = SpringUtil.getInstance();
		assertNotNull(util);
		TestBean testBean = util.getBean(TestBean.class);
		assertNotNull(testBean);
	}
}
