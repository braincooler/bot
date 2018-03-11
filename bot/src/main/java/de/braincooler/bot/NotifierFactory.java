package de.braincooler.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class NotifierFactory {
	
	@Bean
	@Scope("prototype")
	public  Notifier createNotifier(String text) {
		return new Notifier(text);
	}
}
