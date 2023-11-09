package io.karma.bts.common.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for indicating that a class
 * or field should be registered by the {@link AutoRegistry}.
 *
 * @author KitsuneAlex
 * @since 23/06/2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Register {
    String value();
}
