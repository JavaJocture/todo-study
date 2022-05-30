package jocture.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jocture.todo.dto.UserDto;
import jocture.todo.entity.User;
import jocture.todo.exception.ApplicationException;
import jocture.todo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    // Overloading: 동일한 메서드명을 사용 (구분방법: 파라미터 수, 파라미터 타입)
    // Overriding : 메서드 재정의(상속관계에서 필요)

    // 메서드 시그니처 : 메서드명 + 파라미터 타입 (Overloading 시 구분) -> 런타임 시에 구분할 수 있느냐?
    //  -> 잘못 설명한 부분(시그니처X) : 반환타입, throws

    @Test
    void signUp_success() throws Exception {
        // Given
        String url = "/auth/signup";
        UserDto userDto = UserDto.builder()
            .email("test@abc.com")
            .username("TEST")
            .password("PaSsWoRd")
            .build();
        String body = objectMapper.writeValueAsString(userDto);
        // 메서드 모킹
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            User user = (User) args[0];
            ReflectionTestUtils.setField(user, "id", "abcdefg"); // Java Reflection
            return null;
        }).when(userService).signUp(any());

        // When && Then
        MvcResult mvcResult = mvc.perform(post(url).content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // .andExpect(status().is(200))
            .andExpect(status().isOk())
            .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        log.debug(">>> responseBody : {}", responseBody);
    }

    @Test
    void signUp_exception() throws Exception {
        // Given
        String url = "/auth/signup";
        UserDto userDto = UserDto.builder()
            .email("test@abc.com")
            .username("TEST")
            .password("PaSsWoRd")
            .build();
        String body = objectMapper.writeValueAsString(userDto);
        // 메서드 모킹
        doThrow(new ApplicationException("ex message"))
            .when(userService)
            .signUp(any());

        // When && Then
        mvc.perform(post(url).content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }
}