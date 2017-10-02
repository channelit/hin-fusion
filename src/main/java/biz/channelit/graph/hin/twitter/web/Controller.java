package biz.channelit.graph.hin.twitter.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

}
