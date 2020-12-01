package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageBotEvent;

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
        	logger.info("app mention event executed bot profile name {}",payload.getEvent().getBotProfile().getName());
            return ctx.ack();
        });
        app.event(MessageBotEvent.class, (payload, ctx) -> {
        	logger.info("message event executed with text value {} and type of event {}",payload.getEvent().getText(),payload.getEvent().getType());
        	logger.info("message event executed with channel name {}",payload.getEvent().getChannel());
        	logger.info("message event executed with username {}",payload.getEvent().getUsername());
        	logger.info("message event executed bot profile name {}",payload.getEvent().getBotId());
            return ctx.ack();
        });
        app.command("/do-the-thing", (req, ctx) -> {
        	logger.info("slash-command executed with text value {} and type of event {}",req.getPayload().getText(),req.getRequestType());
        	logger.info("slash-command executed with channel name {}",req.getPayload().getChannelName());
        	logger.info("slash-command executed with username {}",req.getPayload().getUserName());
        	logger.info("slash-command executed response url {}",req.getPayload().getResponseUrl());
            return ctx.ack("OK, let's do it!");
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