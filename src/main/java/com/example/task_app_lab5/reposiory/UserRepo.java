package com.example.task_app_lab5.reposiory;

import com.example.task_app_lab5.model.User_table;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User_table, Long>{
    User_table findByUsername(String username);
    User_table findByEmail(String email);
    List<User_table> findByRole(User_table.Role role);
}
