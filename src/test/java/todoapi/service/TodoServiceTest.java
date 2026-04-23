package todoapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import todoapi.exception.TodoNotFoundException;
import todoapi.model.Todo;
import todoapi.repository.TodoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // ← tells JUnit to use Mockito
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    // ↑ fake repository — no real database involved

    @InjectMocks
    private TodoService todoService;
    // ↑ real TodoService, but with the fake repository injected into it

    private Todo sampleTodo;

    @BeforeEach  // ← runs before every single test
    void setUp() {
        // Create a sample todo we can reuse in every test
        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Buy milk");
        sampleTodo.setDescription("2% milk");
        sampleTodo.setIsCompleted(false);
    }

    // ─────────────────────────────────────────
    // Tests for getAllTodos()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getAllTodos should return list of todos")
    void getAllTodos_shouldReturnAllTodos() {
        // GIVEN — tell fake repository what to return
        when(todoRepository.findAll()).thenReturn(List.of(sampleTodo));

        // WHEN — call the real service method
        List<Todo> result = todoService.getAllTodos();

        // THEN — check the result is correct
        assertEquals(1, result.size());
        assertEquals("Buy milk", result.get(0).getTitle());
    }

    @Test
    @DisplayName("getAllTodos should return empty list when no todos exist")
    void getAllTodos_shouldReturnEmptyList() {
        // GIVEN
        when(todoRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<Todo> result = todoService.getAllTodos();

        // THEN
        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────
    // Tests for getTodoById()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getTodoById should return todo when ID exists")
    void getTodoById_shouldReturnTodo_whenIdExists() {
        // GIVEN
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        // WHEN
        Todo result = todoService.getTodoById(1L);

        // THEN
        assertNotNull(result);
        assertEquals("Buy milk", result.getTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getTodoById should throw TodoNotFoundException when ID does not exist")
    void getTodoById_shouldThrowException_whenIdNotFound() {
        // GIVEN — fake returns empty (no todo with this ID)
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        // THEN — assert that calling the method throws the right exception
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.getTodoById(99L);
            // WHEN is inside assertThrows
        });
    }

    // ─────────────────────────────────────────
    // Tests for createTodo()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("createTodo should save and return the todo")
    void createTodo_shouldSaveAndReturnTodo() {
        // GIVEN — when save() is called with any Todo, return sampleTodo
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // WHEN
        Todo result = todoService.createTodo(sampleTodo);

        // THEN
        assertNotNull(result);
        assertEquals("Buy milk", result.getTitle());

        // Also verify that save() was actually called once
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    // ─────────────────────────────────────────
    // Tests for updatedTodo()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("updatedTodo should update and return the todo")
    void updatedTodo_shouldUpdateFields() {
        // GIVEN
        Todo updatedData = new Todo();
        updatedData.setTitle("Buy oat milk");
        updatedData.setDescription("From Whole Foods");
        updatedData.setIsCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(sampleTodo);

        // WHEN
        Todo result = todoService.updatedTodo(1L, updatedData);

        // THEN
        assertEquals("Wrong title", result.getTitle());
        assertEquals("From Whole Foods", result.getDescription());
        assertTrue(result.getIsCompleted());
    }

    @Test
    @DisplayName("updatedTodo should throw exception when todo not found")
    void updatedTodo_shouldThrowException_whenIdNotFound() {
        // GIVEN
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        // THEN
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.updatedTodo(99L, new Todo());
        });
    }

    // ─────────────────────────────────────────
    // Tests for deleteTodo()
    // ─────────────────────────────────────────

    @Test
    @DisplayName("deleteTodo should delete the todo when ID exists")
    void deleteTodo_shouldDelete_whenIdExists() {
        // GIVEN
        when(todoRepository.existsById(1L)).thenReturn(true);

        // WHEN
        boolean result = todoService.deleteTodo(1L);

        // THEN
        assertTrue(result);
        verify(todoRepository, times(1)).deleteById(1L);
        // ↑ verify deleteById was actually called once
    }

    @Test
    @DisplayName("deleteTodo should return false when ID does not exist")
    void deleteTodo_shouldReturnFalse_whenIdNotFound() {
        // GIVEN
        when(todoRepository.existsById(99L)).thenReturn(false);

        // WHEN
        boolean result = todoService.deleteTodo(99L);

        // THEN
        assertFalse(result);
        verify(todoRepository, never()).deleteById(any());
        // ↑ verify deleteById was NEVER called (nothing to delete)
    }
}