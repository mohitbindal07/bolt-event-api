package com.example.demo;

import javax.servlet.annotation.WebServlet;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

@WebServlet("/slack/events")
public class SlackAppController extends SlackAppServlet {
    public SlackAppController(App app) {
        super(app);
    }
    
	/*
	 * public static void main(String[] args) {
	 * 
	 * String str = "abc # xyz # low"; String [] words = str.split("#");
	 * 
	 * for(String w: words) { System.out.println(w); } }
	 */
}
