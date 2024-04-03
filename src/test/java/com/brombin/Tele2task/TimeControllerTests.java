package com.brombin.Tele2task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@WebMvcTest(com.brombin.Tele2task.controllers.TimeController.class)
public class TimeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetCurrentTime() throws Exception {
        ZonedDateTime currentTime = ZonedDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        mockMvc.perform(get("/server/time"))
                .andExpect(status().isOk())
                .andExpect(content().string(formattedTime));
    }
}
