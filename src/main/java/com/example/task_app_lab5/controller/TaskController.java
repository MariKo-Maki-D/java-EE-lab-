package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.Category;
import com.example.task_app_lab5.model.Tasks;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.CategoryRepo;
import com.example.task_app_lab5.reposiory.TaskRepo;
import com.example.task_app_lab5.reposiory.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {
    @Autowired
    private TaskRepo taskRepository;
    @Autowired
    private UserRepo  userRepo;
    @Autowired
    private CategoryRepo categoryRepository;

    @GetMapping("/tasks")
    public String viewTasks(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        List<Tasks> tasks = taskRepository.findByUserId(user.getId());
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/tasks/add")
    public String showAddTaskForm(Model model){
        model.addAttribute("task", new Tasks());
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "addTask";
    }

    @PostMapping("/tasks/add")
    public String addTask(@ModelAttribute Tasks task, @AuthenticationPrincipal UserDetails userDetails){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks";
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
