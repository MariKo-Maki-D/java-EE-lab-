package com.example.task_app_lab5.controller;
import com.example.task_app_lab5.model.Category;
import com.example.task_app_lab5.model.Tasks;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.CategoryRepo;
import com.example.task_app_lab5.reposiory.TaskRepo;
import com.example.task_app_lab5.reposiory.UserRepo;
import com.example.task_app_lab5.service.AdminService;
import com.example.task_app_lab5.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepo taskRepo;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String adminPanel() {
        return "admin";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers(Model model){
        List<User_table> users = userRepo.findAll();
        model.addAttribute("users" ,users);
        return "users";
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewTasks(Model model,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "3") int size) {
        Page<Tasks> tasksPage = taskService.getTasks(search, page, size);
        model.addAttribute("tasks", tasksPage.getContent());
        model.addAttribute("currentPage", tasksPage.getNumber());
        model.addAttribute("totalPages", tasksPage.getTotalPages());
        model.addAttribute("search", search);
        return "tasksByAdmin";
    }

    @GetMapping("/tasks/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddTaskForm(Model model){
        model.addAttribute("task", new Tasks());
        List<Category> categories = categoryRepo.findAll();
        model.addAttribute("categories", categories);
        List<User_table> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "addTask";
    }

    @PostMapping("/tasks/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTask(@ModelAttribute Tasks task, @RequestParam("userId") Long userId){
        User_table user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        task.setUser(user);
        taskRepo.save(task);
        adminService.sendTaskNotification(user.getEmail(), task);
        return "redirect:/admin/tasks";
    }
}


