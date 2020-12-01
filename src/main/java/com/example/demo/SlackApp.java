package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageBotEvent;

@Configuration
public class SlackApp {
	@Bean
    public App initSlackApp() {
        App app = new App();
        app.event(AppMentionEvent.class, (payload, ctx) -> {
            return ctx.ack();
        });
        app.event(MessageBotEvent.class, (payload, ctx) -> {
            return ctx.ack();
        });
        app.command("/do-the-thing", (req, ctx) -> {
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