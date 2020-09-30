package cn.edu.sdu.qd.oj.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Configuration
public class JacksonConfig {
 
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> {
			builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
			builder.serializerByType(Long.class, ToStringSerializer.instance);
			builder.serializerByType(Date.class, DateToTimestampStrSerializer.instance);
		};
	}



	/**
	* @Description 日期转字符串时间戳序列化器
	**/
	public static class DateToTimestampStrSerializer extends JsonSerializer<Date> {
		public final static DateToTimestampStrSerializer instance = new DateToTimestampStrSerializer();

		@Override
		public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeString(Optional.ofNullable(date).map(Date::getTime).map(String::valueOf).orElse(null));
		}
	}
}