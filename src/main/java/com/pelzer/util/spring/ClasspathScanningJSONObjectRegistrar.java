package com.pelzer.util.spring;

import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.pelzer.util.Logging;
import com.pelzer.util.Logging.Logger;
import com.pelzer.util.json.JSONObject;
import com.pelzer.util.json.JSONUtil;

public class ClasspathScanningJSONObjectRegistrar {
  private static Logger log = Logging.getLogger(ClasspathScanningJSONObjectRegistrar.class);

  /**
   * A call to this method with a path (ie "com.pelzer.util.foo") will scan the
   * given package and any children for {@link JSONObject}s, and call
   * {@link JSONUtil#register(JSONObject)} for each one. Redundant calls are
   * fine, since the registration process ignores redundant registrations.
   */
  public static void scanPath(String path) {
    log.debug("Scanning classpath for JSONObjects under path '{}'", path);
    final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(JSONObject.class));

    // scan in the passed-in path
    final Set<BeanDefinition> components = provider.findCandidateComponents(path);
    for (final BeanDefinition component : components)
      try {
        final Class<?> cls = Class.forName(component.getBeanClassName());
        final JSONObject message = (JSONObject) cls.newInstance();
        JSONUtil.register(message);
      } catch (final Exception ex) {
        log.error("Could not instantiate class: {}", ex, component.getBeanClassName());
      }
  }

}
