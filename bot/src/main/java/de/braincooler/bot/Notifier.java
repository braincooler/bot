package de.braincooler.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Notifier implements Runnable {

	@Autowired
	private TelegramBot telegramBot;
	
	private String message;
	
	public Notifier (String text) {
		this.message = text;
	}

	@Override
	public void run() {
		telegramBot.notifyAll(message);
	}

}
