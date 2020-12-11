package com.example.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageBotEvent;
import com.slack.api.model.event.MessageEvent;

/*import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
*/
@Configuration
public class SlackApp {
	private static final Logger logger = LoggerFactory.getLogger(SlackApp.class);

	//@Autowired
	//private StanfordCoreNLP stanfordCoreNLP;
	
	static public String workorderId;
	
	@Bean
	public App initSlackApp() {
		App app = new App();
		app.event(AppMentionEvent.class, (payload, ctx) -> {
			logger.info("app mention event executed with text value {} and type of event {}",
					payload.getEvent().getText(), payload.getEvent().getType());
			logger.info("app mention event executed with channel name {}", payload.getEvent().getChannel());
			logger.info("app mention event executed with username {}", payload.getEvent().getUsername());
	
			try {

				// Use the response.getBody()
				AppMentionEvent event = payload.getEvent();
				logger.info("app mention event hello executed with event  {} :", event);
				logger.info("app mention event hello executed with bot token  {} :", ctx.getBotToken());
				logger.info("app mention event hello executed with channel {} :", event.getChannel());
				// Call the chat.postMessage method using the built-in WebClient
				
				if(isWorkOrderStatus(payload.getEvent().getText())) {
				    workorderId = getWorkOrderId(payload.getEvent().getText());
					String workOrderStatus = getWorkOrderStatus(workorderId);
					ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
							// The token you used to initialize your app is stored in the `context` object
							.token(ctx.getBotToken())
							// Payload message should be posted in the channel where original message was
							// heard
							.channel(event.getChannel()).text("The status of workorder "+workorderId +" is "+workOrderStatus));
				}
				if(isWorkOrderAssigned(payload.getEvent().getText())) {
					String workOrderAssigned =getWorkOrderAssigned(workorderId);
							ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("workorder assigned to "+workOrderAssigned));
				}
				if(isWorkOrderLength(payload.getEvent().getText())) {
					List<String> lengths =getWorkOrderLength(workorderId);
							ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("length of each cable in this workorder are FQN1 : "+lengths.get(0)+", FQN2 : "+lengths.get(1)+ ", FQN3 : "+lengths.get(2)+", FQN4 : "+lengths.get(3)));
				}
				if(isWorkOrderMilestone(payload.getEvent().getText())) {
					List<String> milestones =getWorkOrderMilestone(workorderId);
							ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("milestone of each cable in this workorder are FQN1 : "+milestones.get(0)+", FQN2 : "+milestones.get(1)+ ", FQN3 : "+milestones.get(2)+", FQN4 : "+milestones.get(3)));
				}
				
			} catch (IOException | SlackApiException e) {
				logger.error("hello error: {}", e.getMessage(), e);
			}
			return ctx.ack();
		});
		app.event(MessageBotEvent.class, (payload, ctx) -> {
			logger.info("message event executed with text value {} and type of event {}", payload.getEvent().getText(),
					payload.getEvent().getType());
			logger.info("message event executed with channel name {}", payload.getEvent().getChannel());
			logger.info("message event executed with username {}", payload.getEvent().getUsername());
			// logger.info("message event executed bot profile name
			// {}",payload.getEvent().getBotId());
			return ctx.ack();
		});
		app.command("/do-the-thing", (req, ctx) -> {
			logger.info("slash-command executed with text value {} and type of event {}", req.getPayload().getText(),
					req.getRequestType());
			logger.info("slash-command executed with channel name {}", req.getPayload().getChannelName());
			logger.info("slash-command executed with username {}", req.getPayload().getUserName());
			// logger.info("slash-command executed response url
			// {}",req.getPayload().getResponseUrl());
			return ctx.ack("OK, let's do it!");
		});

		app.message("hello", (req, ctx) -> {
			logger.info("message event hello executed with text value {} and type of event {}",
					req.getEvent().getText(), req.getEvent().getType());
			logger.info("message event hello executed with channel name {}", req.getEvent().getChannel());
			logger.info("message event hello executed with username {}", req.getEvent().getUser());
			try {
				MessageEvent event = req.getEvent();
				logger.info("message event hello executed with event  {} :", event);
				logger.info("message event hello executed with bot token  {} :", ctx.getBotToken());
				logger.info("message event hello executed with channel {} :", event.getChannel());
				// Call the chat.postMessage method using the built-in WebClient
				ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
						// The token you used to initialize your app is stored in the `context` object
						.token(ctx.getBotToken())
						// Payload message should be posted in the channel where original message was
						// heard
						.channel(event.getChannel()).text("world"));
				logger.info("hello result: {}", result);
			} catch (IOException | SlackApiException e) {
				logger.error("hello error: {}", e.getMessage(), e);
			}
			return ctx.ack();
		});

		return app;
	}

	public boolean isWorkOrderStatus(String text) {
		String regex = "FQN[0-9]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return true;
		}
		return false;
	}
	
	public boolean isWorkOrderAssigned(String text) {
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.equalsIgnoreCase("whom")
					|| coreLabel.equalsIgnoreCase("assigned")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWorkOrderLength(String text) {
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.equalsIgnoreCase("cable")
					|| coreLabel.equalsIgnoreCase("miles")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWorkOrderMilestone(String text) {
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.equalsIgnoreCase("milestone")
					|| coreLabel.equalsIgnoreCase("milestones")) {
				return true;
			}
		}
		return false;
	}
	
	public String getWorkOrderId(String text) {
		String regex = "FQN[0-9]+";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String workId = null;
		while (matcher.find()) {
			workId = text.substring(matcher.start(), matcher.end());
		}
		return workId;
	}
	
	public String getWorkOrderStatus(String workId) {

		RestTemplate restTemplate = new RestTemplate();
		String uri = "https://slack-event-api.herokuapp.com/api/v1/workorder/" + workId + "/status";
		
		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of Events
		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		String status = responseEntity.getBody();
		return status;
	}
	
	public String getWorkOrderAssigned(String workId) {

		RestTemplate restTemplate = new RestTemplate();

		String uri = "https://slack-event-api.herokuapp.com/api/v1/workorder/" + workId + "/assigned";

		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
				String.class);
		String assigned = responseEntity.getBody();
		return assigned;

	}
	
	public List<String> getWorkOrderLength(String workId) {

		RestTemplate restTemplate = new RestTemplate();

		String uri = "https://slack-event-api.herokuapp.com/api/v1/workorder/" + workId +"/length";

		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<List<String>> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<String>>() {});
		List<String> strings = responseEntity.getBody();
		return strings;

	}
	
	public List<String> getWorkOrderMilestone(String workId) {

		RestTemplate restTemplate = new RestTemplate();

		String uri = "https://slack-event-api.herokuapp.com/api/v1/workorder/" + workId +"/milestone";

		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<List<String>> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<List<String>>() {});
		List<String> strings = responseEntity.getBody();
		return strings;

	}
	
	/*public List<CoreLabel> getAllToken(String text) {

		CoreDocument coreDocument = new CoreDocument(text);
		stanfordCoreNLP.annotate(coreDocument);
		List<CoreLabel> coreLabels = coreDocument.tokens();
		return coreLabels;
	}*/
	/*
	 * @Bean public App initSlackApp() { App app = new App(); app.command("/hello",
	 * (req, ctx) -> { return ctx.ack("What's up?"); }); return app; }
	 */
}