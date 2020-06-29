package be.camunda.multipleinstance.controllers;

import be.camunda.multipleinstance.service.CamundaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/REST/multipleinstance")
@RestController
public class MultipleInstanceController {

    private CamundaService camundaService;

    public MultipleInstanceController(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    @RequestMapping(
            value = "/instances",
            produces = { "text/plain" },
            method = RequestMethod.POST)
    public ResponseEntity<String> startProcess() {
        List<String> items = Arrays.asList("item1", "item2", "item3");
        String businessKey = camundaService.startProcess(items);

        return ResponseEntity.ok(businessKey);
    }

    @RequestMapping(
            value = "/instances/{businessKey}",
            produces = { "text/plain" },
            method = RequestMethod.POST)
    public ResponseEntity<String> correlateProcess(
            @PathVariable("businessKey") String businessKey,
            @RequestParam(value = "itemId", required = true) String itemId) {
        camundaService.correlateSubprocessInstance(businessKey, itemId);
        return ResponseEntity.ok("correlation ok");
    }


}
