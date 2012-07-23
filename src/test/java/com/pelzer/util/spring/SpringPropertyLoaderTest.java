package com.pelzer.util.spring;

import com.pelzer.util.spring.testbeans.TestBean;

import junit.framework.TestCase;

public class SpringPropertyLoaderTest extends TestCase {
	
	
	public void testPropertyLoading(){
		String value = SpringUtil.getInstance().getBean(TestBean.class).getFoo();
		assertEquals("I am the foo", value);
	}
}
