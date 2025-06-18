package br.com.g12.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUseCase<T> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void logInput(T input) {
        log.info("Executing use case with input: {}", input);
    }

    protected void logSuccess() {
        log.info("Use case executed successfully");
    }

    protected void logError(Exception e) {
        log.error("Error during use case execution: {}", e.getMessage(), e);
    }

}
