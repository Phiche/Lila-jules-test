package com.example.i18n;

import java.util.function.Function;

/**
 * Represents a function that can select an I18nModule.
 * The Scala equivalent was `I18nModule.type => I18nModule`.
 * In Java, this might often simplify to just using an I18nModule directly or a Supplier<I18nModule>.
 * This interface is a direct translation attempt for completeness.
 */
@FunctionalInterface
public interface I18nModuleSelector extends Function<Class<I18nModule>, I18nModule> {
    // The inherited method is: I18nModule apply(Class<I18nModule> i18nModuleClass);
}
