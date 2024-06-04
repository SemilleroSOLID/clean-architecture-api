package com.example.demo.Api.Controllers;

import com.example.demo.Application.IRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("request")
public class RequestController {

    @Autowired
    private IRequestService requestService;
}
