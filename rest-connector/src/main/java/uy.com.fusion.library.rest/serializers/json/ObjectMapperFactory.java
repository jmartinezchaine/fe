package uy.com.fusion.library.rest.serializers.json;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperFactory {

    private static SimpleModule getModule(JsonConfig jsonConfig) {
        // Register custom serializers
        SimpleModule module = new SimpleModule("DefaultModule", new Version(0, 0, 1, null));

        // Joda DateTime
        module.addDeserializer(DateTime.class, new JodaDateTimeJsonDeserializer(jsonConfig.getDateTimeFormatter()));
        module.addSerializer(DateTime.class, new JodaDateTimeJsonSerializer(jsonConfig.getDateTimeFormatter()));

        // Joda LocalDate
        module.addDeserializer(LocalDate.class, new JodaLocalDateJsonDeserializer(jsonConfig.getLocalDateFormatter()));
        module.addSerializer(LocalDate.class, new JodaLocalDateJsonSerializer(jsonConfig.getLocalDateFormatter()));

        return module;
    }

    private static ObjectMapper internalGetObjectMapper(JsonConfig jsonConfig) {
        ObjectMapper instance = new ObjectMapper();

        if (jsonConfig.getDateFormatter() != null) {
            instance.setDateFormat(new SupportedDatesDateFormat(jsonConfig.getDateFormatter()));
        }

        instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        instance.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        if (JsonConfig.JsonFormat.SNAKE_CASE.equals(jsonConfig.getFormat())) {
            instance.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }

        return instance;
    }

    public static ObjectMapper getObjectMapper(JsonConfig jsonConfig) {
        ObjectMapper instance = internalGetObjectMapper(jsonConfig);

        instance.registerModule(getModule(jsonConfig));

        return instance;
    }

    public static ObjectMapper getNonJodaObjectMapper(JsonConfig jsonConfig) {
        ObjectMapper instance = internalGetObjectMapper(jsonConfig);

        instance.registerModule(new JavaTimeModule());

        return instance;
    }
}
