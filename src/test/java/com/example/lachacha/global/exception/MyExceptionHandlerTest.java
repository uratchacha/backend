package com.example.lachacha.global.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SampleController.class)
class MyExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHandleMyException() throws Exception {
        mockMvc.perform(get("/test?name=")) // 빈 값 전달
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("잘못된 입력값입니다."));
    }

    @Test
    void testHandleUserNotFoundException() throws Exception {
        mockMvc.perform(get("/test?name=admin")) // 특정 사용자 존재하지 않는 경우
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    void testHandleUnexpectedException() throws Exception {
        mockMvc.perform(get("/unexpected-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."));
    }

}
