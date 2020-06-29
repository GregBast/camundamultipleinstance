package be.camunda.multipleinstance;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class MultipleinstanceApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @Deployment(resources = "bpmn/multiple_instance.bpmn")
    public void end2endTest() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        MockMvcRequestBuilders
                        .post("/REST/multipleinstance/instances")
                );

        resultActions
                .andDo(print())
                .andExpect(status().isOk());

        String businessKey = resultActions.andReturn().getResponse().getContentAsString();
        String itemToCorrelate = "item2";


        resultActions = mockMvc
                .perform(
                        MockMvcRequestBuilders
                        .post("/REST/multipleinstance/instances/" + businessKey)
                        .queryParam("itemId", itemToCorrelate)
                );

        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

}
