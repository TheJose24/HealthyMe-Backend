package dev.diegoqm.healthyme_citas.config;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "healthyme_citas";
    }

    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        // Convierte de String (en Mongo) a LocalTime (en Java)
        converters.add(new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return LocalTime.parse(source);
            }
        });

        // Convierte de LocalTime (en Java) a String (en Mongo)
        converters.add(new Converter<LocalTime, String>() {
            @Override
            public String convert(LocalTime source) {
                return source.toString();
            }
        });

        return new MongoCustomConversions(converters);
    }
}