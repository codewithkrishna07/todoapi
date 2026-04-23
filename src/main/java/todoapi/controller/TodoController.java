package todoapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todoapi.exception.TodoNotFoundException;
import todoapi.model.Todo;
import todoapi.service.TodoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/myTodos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public List<Todo> getALlTodos(){
        return todoService.getAllTodos();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id){
       return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public  ResponseEntity<Todo> createTodos(@Valid @RequestBody Todo todo){
        Todo create= todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody  Todo todo){

        return ResponseEntity.ok(todoService.updatedTodo(id, todo));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> patchTodo(@PathVariable Long id, @RequestBody Todo todo)  {

        return ResponseEntity.ok(todoService.patchTodo(id,todo));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Todo> deleteTodo(@PathVariable Long id){

        boolean deleted = todoService.deleteTodo(id);

        if(deleted){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }

}
