package com.pelzer.util.spring;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.pelzer.util.OverridableFields;
import com.pelzer.util.PropertyManager;

/**
 * Utility class for interfacing with Spring. By default it looks for spring
 * files named either 'beans.xml' or 'applicationContext.xml', but that can be
 * overridden via the {@link PropertyManager}, setting fields like so: <code>
 * com.pelzer.util.spring.SpringUtil$SpringUtilConstants.CONTEXT_DEFINITION.0=beans.xml
 * com.pelzer.util.spring.SpringUtil$SpringUtilConstants.CONTEXT_DEFINITION.1=foo.xml
 * com.pelzer.util.spring.SpringUtil$SpringUtilConstants.CONTEXT_DEFINITION.2=bar.xml
 * </code>
 */
@Service(SpringUtil.BEAN_NAME)
public final class SpringUtil implements BeanFactoryAware, ApplicationContextAware {
  public static final String BEAN_NAME = "com.pelzer.util.spring.SpringUtil";

  public static class SpringUtilConstants extends OverridableFields {
    public static String[] CONTEXT_DEFINITION = new String[] { "classpath*:beans.xml", "classpath*:applicationContext.xml" };
    static {
      new SpringUtilConstants().init();
    }
  }

  static boolean hasGetSpringBeanBeenCalled = false;

  /**
   * Takes the given beanClass (which is probably an interface) and requests
   * that Spring instantiate a bean for that class. The underlying mechanism is
   * specific to us, in that we expect the beanClass to have a static String
   * 'BEAN_NAME', which we will then pass to Spring to get our bean.
   * <p>
   * <b>WARNING</b> YOU MAY ONLY CALL THIS METHOD ONCE!<br>
   * The reason for this is that you should only be entering Spring from one
   * direction, so if you call this twice, you're not following that advice.
   * Instead, you should call SpringUtil.{@link #getInstance()}.
   * {@link #getBean(Class)}
   * 
   * @throws org.springframework.beans.BeansException
   *           If there is a problem loading the bean, if it does not have a
   *           'BEAN_NAME' property, or if you call this method a second time.
   */
  public static <T extends Object> T getSpringBean(final Class<T> beanClass) {
    return getSpringBean(SpringUtilConstants.CONTEXT_DEFINITION, beanClass);
  }

  public static <T extends Object> T getSpringBean(final String[] beanContextDefinitions, final Class<T> beanClass) {
    if (hasGetSpringBeanBeenCalled)
      throw new BeanCreationException("SpringUtil.getSpringBean(...) can only be called once! See the javadocs, buster!");
    hasGetSpringBeanBeenCalled = true;

    final SpringUtil util = new SpringUtil();
    util.setApplicationContext(new ClassPathXmlApplicationContext(beanContextDefinitions));
    return util.getBean(beanClass);
  }

  private BeanFactory        beanFactory        = null;
  private ApplicationContext applicationContext = null;

  /**
   * Part of the {@link BeanFactoryAware} interface, allows Spring to inject the
   * factory that created this bean for later use in {@link #getBean(Class)}.
   */
  public void setBeanFactory(final BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  /**
   * Takes the given beanClass (which is probably an interface) and requests
   * that Spring instantiate a bean for that class. The underlying mechanism is
   * specific to us, in that we expect the beanClass to have a static String
   * 'BEAN_NAME', which we will then pass to Spring to get our bean.
   * <p>
   * Ideally, you should RARELY need this method, as you'd have all your needed
   * beans injected into your class automatically by Spring. If you choose to
   * use this method, please check with Jason to make sure there isn't a better
   * way!
   * 
   * @throws org.springframework.beans.BeansException
   *           If there is a problem loading the bean or it does not have a
   *           'BEAN_NAME' property
   */
  public <T extends Object> T getBean(final Class<T> beanClass) {
    if (beanFactory == null)
      throw new BeanCreationException("SpringUtil.getBean(...) called on a non-managed SpringUtil instance.");

    // Figure out what's stored in 'BEAN_NAME'
    String beanName = "";
    try {
      final Field field = beanClass.getField("BEAN_NAME");
      beanName = String.valueOf(field.get(null));
    }
    catch (final SecurityException ex) {
      throw new BeanCreationException("Security exception accessing 'BEAN_NAME' for " + beanClass.getName(), ex);
    }
    catch (final NoSuchFieldException ex) {
      throw new BeanCreationException("NoSuchFieldException accessing 'BEAN_NAME' for " + beanClass.getName(), ex);
    }
    catch (final IllegalArgumentException ex) {
      throw new BeanCreationException("IllegalArgumentException accessing 'BEAN_NAME' for " + beanClass.getName(), ex);
    }
    catch (final IllegalAccessException ex) {
      throw new BeanCreationException("IllegalAccessException accessing 'BEAN_NAME' for " + beanClass.getName(), ex);
    }

    return beanFactory.getBean(beanName, beanClass);
  }

  public static SpringUtil singletonInstance;

  /**
   * Convenience method, the same as calling
   * <code>getSpringBean(SpringUtil.class);</code> except that it can be called
   * more than once (it caches the result).
   */
  public static synchronized SpringUtil getInstance() {
    if (singletonInstance == null)
      singletonInstance = getSpringBean(SpringUtil.class);
    return singletonInstance;
  }

  /**
   * @return the beanFactory
   */
  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    setBeanFactory(applicationContext);
  }

  /**
   * @return the applicationContext
   */
  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

}
