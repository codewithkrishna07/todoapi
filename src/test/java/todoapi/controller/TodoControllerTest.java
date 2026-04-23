package todoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import todoapi.config.TestSecurityConfig;
import todoapi.exception.TodoNotFoundException;
import todoapi.model.Todo;
import todoapi.security.JwtFilter;
import todoapi.service.TodoService;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TodoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@Import(TestSecurityConfig.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo sampleTodo() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Buy milk");
        todo.setDescription("2% milk");
        todo.setIsCompleted(false);
        return todo;
    }

    // ─────────────────────────────────────────
    // GET /myTodos
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /myTodos should return 200 with list of todos")
    void getAllTodos_shouldReturn200() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(sampleTodo()));

        mockMvc.perform(get("/myTodos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Buy milk"))
                .andExpect(jsonPath("$[0].isCompleted").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /myTodos should return empty list when no todos exist")
    void getAllTodos_shouldReturnEmptyList() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of());

        mockMvc.perform(get("/myTodos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────────────────────────────────
    // GET /myTodos/{id}
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /myTodos/{id} should return 200 when todo exists")
    void getTodoById_shouldReturn200_whenExists() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(sampleTodo());

        mockMvc.perform(get("/myTodos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /myTodos/{id} should return 404 when todo not found")
    void getTodoById_shouldReturn404_whenNotFound() throws Exception {
        when(todoService.getTodoById(99L))
                .thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/myTodos/99"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────
    // POST /myTodos
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /myTodos should return 201 when todo is created")
    void createTodo_shouldReturn201() throws Exception {
        when(todoService.createTodo(any(Todo.class))).thenReturn(sampleTodo());

        mockMvc.perform(post("/myTodos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTodo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /myTodos should return 400 when title is blank")
    void createTodo_shouldReturn400_whenTitleIsBlank() throws Exception {
        Todo invalidTodo = new Todo();
        invalidTodo.setTitle("");

        mockMvc.perform(post("/myTodos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // PUT /myTodos/{id}
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PUT /myTodos/{id} should return 200 when todo is updated")
    void updateTodo_shouldReturn200() throws Exception {
        Todo updatedTodo = new Todo();
        updatedTodo.setId(1L);
        updatedTodo.setTitle("Buy oat milk");
        updatedTodo.setDescription("From Whole Foods");
        updatedTodo.setIsCompleted(true);

        when(todoService.updatedTodo(any(Long.class), any(Todo.class)))
                .thenReturn(updatedTodo);

        mockMvc.perform(put("/myTodos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy oat milk"))
                .andExpect(jsonPath("$.isCompleted").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /myTodos/{id} should return 404 when todo not found")
    void updateTodo_shouldReturn404_whenNotFound() throws Exception {
        when(todoService.updatedTodo(any(Long.class), any(Todo.class)))
                .thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(put("/myTodos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTodo())))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────
    // PATCH /myTodos/{id}
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PATCH /myTodos/{id} should return 200 when todo is patched")
    void patchTodo_shouldReturn200() throws Exception {
        Todo patchData = new Todo();
        patchData.setTitle("Patched title");

        Todo patchedResult = new Todo();
        patchedResult.setId(1L);
        patchedResult.setTitle("Patched title");
        patchedResult.setDescription("2% milk");
        patchedResult.setIsCompleted(false);

        when(todoService.patchTodo(any(Long.class), any(Todo.class)))
                .thenReturn(patchedResult);

        mockMvc.perform(patch("/myTodos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched title"))
                .andExpect(jsonPath("$.description").value("2% milk"));
    }

    // ─────────────────────────────────────────
    // DELETE /myTodos/{id}
    // ─────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("DELETE /myTodos/{id} should return 204 when deleted")
    void deleteTodo_shouldReturn204() throws Exception {
        when(todoService.deleteTodo(1L)).thenReturn(true);

        mockMvc.perform(delete("/myTodos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /myTodos/{id} should return 404 when todo not found")
    void deleteTodo_shouldReturn404_whenNotFound() throws Exception {
        when(todoService.deleteTodo(99L))
                .thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(delete("/myTodos/99"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────
    // Security
    // ─────────────────────────────────────────

    @Test
    @DisplayName("GET /myTodos should return 401 when not logged in")
    void getAllTodos_shouldReturn401_whenNotAuthenticated() throws Exception {
        // No @WithMockUser — simulates unauthenticated request
        mockMvc.perform(get("/myTodos"))
                .andExpect(status().isUnauthorized());
    }
}