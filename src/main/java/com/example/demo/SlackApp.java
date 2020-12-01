package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageBotEvent;
import com.slack.api.model.event.MessageEvent;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SlackApp {
	private static final Logger logger =
			LoggerFactory.getLogger(SlackApp.class);
	@Bean
    public App initSlackApp() {
        App app = new App();
        app.event(AppMentionEvent.class, (payload, ctx) -> {
        	logger.info("app mention event executed with text value {} and type of event {}",payload.getEvent().getText(),payload.getEvent().getType());
        	logger.info("app mention event executed with channel name {}",payload.getEvent().getChannel());
        	logger.info("app mention event executed with username {}",payload.getEvent().getUsername());
        	//logger.info("app mention event executed bot profile name {}",payload.getEvent().getBotProfile().getName());
        	 try {
        		 AppMentionEvent event = payload.getEvent();
                 logger.info("app mention event hello executed with event  {} :",event);
                 logger.info("app mention event hello executed with bot token  {} :",ctx.getBotToken());
                 logger.info("app mention event hello executed with channel {} :",event.getChannel());
                 // Call the chat.postMessage method using the built-in WebClient
                 if("hello".equalsIgnoreCase(payload.getEvent().getText())) {
	                 ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
	                     // The token you used to initialize your app is stored in the `context` object
	                     .token(ctx.getBotToken())
	                     // Payload message should be posted in the channel where original message was heard
	                     .channel(event.getChannel())
	                     .text("world")
	                 );
                 }
                 if("how are you!".equalsIgnoreCase(payload.getEvent().getText())) {
	                 ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
	                     // The token you used to initialize your app is stored in the `context` object
	                     .token(ctx.getBotToken())
	                     // Payload message should be posted in the channel where original message was heard
	                     .channel(event.getChannel())
	                     .text("I am good here!")
	                 );
                 }
                 //logger.info("hello result: {}", result);
             } catch (IOException | SlackApiException e) {
                 logger.error("hello error: {}", e.getMessage(), e);
             }
             return ctx.ack();
        });
        app.event(MessageBotEvent.class, (payload, ctx) -> {
        	logger.info("message event executed with text value {} and type of event {}",payload.getEvent().getText(),payload.getEvent().getType());
        	logger.info("message event executed with channel name {}",payload.getEvent().getChannel());
        	logger.info("message event executed with username {}",payload.getEvent().getUsername());
        	//logger.info("message event executed bot profile name {}",payload.getEvent().getBotId());
            return ctx.ack();
        });
        app.command("/do-the-thing", (req, ctx) -> {
        	logger.info("slash-command executed with text value {} and type of event {}",req.getPayload().getText(),req.getRequestType());
        	logger.info("slash-command executed with channel name {}",req.getPayload().getChannelName());
        	logger.info("slash-command executed with username {}",req.getPayload().getUserName());
        	//logger.info("slash-command executed response url {}",req.getPayload().getResponseUrl());
            return ctx.ack("OK, let's do it!");
        });
        
        app.message("hello", (req, ctx) -> {
        	logger.info("message event hello executed with text value {} and type of event {}",req.getEvent().getText(),req.getEvent().getType());
        	logger.info("message event hello executed with channel name {}",req.getEvent().getChannel());
        	logger.info("message event hello executed with username {}",req.getEvent().getUser());
            try {
                MessageEvent event = req.getEvent();
                logger.info("message event hello executed with event  {} :",event);
                logger.info("message event hello executed with bot token  {} :",ctx.getBotToken());
                logger.info("message event hello executed with channel {} :",event.getChannel());
                // Call the chat.postMessage method using the built-in WebClient
                ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
                    // The token you used to initialize your app is stored in the `context` object
                    .token(ctx.getBotToken())
                    // Payload message should be posted in the channel where original message was heard
                    .channel(event.getChannel())
                    .text("world")
                );
                logger.info("hello result: {}", result);
            } catch (IOException | SlackApiException e) {
                logger.error("hello error: {}", e.getMessage(), e);
            }
            return ctx.ack();
        });
        
        return app;
    }
	
	/*@Bean
	  public App initSlackApp() {
	    App app = new App();
	    app.command("/hello", (req, ctx) -> {
	      return ctx.ack("What's up?");
	    });
	    return app;
	  }*/
}