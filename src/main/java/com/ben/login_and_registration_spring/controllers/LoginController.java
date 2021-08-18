package com.ben.login_and_registration_spring.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ben.login_and_registration_spring.models.User;
import com.ben.login_and_registration_spring.services.UserService;
import com.ben.login_and_registration_spring.validators.UserValidator;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserValidator validator;
        
    @GetMapping("/")
    public String mainPage() {
    	return "redirect:/home";
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, 
    						BindingResult result, 
    						HttpSession session) {
    	// if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
//    	VALIDATE
    	validator.validate(user, result);
    	if (result.hasErrors()) {
    		return "registrationPage.jsp";
    	}
    	User newUser = userService.registerUser(user);
//    	putting in session  ONLY the user id
    	session.setAttribute("user_id", newUser.getId());
    	return "redirect:/home";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, 
    						@RequestParam("password") String password, 
    						Model model, HttpSession session,
    						RedirectAttributes flash) {
        // if the user is authenticated, save their user id in session
        // else, add error messages and return the login page
//    	USERSERVICE HAS THE AUTHENTICATE USER!
    	if (userService.authenticateUser(email, password)) {
//    		USER IS AUTHENTIC!
//    		find this user
    		User thisUser = userService.findByEmail(email);
//    		1. store if in session!
    		session.setAttribute("user_id", thisUser.getId());
    		return "redirect:/home";
    	} else {
    		flash.addFlashAttribute("error", "login fail");//    		
    		return "redirect:/login";
    	}
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    	Long id = (Long) session.getAttribute("user_id");
    	if (id != null) {
    		User thisUser = userService.findUserById(id);
    		model.addAttribute("user", thisUser);
    		return "homePage.jsp";
    	}
    	return "redirect:/login";
    	
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
    	return "redirect:/login";
        // redirect to login page
    }
}