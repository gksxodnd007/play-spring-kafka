package org.codingsquid.core;

import org.codingsquid.core.config.CoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author taewoong.han
 * @since 2021.02.21
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
@Import(CoreConfig.class)
public @interface EnableCore {

}
