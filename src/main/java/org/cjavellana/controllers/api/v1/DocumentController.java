package org.cjavellana.controllers.api.v1;

import org.cjavellana.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v1")
public class DocumentController {

    @Autowired
    private UserInfoService userInfoService;

    @ResponseBody
    @RequestMapping(value = "/documents")
    public HttpEntity<String> create() {
        userInfoService.findAll();
        return new HttpEntity<>("result: success");
    }

}
