package be.camunda.multipleinstance.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CamundaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaService.class);

    public static final String PROCESS_INSTANCE_KEY = "multiple_instance_problem";

    public static final String MESSAGE_FOR_SUBPROCESS = "message_for_subprocess";

    public static final String COLLECTION_ITEMS = "items";

    public static final String COLLECTION_ITEM_ID = "item";

    public static final String EVENT_FOR_SUBPROCESS = "event_for_subprocess";

    private final ProcessEngine processEngine;

    @SuppressWarnings("all")
    public CamundaService(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public String startProcess(List<String> items) {
        UUID uuid = UUID.randomUUID();

        ProcessInstance processInstance =
                processEngine
                .getRuntimeService()
                .createProcessInstanceByKey(PROCESS_INSTANCE_KEY)
                .businessKey(uuid.toString())
                .setVariable(COLLECTION_ITEMS, items)
                .execute();

        LOGGER.info("Process instance with id {} created", processInstance.getId());

        return uuid.toString();
    }

    public void correlateSubprocessInstance(String businessKey, String itemId) {
        processEngine
                .getRuntimeService()
                .createMessageCorrelation(MESSAGE_FOR_SUBPROCESS)
                .processInstanceBusinessKey(businessKey)
                .localVariableEquals(COLLECTION_ITEM_ID, "itemId")
                .correlate();
    }

}
