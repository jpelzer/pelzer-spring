package com.pelzer.util.spring;

import java.util.Properties;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import com.pelzer.util.Logging;
import com.pelzer.util.PropertyManager;

/**
 * This class exists to allow Spring to initialize itself from the
 * PropertyManager instead of custom property files in each environment. By
 * convention, configuration values for this are stored in the property file
 * 'spring.loader.properties' and are all prefixed with 'spring.', but they can
 * technically be stored anywhere.
 * <p>
 * To enable this, put this in your beans definition file: <code>
 *   <bean id="propertyConfigurer" class="com.pelzer.util.spring.SpringPropertyLoader" lazy-init="false"></bean>
 * </code>
 */
public class SpringPropertyLoader extends PropertyPlaceholderConfigurer {
	private final Logging.Logger log = Logging.getLogger(this);

	@Override
	protected String resolvePlaceholder(final String placeholder,
			final Properties props, final int systemPropertiesMode) {
		final String value = PropertyManager.getProperty("spring", placeholder);
		log.debug("resolved '{}' to '{}'", placeholder, value);
		return value;
	}
}
