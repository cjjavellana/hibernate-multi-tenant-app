package org.cjavellana.controllers.api.v1;

import org.cjavellana.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1")
public class DocumentController {

    private UserInfoService userInfoService;

    @Autowired
    public DocumentController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/documents")
    public ResponseEntity getDemo() {
        userInfoService.findAll();
        return ResponseEntity.ok("{\"status\": \"ok\"}");
    }

    @PutMapping("/document/{id}")
    public ResponseEntity putDemo(@PathVariable Integer id, String content) {
        return ResponseEntity.ok("{\"status\": \"" + content + "\"}");
    }
}
