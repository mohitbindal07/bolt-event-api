package com.example.demo;

import javax.servlet.annotation.WebServlet;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

@WebServlet("/slack/events")
public class SlackAppController extends SlackAppServlet {
    public SlackAppController(App app) {
        super(app);
    }
    
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> slackEventAPIAuth(String request) {
	 DocumentContext dc = JsonPath.parse(request);
	 System.out.println("The json payload containing challenge : '{}' '"+ dc.read("$.challenge"));
	 
	 
	 if(dc.read("$.type").equals("url_verification")) {
		 System.out.println("The json payload type is url_verification ? : '{}' '"+ dc.read("$.type"));
		 return new ResponseEntity<String>("challenge :"+dc.read("$.challenge"), HttpStatus.OK);
	 }else if(dc.read("$.type").equals("event_callback")) {
		 System.out.println("The json payload type is event_verification ? : '{}' '"+ dc.read("$.type"));
		 System.out.println("the text entered is : "+  dc.read("$.event.text"));
		 System.out.println("the user  is : "+  dc.read("$.event.user"));
		 return new ResponseEntity<String>("event callback", HttpStatus.OK);
	 }
	 return new ResponseEntity<String>("challenge :"+dc.read("$.challenge"), HttpStatus.OK);
 }
    
	/*
	 * public static void main(String[] args) {
	 * 
	 * String str = "abc # xyz # low"; String [] words = str.split("#");
	 * 
	 * for(String w: words) { System.out.println(w); } }
	 */
}
