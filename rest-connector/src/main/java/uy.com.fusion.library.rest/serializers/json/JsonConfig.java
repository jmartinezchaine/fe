package uy.com.fusion.library.rest.serializers.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class JsonConfig {

    public static enum JsonFormat {
        CAMEL_CASE, SNAKE_CASE;
    }

    // //////////////////////////////////////////////
    // FACTORY METHODS
    // //////////////////////////////////////////////

    public static ConfigBuilder createBuilder() {
        return new ConfigBuilder();
    }

    public static ConfigBuilder createBuilder(Config config) {
        return new ConfigBuilder(config);
    }


    public static JsonConfig createDefault() {
        return new ConfigBuilder().build();
    }

    public static JsonConfig createFrom(Config config) {
        return new ConfigBuilder(config).build();
    }

    // //////////////////////////////////////////////
    // ATTRIBUTES
    // //////////////////////////////////////////////

    private JsonFormat format;
    private DateTimeFormatter dateTimeFormatter;
    private DateTimeFormatter localDateFormatter;
    private DateFormat dateFormatter;

    private JsonConfig(JsonFormat format, DateTimeFormatter dateTimeFormatter, DateTimeFormatter localDateFormatter,
        DateFormat dateFormatter) {
        this.format = format;
        this.dateTimeFormatter = dateTimeFormatter;
        this.localDateFormatter = localDateFormatter;
        this.dateFormatter = dateFormatter;
    }

    public JsonFormat getFormat() {
        return this.format;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return this.dateTimeFormatter;
    }

    public DateTimeFormatter getLocalDateFormatter() {
        return this.localDateFormatter;
    }

    public DateFormat getDateFormatter() {
        return this.dateFormatter;
    }


    // //////////////////////////////////////////////
    // BUILDER
    // //////////////////////////////////////////////

    public static class ConfigBuilder {

        private JsonFormat format;
        private DateTimeFormatter dateTimeFormatter;
        private DateTimeFormatter localDateFormatter;
        private DateFormat dateFormatter;
        private TimeZone dateTimeZone;

        private ConfigBuilder() {
            this(
                ConfigFactory
                    .parseString("properties-format: SNAKE_CASE, datetime-formatter:default, localdate-formatter:default, date-formatter:default, date-timezone:GMT"));
        }

        private ConfigBuilder(Config jsonConfig) {

            this.format = JsonFormat.valueOf(jsonConfig.getString("properties-format"));

            String dateTimeFormatter = jsonConfig.getString("datetime-formatter");
            if ("default".equals(dateTimeFormatter)) {
                this.dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
            } else if ("iso".equals(dateTimeFormatter)) {
                this.dateTimeFormatter = ISODateTimeFormat.dateTime();
            } else if ("timestamp".equals(dateTimeFormatter)) {
                this.dateTimeFormatter = null;
            } else {
                this.dateTimeFormatter = DateTimeFormat.forPattern(dateTimeFormatter);
            }

            String localDateFormatter = jsonConfig.getString("localdate-formatter");
            if ("default".equals(localDateFormatter)) {
                this.localDateFormatter = ISODateTimeFormat.date();
            } else if ("timestamp".equals(localDateFormatter)) {
                this.localDateFormatter = null;
            } else {
                this.localDateFormatter = DateTimeFormat.forPattern(localDateFormatter);
            }


            String dateFormatterPattern = jsonConfig.getString("date-formatter");
            if ("default".equals(localDateFormatter)) {
                this.dateFormatter = new ISO8601DateFormat();
            } else if ("timestamp".equals(localDateFormatter)) {
                this.dateFormatter = null;
            } else {
                this.dateFormatter = new SimpleDateFormat(dateFormatterPattern);
            }

            String dateTimezoneId = jsonConfig.getString("date-timezone");
            this.dateTimeZone = TimeZone.getTimeZone(dateTimezoneId);
        }

        public ConfigBuilder withCamelCaseFormat() {
            this.format = JsonFormat.CAMEL_CASE;
            return this;
        }

        public ConfigBuilder withSnakeCaseFormat() {
            this.format = JsonFormat.SNAKE_CASE;
            return this;
        }

        public ConfigBuilder withDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
            return this;
        }

        public ConfigBuilder withDateTimeFormatter(String pattern) {
            this.dateTimeFormatter = DateTimeFormat.forPattern(pattern);
            return this;
        }

        public ConfigBuilder withISODateTimeFormatter() {
            this.dateTimeFormatter = ISODateTimeFormat.dateTime();
            return this;
        }

        public ConfigBuilder withDateTimeAsTimestamp() {
            this.dateTimeFormatter = null;
            return this;
        }

        public ConfigBuilder withLocalDateFormatter(DateTimeFormatter localDateFormatter) {
            this.localDateFormatter = localDateFormatter;
            return this;
        }

        public ConfigBuilder withLocalDateFormatter(String pattern) {
            this.localDateFormatter = DateTimeFormat.forPattern(pattern);
            return this;
        }

        public ConfigBuilder withLocalDateAsTimestamp() {
            this.localDateFormatter = null;
            return this;
        }

        public ConfigBuilder withDateTimeZone(TimeZone dateTimeZone) {
            this.dateTimeZone = dateTimeZone;
            return this;
        }

        public ConfigBuilder withDateFormatter(DateFormat dateFormatter) {
            this.dateFormatter = dateFormatter;
            return this;
        }

        public ConfigBuilder withDateFormatter(String dateFormatterPattern) {
            this.dateFormatter = new SimpleDateFormat(dateFormatterPattern);
            return this;
        }

        public ConfigBuilder withDateFormatterAsTimestamp() {
            this.dateFormatter = null;
            return this;
        }

        public ConfigBuilder withDateTimeZone(String dateTimezoneId) {
            this.dateTimeZone = TimeZone.getTimeZone(dateTimezoneId);
            return this;
        }

        public JsonConfig build() {
            if (this.dateFormatter != null) {
                this.dateFormatter.setTimeZone(this.dateTimeZone);
            }
            return new JsonConfig(this.format, this.dateTimeFormatter, this.localDateFormatter, this.dateFormatter);
        }
    }

}
