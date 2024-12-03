package com.example.task_app_lab5.controller;
import com.example.task_app_lab5.model.Tasks;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.CategoryRepo;
import com.example.task_app_lab5.reposiory.TaskRepo;
import com.example.task_app_lab5.reposiory.UserRepo;
import com.example.task_app_lab5.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class TaskController {
    @Autowired
    private TaskRepo taskRepository;
    @Autowired
    private UserRepo  userRepo;
    @Autowired
    private CategoryRepo categoryRepository;
    @Autowired
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/tasks")
    @PreAuthorize("hasRole('USER')")
    public String viewTasks(@AuthenticationPrincipal UserDetails userDetails, Model model,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "3") int size) {
        Page<Tasks> tasksPage = taskService.getTasks(search, page, size);
        model.addAttribute("tasksPage", tasksPage);
        model.addAttribute("search", search);
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        List<Tasks> tasks = taskRepository.findByUserId(user.getId());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/tasks/edit/{id}")
    public String showEditTaskForm(@PathVariable("id") long id, Model model){
    Optional<Tasks> task = taskRepository.findById(id);
    if (task.isEmpty()) {
        throw new IllegalArgumentException("task doesnt exist with Id: " + id);
    }
    model.addAttribute("task", task.get());
    model.addAttribute("categories", categoryRepository.findAll());
    return "edittask";
    }

    @PostMapping("/tasks/edit/{id}")
    public String editTask(@PathVariable("id") long id, Tasks updatedTask){
        Optional<Tasks> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent()){
            throw new IllegalArgumentException("task doesnt exist with Id: " + id);
        }
        Tasks task = optionalTask.get();
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDueDate(updatedTask.getDueDate());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());
        task.setCategory(updatedTask.getCategory());
        taskRepository.save(task);
        return "redirect:/tasks";
    }
    @GetMapping("tasks/delete/{id}")
    public String deleteTask (@PathVariable("id") long id){
        Tasks task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid task Id:" + id));
        taskRepository.delete(task);
        return "redirect:/tasks";
    }

    @GetMapping("tasks/filter/status")
    public String filterTaskbyStatus(@RequestParam("status") String status, @AuthenticationPrincipal UserDetails userDetails, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        List<Tasks> tasks = taskRepository.findByUserIdAndStatus(user.getId(), status);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
    @GetMapping("tasks/filter/category")
    public String filterTaskbyCategory(@RequestParam("categoryId") long categoryId, @AuthenticationPrincipal UserDetails userDetails, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        List<Tasks> tasks = taskRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
    @GetMapping("/tasks/sort/dueDate")
    public String sortTasksByDueDate(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        List<Tasks> tasks = taskRepository.findByUserIdOrderByDueDateAsc(user.getId());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }


}
