package com.github.birulazena.UserService.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InstallOpenTelemetryAppended implements InitializingBean {

    private final OpenTelemetry openTelemetry;


    @Override
    public void afterPropertiesSet() throws Exception {
        OpenTelemetryAppender.install(this.openTelemetry);
    }
}
