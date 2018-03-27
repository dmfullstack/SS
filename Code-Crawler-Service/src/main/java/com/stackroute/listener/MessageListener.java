package com.stackroute.listener;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stackroute.domain.Result;
import com.stackroute.service.CodeService;


@Component
public class MessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String message;
	
	CodeService codeService;
	
	@Autowired
	public void setCodeService(CodeService codeService) {
		this.codeService = codeService;
	}

    public String getMessage() {
		return message;
	}
       
    
    public void receiveMessage(String message) {
    	this.message = message;
    	Gson gson = new Gson();
        Type type = new TypeToken<Result>() {}.getType();
        Result result = gson.fromJson(message, type);
        logger.info("Message received"+message.toString());
    	System.out.println("Message received"+message.toString());
    	codeService.getCodeSnippetCount(result);
		
    }
}
