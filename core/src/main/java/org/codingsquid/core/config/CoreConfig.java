package org.codingsquid.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author taewoong.han
 * @since 2021.02.21
 */
@Configuration
@Import(JacksonConfig.class)
public class CoreConfig {
}
