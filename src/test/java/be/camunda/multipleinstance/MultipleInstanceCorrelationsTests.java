package be.camunda.multipleinstance;


import be.camunda.multipleinstance.service.CamundaService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

@Deployment(resources = "bpmn/multiple_instance.bpmn")
public class MultipleInstanceCorrelationsTests {

    private static final String BUSINESS_KEY = "businessKey";

    private static final List<String> ITEMS = Arrays.asList("item1", "item2", "items3");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    @ClassRule
    public static ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder.create().build();


    @Before
    public void setup() {
        init(processEngineRule.getProcessEngine());
    }

    @Test
    public void reproducer1() {

        ProcessInstance processInstance = BpmnAwareTests
                .runtimeService()
                .createProcessInstanceByKey(CamundaService.PROCESS_INSTANCE_KEY)
                .businessKey(BUSINESS_KEY)
                .setVariable(CamundaService.COLLECTION_ITEMS, ITEMS)
                .execute();

        Map<String, Object> correlationKeysMap = new HashMap<>();
        correlationKeysMap.put(CamundaService.COLLECTION_ITEM_ID, "item1");

        BpmnAwareTests
                .assertThat(processInstance)
                .isWaitingAt(CamundaService.EVENT_FOR_SUBPROCESS);

        BpmnAwareTests
                .runtimeService()
                .correlateMessage(
                        CamundaService.MESSAGE_FOR_SUBPROCESS,
                        BUSINESS_KEY,
                        correlationKeysMap,
                        null);
    }

    @Test
    public void reproducer2() {
        ProcessInstance processInstance = BpmnAwareTests
                .runtimeService()
                .createProcessInstanceByKey(CamundaService.PROCESS_INSTANCE_KEY)
                .businessKey(BUSINESS_KEY)
                .setVariable(CamundaService.COLLECTION_ITEMS, ITEMS)
                .execute();

        BpmnAwareTests
                .assertThat(processInstance)
                .isWaitingAt(CamundaService.EVENT_FOR_SUBPROCESS);

        BpmnAwareTests
                .runtimeService()
                .createMessageCorrelation(CamundaService.MESSAGE_FOR_SUBPROCESS)
                .processInstanceBusinessKey(BUSINESS_KEY)
                .processInstanceVariableEquals(CamundaService.COLLECTION_ITEM_ID, "item1")
                .correlate();
    }

    @Test
    public void reproducer3() {
        ProcessInstance processInstance = BpmnAwareTests
                .runtimeService()
                .createProcessInstanceByKey(CamundaService.PROCESS_INSTANCE_KEY)
                .businessKey(BUSINESS_KEY)
                .setVariable(CamundaService.COLLECTION_ITEMS, ITEMS)
                .executeWithVariablesInReturn();

        BpmnAwareTests
                .assertThat(processInstance)
                .isWaitingAt(CamundaService.EVENT_FOR_SUBPROCESS);

        BpmnAwareTests
                .runtimeService()
                .createMessageCorrelation(CamundaService.MESSAGE_FOR_SUBPROCESS)
                .processInstanceBusinessKey(BUSINESS_KEY)
                .localVariableEquals(CamundaService.COLLECTION_ITEM_ID, Variables.stringValue("item1"))
                .correlate();
    }

}
