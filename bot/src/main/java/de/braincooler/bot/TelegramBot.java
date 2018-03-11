package de.braincooler.bot;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;
	@Autowired
	private NotifierFactory notifierFactory;

	private Set<Long> chatIds = new HashSet<Long>();
	private List<LocalTime> alarmTime = new ArrayList<LocalTime>();

	static {
		ApiContextInitializer.init();
	}

	@PostConstruct
	public void init() {
		TelegramBotsApi botapi = new TelegramBotsApi();
		try {
			botapi.registerBot(this);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return "CaoticAna_bot";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdateReceived(Update update) {
		chatIds.add(update.getMessage().getChatId());
		if (isUpdateValid(update)) {
			alarmTime.clear();
			String messageText = update.getMessage().getText();
			for (int i = 0; i < messageText.length(); i++) {
				if (messageText.charAt(i) == ':') {
					StringBuilder sb = new StringBuilder();
					sb.append(messageText.substring(i - 2, i) + messageText.substring(i, i + 3));
					String timeString = sb.toString();
					try {
						LocalTime date = (LocalTime.parse(timeString)).minus(125, ChronoUnit.MINUTES);
						alarmTime.add(date);
					} catch (Exception e) {

					}
				}
			}
			if (!alarmTime.isEmpty()) {
				taskScheduler.setPoolSize(alarmTime.size());
				StringBuilder sb = new StringBuilder();
				sb.append("Расписание портов: ");
				for (LocalTime t : alarmTime) {
					Date date = new Date();
					date.setHours(t.getHour());
					date.setMinutes(t.getMinute());
					date.setSeconds(0);
					taskScheduler.schedule(notifierFactory.createNotifier("Порт 5 минут!"), date);
					sb.append("\n" + String.valueOf(t));
				}
				sendMsg(update.getMessage().getChatId(), sb.toString());
			} else 
				sendMsg(update.getMessage().getChatId(), "Время не найдено :(");
		}
	}

	public void sendMsg(Long chatId, String text) {
		SendMessage message = new SendMessage().setChatId(chatId).setText(text);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void notifyAll(String text) {
		chatIds.forEach(s -> sendMsg(s, text));
	}

	public boolean isUpdateValid(Update update) {
		if (!update.hasMessage())
			return false;
		if (!update.getMessage().hasText())
			return false;
		return true;
	}

	@Override
	public String getBotToken() {
		return "561051477:AAGD2iJLvCYyq6tUqdNSUz9zfidiHM6dqd8";
	}

	public Set<Long> getChatIds() {
		return chatIds;
	}

	public void setChatIds(Set<Long> chatIds) {
		this.chatIds = chatIds;
	}

	public List<LocalTime> getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(List<LocalTime> alarmTime) {
		this.alarmTime = alarmTime;
	}
}