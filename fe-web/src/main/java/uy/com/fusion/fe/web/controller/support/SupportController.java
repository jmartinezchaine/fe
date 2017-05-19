package uy.com.fusion.fe.web.controller.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uy.com.fusion.fe.web.util.JsonUtils;

/**
 * Created by juanmartinez on 30/8/16.
 */
@RestController
public class SupportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportController.class);

    @Autowired
    JsonUtils jsonUtils;


    @RequestMapping(value = "/fe/health-check",
                    method = RequestMethod.GET)
    public String healthCheck() {
        return "Tuti funncionando...";
    }


}
