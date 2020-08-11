package com.socialLogin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SocialFacebookController {
	private String url_fb="http://localhost:8080/forwardLogin_fb";
	private String url_google="http://localhost:8080/forwardLogin_google";
	private String url_github="http://localhost:8080/forwardLogin_github";
	private FacebookConnectionFactory factory_fb = new FacebookConnectionFactory("358655368659205",
			"d4c3ed820c3431106ac030701e478c78");
	private GoogleConnectionFactory factory_google = new GoogleConnectionFactory("727444412418-qhsu0n9vdsdib67lv4tsmg47en9so09k.apps.googleusercontent.com",
			"tMEuVyzc8snims8Lni4ncr_m");
	private GitHubConnectionFactory factory_github = new GitHubConnectionFactory("8559d331243b35706094",
			"26b5045d4fe62133870d3ee410e7a89df718693e");
	@RequestMapping("/")
	public ModelAndView firstPage() {
		return new ModelAndView("welcome");
	}

	//facebook
	@GetMapping(value = "/useApplication_fb")
	public String producer_fb() {

		OAuth2Operations operations = factory_fb.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();

		params.setRedirectUri(url_fb);
		params.setScope("email,public_profile");

		String url = operations.buildAuthenticateUrl(params);
		return "redirect:" + url;

	}

	@RequestMapping(value = "/forwardLogin_fb")
	public ModelAndView producer_fb(@RequestParam("code") String authorizationCode) {
		System.out.println("authorizationCode "+authorizationCode);
		OAuth2Operations operations = factory_fb.getOAuthOperations();
		AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, url_fb,
				null);
		Connection<Facebook> connection = factory_fb.createConnection(accessToken);
		Facebook facebook = connection.getApi();
		String[] fields = { "id", "email", "first_name", "last_name" };
		User userProfile = facebook.fetchObject("me", User.class, fields);
		com.socialLogin.dto.User userDetails=new com.socialLogin.dto.User();
		userDetails.setName(userProfile.getFirstName());

		System.out.println("id facebook"+userProfile.getId());
		userDetails.setEmail(userProfile.getEmail());
		userDetails.setUserID(userProfile.getId());
		ModelAndView model = new ModelAndView("details");
		model.addObject("user", userDetails);
		return model;

	}

	//google
	@GetMapping(value = "/useApplication_google")
	public String producer_google() {

		OAuth2Operations operations = factory_google.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();

		params.setRedirectUri(url_google);
		params.setScope("email profile");

		String url = operations.buildAuthenticateUrl(params);
		System.out.println("The URL is" + url);
		return "redirect:" + url;

	}
	@RequestMapping(value = "/forwardLogin_google")
	public ModelAndView producer_google(@RequestParam("code") String authorizationCode) {
		System.out.println("authorizationCode "+authorizationCode);
		OAuth2Operations operations = factory_google.getOAuthOperations();
		AccessGrant accessToken = operations.exchangeForAccess(authorizationCode,url_google,
				null);
		Connection<Google> connection = factory_google.createConnection(accessToken);
		Google google = connection.getApi();
		com.socialLogin.dto.User userDetails=new com.socialLogin.dto.User();
		userDetails.setName(google.userOperations().getUserInfo().getFirstName());
		System.out.println("##############ID  google #######"+google.userOperations().getUserInfo().getId());
		userDetails.setUserID(google.userOperations().getUserInfo().getId());
		userDetails.setEmail(google.userOperations().getUserInfo().getEmail());
		ModelAndView model = new ModelAndView("details");
		model.addObject("user", userDetails);
		return model;

	}
	//git hub
		@GetMapping(value = "/useApplication_github")
		public String producer_github() {

			OAuth2Operations operations = factory_github.getOAuthOperations();
			OAuth2Parameters params = new OAuth2Parameters();

			params.setRedirectUri(url_github);
			params.setScope("read:user,user");

			String url = operations.buildAuthenticateUrl(params);
			return "redirect:" + url;

		}

		@RequestMapping(value = "/forwardLogin_github")
		public ModelAndView producer_github(@RequestParam("code") String authorizationCode) {
			OAuth2Operations operations = factory_github.getOAuthOperations();
			AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, url_github,
					null);
			Connection<GitHub> connection = factory_github.createConnection(accessToken);
			GitHub github = connection.getApi();
			com.socialLogin.dto.User userDetails=new com.socialLogin.dto.User();
			userDetails.setName(github.userOperations().getUserProfile().getLogin());
			System.out.println(github.userOperations().getUserProfile().getId()+"******id git *******");
			userDetails.setEmail(github.userOperations().getUserProfile().getEmail());
			userDetails.setUserID(String.valueOf(github.userOperations().getUserProfile().getId()));
			ModelAndView model = new ModelAndView("details");
			model.addObject("user", userDetails);
			return model;

		}
		@RequestMapping(value = "/logout")
		private ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        HttpSession session = request.getSession();
	        if(session.getAttribute("user") != null){
	            session.removeAttribute("user");
	            response.sendRedirect("welcome.jsp");
	            
	        }
	        session.invalidate();
	        ModelAndView model = new ModelAndView("welcome");
			return model;
}
		
}
