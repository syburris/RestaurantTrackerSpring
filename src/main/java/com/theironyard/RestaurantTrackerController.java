package com.theironyard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zach on 6/21/16.
 */
@Controller
public class RestaurantTrackerController {

    @Autowired
    UserRepository users;

    @Autowired
    RestaurantRepository restaurants;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(HttpSession session, Model model, String search) {
        String username = (String) session.getAttribute("username");
        User user = users.findByName(username);

        List<Restaurant> rests;
        if (search != null) {
            rests = restaurants.searchLocation(search);
        }
        else {
            rests = restaurants.findByUser(user);
        }
        model.addAttribute("restaurants", rests);
        model.addAttribute("user", user);
        return "home";
    }


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String name, String password, HttpSession session) throws Exception {
        User user = users.findByName(name);
        if (user == null) {
            user = new User(name,PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (PasswordStorage.verifyPassword(password, user.password)) {
            throw new Exception("Wrong password!");
        }
        session.setAttribute("username", name);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(path = "/create-restaurant", method = RequestMethod.POST)
    public String create(HttpSession session, String name, String location, int rating, String comment) throws Exception {
        String username = (String) session.getAttribute("username");
        User user = users.findByName(username);
        if (user == null) {
            throw new Exception("Not logged in.");
        }
        Restaurant restaurant = new Restaurant(name, location, rating, comment, user);
        restaurants.save(restaurant);
        return "redirect:/";
    }

    @RequestMapping(path = "/delete-restaurant", method = RequestMethod.POST)
    public String delete(int id) {
        restaurants.delete(id);
        return "redirect:/";
    }
}
