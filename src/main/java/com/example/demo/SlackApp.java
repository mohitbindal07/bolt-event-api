package com.example.demo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.slack.api.model.event.MessageEvent;


@Configuration
public class SlackApp {
	private static final Logger logger = LoggerFactory.getLogger(SlackApp.class);
	
	static public String workorderId;
	static public String workorderId2;
	static Map<String,String> map = new HashMap<>();
	static Map<String,String> map2 = new HashMap<>();
	static boolean isCompleted = true;
	static boolean isCompleted2 = true;
	static List<JiraTicket> jiraTickets = new  ArrayList<JiraTicket>();
		
	@Bean
	public App initSlackApp() {
		App app = new App();
		app.event(AppMentionEvent.class, (payload, ctx) -> {
			logger.info("app mention event executed with text value {} and type of event {}",
					payload.getEvent().getText(), payload.getEvent().getType());
			try {

				// Use the response.getBody()
				AppMentionEvent event = payload.getEvent();
				// Call the chat.postMessage method using the built-in WebClient
				String text = payload.getEvent().getText();
				if(text.contains("help")) {
					//return getHelp(event,ctx);
		    		ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
							// The token you used to initialize your app is stored in the `context` object
							.token(ctx.getBotToken())
							// Payload message should be posted in the channel where original message was
							// heard
							.channel(event.getChannel()).text("How can I help you today,\n"
				    				+ "1. Issue/Ticket tracking\n"
				    				+ "2. Work order status"));
		    		map.clear();
		    		isCompleted =true;
		    		return ctx.ack();
		    	}
				if(!(text.isEmpty() || text==null) && text.toLowerCase().contains("ticket")) {
		    		map.put("command", "ticket");
		    	}
		    	else if(!(text.isEmpty() || text==null) && (text.toLowerCase().contains("work order")|| text.toLowerCase().contains("workorder"))) {
		    		map.put("command", "work order");
		    	}
				if(text.contains("jira id")) {
					String jiraId = getJiraId(text);
					for(JiraTicket jira: jiraTickets ) {
						if(jira.getJiraId().equalsIgnoreCase(jiraId)) {
							ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("The jira ticket #" +jira.getJiraId()+" the title is "+"'"+jira.getTitle()+"'"+ " and severity is "+"'"+jira.getSeverity()+"'"));
						}
					}
					
				}
				if("ticket".equals(map.get("command"))) {
					if(isCompleted) {
						String res = "Please provide me the module name, title and severity of the Issue with separator(#)?";
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel()).text(res));
						 isCompleted = false;
						//return ctx.ack();
					}
					else {	
						
						logger.info("pipe operator   {} :", text);
						 String [] modTitleSev = text.split("#");
						 
						 JiraTicket jiraTicket = new JiraTicket();
						 
						 jiraTicket.setModule(modTitleSev[0].trim());
						 jiraTicket.setTitle(modTitleSev[1].trim());
						 jiraTicket.setSeverity(modTitleSev[2].trim());
						 int random_int = (int) (Math.random() * (999999 - 111111 + 1) + 111111);
						 String jiraId = "JT" + random_int;
						 jiraTicket.setJiraId(jiraId);
						 jiraTickets.add(jiraTicket);
						 
						// String jiraId = createIssueTracking(modTitleSev[1],modTitleSev[0],modTitleSev[1],modTitleSev[2]);
						 ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("Ticket created with jira id : "+jiraId));
						
						// return ctx.ack();
						 map.clear();
						 isCompleted = true;
					}
					
				}else if("work order".equals(map.get("command"))){
					if(isWorkOrderStatus(payload.getEvent().getText())) {
					    workorderId = getWorkOrderId(payload.getEvent().getText());
						String workOrderStatus = getWorkOrderStatus(workorderId);
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel()).text("The status of workorder "+workorderId +" is "+workOrderStatus));
						return ctx.ack();
					}
					if(isWorkOrderAssigned(payload.getEvent().getText())) {
						logger.info("inside work order assigned block");
						String workOrderAssigned =getWorkOrderAssigned(workorderId);
								ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
										// The token you used to initialize your app is stored in the `context` object
										.token(ctx.getBotToken())
										// Payload message should be posted in the channel where original message was
										// heard
										.channel(event.getChannel()).text("workorder assigned to "+workOrderAssigned));
								return ctx.ack();
					}
					if(isWorkOrderLength(payload.getEvent().getText())) {
						List<String> lengths =getWorkOrderLength(workorderId);
								ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
										// The token you used to initialize your app is stored in the `context` object
										.token(ctx.getBotToken())
										// Payload message should be posted in the channel where original message was
										// heard
										.channel(event.getChannel()).text("length of each cable in this workorder are FQN1 : "+lengths.get(0)+", FQN2 : "+lengths.get(1)+ ", FQN3 : "+lengths.get(2)+", FQN4 : "+lengths.get(3)));
								return ctx.ack();
					}
					if(isWorkOrderMilestone(payload.getEvent().getText())) {
						List<String> milestones =getWorkOrderMilestone(workorderId);
								ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
										// The token you used to initialize your app is stored in the `context` object
										.token(ctx.getBotToken())
										// Payload message should be posted in the channel where original message was
										// heard
										.channel(event.getChannel()).text("milestone of each cable in this workorder are FQN1 : "+milestones.get(0)+", FQN2 : "+milestones.get(1)+ ", FQN3 : "+milestones.get(2)+", FQN4 : "+milestones.get(3)));
								return ctx.ack();
					}
					map.clear();
				}
				
				
			} catch (IOException | SlackApiException e) {
				logger.error("hello error: {}", e.getMessage(), e);
			} 
			return ctx.ack();
		});
		String regex = "^[a-zA-Z0-9_.-|?#\\s]*$";
		Pattern pattern = Pattern.compile(regex);
		app.message(pattern, (req, ctx) -> {
			logger.info("message event pattern executed with text value {} and type of event {}",
					req.getEvent().getText(), req.getEvent().getType());
			
			try {
				MessageEvent event = req.getEvent();
				
				// Call the chat.postMessage method using the built-in WebClient
				String text = req.getEvent().getText();
				if(text.equals("help")){
					logger.info("message event pattern executed help typed");
					ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
							// The token you used to initialize your app is stored in the `context` object
							.token(ctx.getBotToken())
							// Payload message should be posted in the channel where original message was
							// heard
							.channel(event.getChannel()).text("How can I help you today,\n"
				    				+ "1. Issue/Ticket tracking\n"
				    				+ "2. Work order status"));
					map2.clear();
					isCompleted2 = true;
					//return ctx.ack();
				}
				if(!(text.isEmpty() || text==null) && text.toLowerCase().contains("ticket")) {
					logger.info("message event pattern executed ticket typed");
		    		map2.put("command", "ticket");
		    	}
		    	else if(!(text.isEmpty() || text==null) && (text.toLowerCase().contains("work order")|| text.toLowerCase().contains("workorder"))) {
		    		logger.info("message event pattern executed work order typed");
		    		map2.put("command", "work order");
		    	}
				if(text.contains("jira id")) {
					logger.info("message event pattern executed jira id typed");
					String jiraId = getJiraId(text);
					for(JiraTicket jira: jiraTickets ) {
						if(jira.getJiraId().equalsIgnoreCase(jiraId)) {
							ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("The jira ticket #" +jira.getJiraId()+" the title is "+"'"+jira.getTitle()+ "'"+" and severity is "+"'"+jira.getSeverity()+"'"));
						}
					}
					
				}
				if("ticket".equals(map2.get("command"))) {
					if(isCompleted2) {
						logger.info("message event pattern executed ticket create typed");
						String res = "Please provide me the module name, title and severity of the Issue with separator(#)?";
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel()).text(res));
						 isCompleted2 = false;
						//return ctx.ack();
					}
					else {	
						logger.info("message event pattern executed ticket jira id typed");
						logger.info("pipe operator   {} :", text);
						 String [] modTitleSev = text.split("#");
						 JiraTicket jiraTicket = new JiraTicket();
						 jiraTicket.setModule(modTitleSev[0].trim());
						 jiraTicket.setTitle(modTitleSev[1].trim());
						 jiraTicket.setSeverity(modTitleSev[2].trim());
						 int random_int = (int) (Math.random() * (999999 - 111111 + 1) + 111111);
						 String jiraId = "JT" + random_int;
						 jiraTicket.setJiraId(jiraId);
						 jiraTickets.add(jiraTicket);
						 
						// String jiraId = createIssueTracking(modTitleSev[1],modTitleSev[0],modTitleSev[1],modTitleSev[2]);
						 ctx.client().chatPostMessage(r -> r
									// The token you used to initialize your app is stored in the `context` object
									.token(ctx.getBotToken())
									// Payload message should be posted in the channel where original message was
									// heard
									.channel(event.getChannel()).text("Ticket created with jira id : "+jiraId));
						
						// return ctx.ack();
						 map2.clear();
					}
					
				} else if ("work order".equals(map2.get("command"))) {
					logger.info("message event pattern executed work order command");
					if (isWorkOrderStatus(text)) {
						logger.info("message event pattern executed work order command status");
						workorderId2 = getWorkOrderId(text);
						String workOrderStatus = getWorkOrderStatus(workorderId2);
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel())
								.text("The status of workorder " + workorderId2 + " is " + workOrderStatus));
						return ctx.ack();
					}
					if (isWorkOrderAssigned(text)) {
						logger.info("message event pattern executed work order command assigned");
						String workOrderAssigned = getWorkOrderAssigned(workorderId2);
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel()).text("workorder assigned to " + workOrderAssigned));
						return ctx.ack();
					}
					if (isWorkOrderLength(text)) {
						logger.info("message event pattern executed work order command length");
						List<String> lengths = getWorkOrderLength(workorderId2);
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel())
								.text("length of each cable in this workorder are FQN1 : " + lengths.get(0)
										+ ", FQN2 : " + lengths.get(1) + ", FQN3 : " + lengths.get(2) + ", FQN4 : "
										+ lengths.get(3)));
						return ctx.ack();
					}
					if (isWorkOrderMilestone(text)) {
						logger.info("message event pattern executed work order command milestone");
						List<String> milestones = getWorkOrderMilestone(workorderId2);
						ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
								// The token you used to initialize your app is stored in the `context` object
								.token(ctx.getBotToken())
								// Payload message should be posted in the channel where original message was
								// heard
								.channel(event.getChannel())
								.text("milestone of each cable in this workorder are FQN1 : " + milestones.get(0)
										+ ", FQN2 : " + milestones.get(1) + ", FQN3 : " + milestones.get(2)
										+ ", FQN4 : " + milestones.get(3)));
						return ctx.ack();
					}
					map2.clear();
				}
				
			} catch (IOException | SlackApiException e) {
				logger.error("hello error: {}", e.getMessage(), e);
			}
			return ctx.ack();
		});

		return app;
	}
	
	public boolean isWorkOrderStatus(String text) {
		logger.info("inside isWorkOrderStatus");
		String regex = "test_tampa[0-9]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return true;
		}
		return false;
	}
	
	public boolean isWorkOrderAssigned(String text) {
		logger.info("inside isWorkOrderAssigned");
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.trim().equalsIgnoreCase("whom")||coreLabel.trim().equalsIgnoreCase("who")||coreLabel.trim().equalsIgnoreCase("aligned")
					|| coreLabel.trim().equalsIgnoreCase("assigned")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWorkOrderLength(String text) {
		logger.info("inside isWorkOrderLength");
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.trim().equalsIgnoreCase("cable")||coreLabel.trim().equalsIgnoreCase("length")
					|| coreLabel.trim().equalsIgnoreCase("miles")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWorkOrderMilestone(String text) {
		logger.info("inside isWorkOrderMilestone");
		String [] strings  = text.split(" ");
		List<String> coreLabels= Arrays.asList(strings);
		//List<CoreLabel> coreLabels = getAllToken(text);
		for (String coreLabel : coreLabels) {
			if (coreLabel.equalsIgnoreCase("milestone")||coreLabel.equalsIgnoreCase("target")
					|| coreLabel.equalsIgnoreCase("milestones")) {
				return true;
			}
		}
		return false;
	}
	
	public String getWorkOrderId(String text) {
		logger.info("inside getWorkOrderId");
		String regex = "test_tampa[0-9]+";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String workId = null;
		while (matcher.find()) {
			workId = text.substring(matcher.start(), matcher.end());
		}
		return workId;
	}
	
	public String getJiraId(String text) {
		logger.info("inside getWorkOrderId");
		String regex = "JT[0-9]+";
		
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
	
	public static JiraTicket getIssueTrackingStatus(String jiraId) {
		
		RestTemplate restTemplate = new RestTemplate();

		String uri = "https://slack-event-api.herokuapp.com/api/v1/jiraticket/" + jiraId +"/status";

		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<JiraTicket> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
				JiraTicket.class);
		JiraTicket jiraTicket = responseEntity.getBody();
		return jiraTicket;
	}
	
	public static void main(String[] args) throws URISyntaxException {
		
		String jiraID = createIssueTracking("slackbot", "slackbot module", "slack bot description", "high");
		updateIssueTracking(jiraID, null, "updated module", "updated description", null);
		System.out.println("The jira status title : "+getIssueTrackingStatus(jiraID).getTitle());
		
	}
	public static String createIssueTracking(String title,String module,String description,String severity) throws URISyntaxException {
		
		RestTemplate restTemplate = new RestTemplate();
		JiraTicket jiraTicket = new JiraTicket();
		jiraTicket.setDescription(description);
		jiraTicket.setModule(module);
		jiraTicket.setSeverity(severity);
		jiraTicket.setTitle(title);
		
		String baseUrl = "https://slack-event-api.herokuapp.com/api/v1/jiraticket/";
	    URI uri = new URI(baseUrl);
		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(jiraTicket,requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, requestEntity, String.class);

		String jiraId = responseEntity.getBody();
		return jiraId;
	}
	
	public static String updateIssueTracking(String jiraId,String title,String module,String description,String severity) throws URISyntaxException {
		
		RestTemplate restTemplate = new RestTemplate();
		String baseUrl = "https://slack-event-api.herokuapp.com/api/v1/jiraticket/"+jiraId+"?title="+title+"&module="+module+"&description="+description+"&severity="+severity;
	   // URI uri = new URI(baseUrl);
		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

		// Add the Jackson message converter
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Make the HTTP GET request, marshaling the response from JSON to an array of
		// Events
		ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.PUT,requestEntity, String.class);

		String jira = responseEntity.getBody();
		return jira;
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