package com.company.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/hello")
public class HelloService {

	@RequestMapping(consumes = "multipart/form-data", value = "/hello", method = RequestMethod.GET)
	public void hello(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		response.getWriter().write("Hello, jetty server start ok.");
	}
	
}
