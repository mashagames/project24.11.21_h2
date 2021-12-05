package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Map.*;


@RestController
public class ApiController {
    HashMap<String, String> userPwd = new HashMap<>();
    HashMap<String, Integer> userAge = new HashMap<>();
    // 1.   Создать пользователя
    /* curl -s -X POST http://localhost:8080/adduser -d 'username=user&password=pwd123&repeatPassword=pwd123&age=25' */
    @PostMapping("adduser")
    public void addUser(
            @RequestParam("username") String username, @RequestParam("password") String password,
            @RequestParam("repeatPassword") String repeatPassword, @RequestParam("age") Integer age) {
        if (userPwd.get(username) != null) throw new UserExistsException();
        if (!Pattern.matches("[a-zA-Z]+", username)) throw new WrongFormatException();
        if (password.equals(repeatPassword)) {
            userPwd.put(username, password);
            userAge.put(username, age);
        } else {
            throw new WrongFormatException();
        }
    }

    // 2. Возвращает пользователя по username. Если пользователя нет, то вернуть 404 ошибку.
    /*  curl -s -X GET http://localhost:8080/users/username  */
    @GetMapping("users/{username}")
    public String getUser(@PathVariable("username") String username) {
        if (userPwd.get(username) == null) throw new NotFoundException();
        return username +"; " + userPwd.get(username) + "; " + userAge.get(username);
    }

    // 3. Удаляет пользователя по username. Если пользователя нет, то вернуть 404 ошибку.
    /*  curl -s -X DELETE http://localhost:8080/users/username  */
    @DeleteMapping("users/{username}")
    public void delUser(@PathVariable("username") String username) {
        if (username.startsWith("admin")) throw new ForbiddenException();
        if (userPwd.get(username) == null) throw new NotFoundException();
        userPwd.remove(username);
        userAge.remove(username);
    }

    // 4.      Обновляет пароль пользователя с таким username.
    /* curl -s -X PUT http://localhost:8080/users/username  -d 'password=newpwd&repeatPassword=newpwd' */
    @PutMapping("users/{username}")
    public void updateTopic(@PathVariable("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam("repeatPassword") String repeatPassword) {
        if (!Pattern.matches("[a-zA-Z]+", username)) throw new WrongFormatException();
        if (userPwd.get(username) == null) throw new NotFoundException();
        if (password!=repeatPassword) throw new WrongFormatException();
        if (!username.startsWith("update")) throw new ForbiddenException();
        userPwd.replace(username, password);
    }
    // 5.      Выдать список пользователей
    /*  curl -s -X GET http://localhost:8080/userinage/25 */
    @GetMapping("userinage/{age}")
    public List<String> getUsersInAge(@PathVariable("age") Integer age) {
        List<String> users = new ArrayList<>();
        for (Entry entry : userAge.entrySet()) {
            if ((userAge.get(entry.getKey()) >= age - 5) && (userAge.get(entry.getKey()) <= age + 5))
                users.add(entry.getKey() + " " + entry.getValue());
        }
        return users;
    }

    // 6.      Выдать список отсортированный по возрасту список пользователей
    /*  curl -s -X GET http://localhost:8080/usersSorted?direction=up */
    @GetMapping("usersSorted")
    @ResponseBody
    public List<String> getUsersInAge(@RequestParam("direction") String direction) {
        HashMap<String, Integer> sortedMap;
        List<String> users = new ArrayList<>();
        if (direction.equals("up"))
            sortedMap = userAge.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
        else
            sortedMap = userAge.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
        sortedMap.forEach((key, value) -> users.add(key + " " + value));
        return users;
    }
}
