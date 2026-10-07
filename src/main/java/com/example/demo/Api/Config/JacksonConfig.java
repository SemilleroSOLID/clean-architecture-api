package com.example.demo.Api.Config;

import com.example.demo.Domain.Enums.EnumConvocationType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    // The front sends the id of the ConvocationType table (1, 2, 3); Jackson would read it as the enum ordinal.
    @Bean
    public Module convocationTypeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(EnumConvocationType.class, new JsonDeserializer<>() {
            @Override
            public EnumConvocationType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String value = parser.getValueAsString();
                if (value == null) {
                    return (EnumConvocationType) context.handleUnexpectedToken(EnumConvocationType.class, parser);
                }
                try {
                    if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT || value.matches("\\d+")) {
                        return EnumConvocationType.fromValue(Integer.parseInt(value));
                    }
                    return EnumConvocationType.valueOf(value);
                } catch (IllegalArgumentException exception) {
                    return (EnumConvocationType) context.handleWeirdStringValue(EnumConvocationType.class, value,
                            "Tipo de convocatoria no válido");
                }
            }
        });
        return module;
    }
}
