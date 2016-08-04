package com.niit.fashionFront.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.niit.fashhionback.dao.CartDAO;
import com.niit.fashhionback.dao.CategoryDAO;
import com.niit.fashhionback.dao.UserDAO;
import com.niit.fashhionback.model.Cart;
import com.niit.fashhionback.model.Category;
import com.niit.fashhionback.model.UserDetails;

@Controller
public class UserController {

	Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserDAO userDAO;

	@Autowired
	UserDetails userDetails;

	@Autowired
	private CartDAO cartDAO;
	
	@Autowired
	private Cart cart;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private Category category;
	
	
	/**
	 * if invalid credentials ->  Home page , login ,  error message
	 * if valid credentials  && he is admin ->  AdminHome page ,logout link
	 * if valid credentials && he is end user ->  Home page, cart, logout link
	 * @param userID
	 * @param password
	 * @return it will return data and page name where to return
	 */
	@RequestMapping("/login")
	public ModelAndView login(@RequestParam(value = "name") String userID,
			@RequestParam(value = "password") String password, HttpSession session) {
		log.debug("Starting of the method login");
		log.info("userID is {}  password is {}", userID, password);

	
		ModelAndView mv = new ModelAndView("home");
		boolean isValidUser = userDAO.isValidUser(userID, password);

		if (isValidUser == true) {
			userDetails = userDAO.get(userID);
			session.setAttribute("loggedInUser", userDetails.getName());
			if (userDetails.getAdmin() == 1) {
				mv.addObject("isAdmin", "true");

			} else {
				mv.addObject("isAdmin", "false");
				cart = cartDAO.get(userID);
				mv.addObject("cart", cart);
				List<Cart> cartList = cartDAO.list();
				mv.addObject("cartList", cartList);
				mv.addObject("cartSize", cartList.size());
			}

		} else {

			mv.addObject("invalidCredentials", "true");
			mv.addObject("errorMessage", "Invalid Credentials");
			

		}
		log.debug("Ending of the method login");
		return mv;
	}

	 
	@RequestMapping("/logout")
	public ModelAndView logout(HttpServletRequest request, HttpSession session) {
		ModelAndView mv = new ModelAndView("/home");
		session.invalidate();
		session = request.getSession(true);
		session.setAttribute("category", category);
		session.setAttribute("categoryList", categoryDAO.list());
	
		mv.addObject("logoutMessage", "You successfully logged out");
		mv.addObject("loggedOut", "true");
	
		return mv;
	 }

}
