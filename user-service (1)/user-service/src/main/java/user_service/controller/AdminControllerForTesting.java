
package user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/u1/admin")
public class AdminControllerForTesting {

    @GetMapping("/test")
    public String testAdminAccess() {
        return "Admin RBAC test successful!";
    }
}



