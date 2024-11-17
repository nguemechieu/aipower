package com.sopotek.aipower.routes;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestParam String uname) {
        return userService.updateUser(id, uname);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @DeleteMapping("/clear-cache")
    public void clearCache() {
        userService.clearAllUserCache();
    }
}
