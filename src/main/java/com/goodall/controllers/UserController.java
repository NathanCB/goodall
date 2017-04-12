package com.goodall.controllers;

import com.goodall.entities.User;
import com.goodall.parsers.RootParser;
import com.goodall.serializers.RootSerializer;
import com.goodall.serializers.UserSerializer;
import com.goodall.services.UserRepository;
import com.goodall.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserRepository users;

    RootSerializer rootSerializer = new RootSerializer();
    UserSerializer userSerializer = new UserSerializer();

    @PostConstruct
    public void init() throws SQLException, PasswordStorage.CannotPerformOperationException {
        if (users.count() == 0) {
            User user = new User();
            user.setUsername("Nat");
            user.setPassword("But");
            users.save(user);
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public Map<String, Object> registerUser(@RequestBody RootParser<User> parser, HttpServletResponse response) throws IOException, PasswordStorage.CannotPerformOperationException {
        User user = parser.getData().getEntity();
        User regUser = new User();
        User dbuser = new User();
        try {
            User checkUsersExists = users.findFirstByUsername(user.getUsername());
            if (checkUsersExists != null || user == null) {
                response.sendError(400, "Please login or register a new account.");
            } else {
                String hashedPassword = user.createPasswordHash(user.getPassword());
                regUser = new User(user.getUsername(), user.getEmail(), hashedPassword);
                dbuser = users.save(regUser);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.sendError(400, "Invalid Login attempt. ");
        }

        return rootSerializer.serializeOne(
                "/users" + dbuser.getId(),
                dbuser,
                userSerializer
        );
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public Map<String, Object> loginUser(@RequestBody RootParser<User> parser, HttpServletResponse response) throws IOException {
        User inputUser = parser.getData().getEntity();

        try {
            User checkUserExists = users.findFirstByUsername(inputUser.getUsername());
            checkUserExists.verifyPassword(inputUser.getPassword());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.sendError(400, "Invalid Login attempt. ");
        }
        User dbuser = users.findFirstByUsername(inputUser.getUsername());

        return rootSerializer.serializeOne(
                "/login" + dbuser.getId(),
                dbuser,
                userSerializer
        );
    }
}