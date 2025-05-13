package com.translator.translator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.translator.translator.model.user.User;
import com.translator.translator.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {
    
    @Autowired
    private final UserService UserService;

    public WebController(UserService UserService) {
        this.UserService = UserService;
    }

    @GetMapping("/login")
    public String home(HttpSession session) {
        return "login"; // Returns login.html template
    }

    @GetMapping("/")
    public String mainWebPage(HttpSession session) {
        
        String username = (String) session.getAttribute("username"); 
        
        if (username == null) { 
            System.out.println("redirecting to login");
            // Force login if no session
            return "redirect:/login"; 
        }
        System.out.println("redirecting the main page the user " + username);
        return "index";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session) {
        String username = (String) session.getAttribute("username"); 
        session.setAttribute("state", null);
        System.out.println("deleting session for user " + username);
        session.setAttribute("username", null);
        return "redirect:/login"; //redirect to login always ?
    }

    //creating users and logging in
    @PostMapping("/user-action")        
    public String handleUserAction(
            @RequestParam String username,
            @RequestParam String action,
            HttpSession session) {
        
        try {
            if ("create".equals(action)) {
                System.out.println("Creating user: " + username);
                try {
                    User newUser = UserService.createUser(new User(username));
                    session.setAttribute("username", username);
                    session.setAttribute("userId", newUser.getId());
                    
                } catch (Exception e) {
                    System.out.println("Duplicate key error: " + e.getMessage());
                    session.setAttribute("state", "userExistsError");
                    session.setAttribute("username", null);
                    session.setAttribute("userId", null);
                }
                
                return "redirect:/";
            } 
            else if ("login".equals(action)) {
                System.out.println("Logging in user: " + username);
                if (UserService.existsByName(username)){

                    User user = UserService.getByName(username).get();
                    session.setAttribute("username", username);
                    session.setAttribute("userId", user.getId());
                    

                }
                else{
                    session.setAttribute("state", "noUserFound");
                    session.setAttribute("username", null);
                    session.setAttribute("userId", null);
                }
                return "redirect:/";
            }
        } catch (Exception e) {
            session.setAttribute("state", "error");  
            session.setAttribute("username", null);
            session.setAttribute("userId", null);
            return "redirect:/"; 
        }
        
        return "redirect:/";
    }

}