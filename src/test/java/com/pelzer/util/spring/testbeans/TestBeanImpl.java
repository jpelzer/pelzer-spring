package com.pelzer.util.spring.testbeans;

import org.springframework.stereotype.Service;


@Service(TestBean.BEAN_NAME)
public class TestBeanImpl implements TestBean {
private String foo = null;
	
	public void setFoo(String foo) {
	  this.foo=foo;
  }

	public String getFoo() {
	  return foo;
  }

}
