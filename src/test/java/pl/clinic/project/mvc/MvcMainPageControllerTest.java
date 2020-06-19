package pl.clinic.project.mvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcMainPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDisplayMainPage() throws Exception {
        // when
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/")).andDo(MockMvcResultHandlers.print());
        // then
        resultActions.andExpect(content()
                .string(Matchers.containsString("Witaj w naszej przychodni!")))
                .andExpect(status().is2xxSuccessful());
    }
}