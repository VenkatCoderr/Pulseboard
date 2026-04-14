package com.uptimemonitor.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final Monitor monitor = new Monitor();

    @Getter
    @Setter
    public static class Jwt {
        @NotBlank
        private String secret;

        @Min(60000)
        private long expirationMs = 86400000;
    }

    @Getter
    @Setter
    public static class Cors {
        @NotBlank
        private String allowedOrigin = "http://localhost:5173";
    }

    @Getter
    @Setter
    public static class Monitor {
        @Min(1000)
        private int timeoutMs = 10000;
    }
}
