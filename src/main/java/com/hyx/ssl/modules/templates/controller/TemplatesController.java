package com.hyx.ssl.modules.templates.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class TemplatesController {

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/certInfo/view")
    public String certInfo() {
        return "certInfo/list";
    }

    @GetMapping("/certDeploy/view")
    public String certDeploy() {
        return "certDeploy/list";
    }

    @GetMapping("/certCheck/view")
    public String certCheck() {
        return "certCheck/list";
    }

    @GetMapping("/authConfig/view")
    public String authConfig() {
        return "authConfig/list";
    }
}
