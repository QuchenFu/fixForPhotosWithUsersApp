package edu.vanderbilt.finsta;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PhotoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAddUser() throws Exception {
        String result = mockMvc.perform(get("/user?login=jules&first=jules&pass=pass&email=foo@foo.com"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Assert.isTrue(result.equals("true"));
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/api/authentication?username=admin&password=admin"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "USER" })
    public void testAddComment() throws Exception {
        mockMvc.perform(get("/comment/add?user=admin&content=hello"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/comment/add?user=bob&content=hi"))
            .andExpect(status().isOk());
        String result = mockMvc.perform(get("/comments"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Assert.isTrue(result.equals("[{\"user\":\"admin\",\"content\":\"hello\"},{\"user\":\"bob\",\"content\":\"hi\"}]"));
    }

    @Test
    @WithMockUser(username = "Rob", authorities = { "USER" })
    public void testAddCommentToCurrentUser() throws Exception {
        mockMvc.perform(get("/comment/addToCurrentUser?content=yo"))
            .andExpect(status().isOk());
        String result = mockMvc.perform(get("/comments"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Assert.isTrue(result.equals("[{\"user\":\"admin\",\"content\":\"hello\"},{\"user\":\"bob\",\"content\":\"hi\"},{\"user\":\"Rob\",\"content\":\"yo\"}]"));
    }

}
