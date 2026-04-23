package todoapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import todoapi.exception.TodoNotFoundException;
import todoapi.model.Todo;
import todoapi.repository.TodoRepository;

import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo updatedTodo(Long id, Todo updatedTodo) {
        Todo todo = getTodoById(id);
        todo.setTitle(updatedTodo.getTitle());
        todo.setDescription(updatedTodo.getDescription());
        todo.setIsCompleted(updatedTodo.getIsCompleted());
        todo.setDueDate(updatedTodo.getDueDate());
        return todoRepository.save(todo);
    }

    public Todo patchTodo(Long id, Todo patchTodo) {
        Todo existing = getTodoById(id);
        if (patchTodo.getTitle() != null) {
            existing.setTitle(patchTodo.getTitle());
        }
        if (patchTodo.getDescription() != null) {
            existing.setDescription(patchTodo.getDescription());
        }
        if (patchTodo.getIsCompleted() != null) {
            existing.setIsCompleted(patchTodo.getIsCompleted());
        }
        if (patchTodo.getDueDate() != null) {
            existing.setDueDate(patchTodo.getDueDate());
        }
        return todoRepository.save(existing);
    }

    public boolean deleteTodo(Long id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}