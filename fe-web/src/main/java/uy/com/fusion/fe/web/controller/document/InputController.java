package uy.com.fusion.fe.web.controller.document;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import springfox.documentation.annotations.ApiIgnore;
import uy.com.fusion.fe.web.api.document.DocumentEx;
import uy.com.fusion.fe.web.api.document.commons.ResponseListEx;
import uy.com.fusion.fe.web.controller.util.ControllerUtils;
import uy.com.fusion.fe.web.service.InputService;
import uy.com.fusion.fe.web.util.JsonUtils;

/**
 * Created by didier on 24/11/16.
 */
@RestController
public class InputController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputController.class);

    @Autowired
    JsonUtils jsonUtils;

    @Autowired
    InputService inputService;

    @RequestMapping(value = "/fe/documents",
                    method = RequestMethod.POST)
    public String add(@RequestBody(required = true) DocumentEx input) {
        try {
            LOGGER.info(this.jsonUtils.toJson(input));
            return this.inputService.addDocument(input);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RestClientException("" + HttpStatus.BAD_REQUEST_400, e);
        }
    }



    @RequestMapping(value = "/fe/documents/{id}",
                    method = RequestMethod.GET)
    @ResponseBody
    @ApiIgnore
    public DocumentEx docById(@PathVariable("id") String id) {
        return this.inputService.getById(id);
    }

    @RequestMapping(value = "/fe/documents",
                    method = RequestMethod.GET)
    @ResponseBody
    @ApiIgnore
    public ResponseListEx listInput(@RequestParam(value = "name",
                    required = false) String name, @RequestParam(value = "type",
                    required = false) String type, @RequestParam(value = "offset",
                    required = false) Integer offset, @RequestParam(value = "limit",
                    required = false) Integer limit) {

        offset = ControllerUtils.configurePaging(offset);
        limit = ControllerUtils.configureLimit(limit);

        return this.inputService.getInputFilter(name, type, offset, limit);
    }

}
