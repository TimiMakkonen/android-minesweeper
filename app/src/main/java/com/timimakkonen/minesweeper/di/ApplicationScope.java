package com.timimakkonen.minesweeper.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies a type that the injector only instantiates once within the application.
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface ApplicationScope {}
