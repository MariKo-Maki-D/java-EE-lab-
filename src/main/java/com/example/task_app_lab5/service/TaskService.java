package com.example.task_app_lab5.service;
import com.example.task_app_lab5.model.Tasks;
import com.example.task_app_lab5.reposiory.TaskRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepo taskRepo;

    public TaskService(TaskRepo taskRepo){
        this.taskRepo = taskRepo;
    }
    //@PreAuthorize("hasRole('ADMIN')")
    //public List<Tasks> getAllTasks(){};

    public Page<Tasks> getTasks(String search, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        if (search!= null && !search.isEmpty()){
            return taskRepo.findByTitleContainingIgnoreCase(search,pageable);
        } else {
            return taskRepo.findAll(pageable);
        }
    }
}
