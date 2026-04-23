package todoapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cant be blank")
    @Size(min=2,max=100, message="Title must be under 100 characters")
    private String title;

    @Size(max = 100, message = "description under 100 characters")
    private String description;
    private Boolean isCompleted;

    @FutureOrPresent(message = "Due date cannot be in the past")
    @JsonFormat(pattern = "MM-dd-yyyy")
    private LocalDate dueDate;

    //no argument constructor
    public Todo(){}

    //Constructor with all the arguments
    public Todo(Long id,String title,String description, Boolean isCompleted, LocalDate dueDate){
        this.id=id;
        this.title=title;
        this.description=description;
        this.isCompleted=isCompleted;
        this.dueDate=dueDate;
    }

    //getter
    public Long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Boolean getIsCompleted(){
        return isCompleted;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }

    //setters
    public void setId(Long id){
        this.id=id;
    }
    public void setTitle(String title) {
      this.title= title;
    }
    public void setDescription(String description){
        this.description=description;
    }

    public void setIsCompleted(Boolean isCompleted){
        this.isCompleted=isCompleted;
    }

    public void setDueDate(LocalDate dueDate){
        this.dueDate=dueDate;
    }
}
