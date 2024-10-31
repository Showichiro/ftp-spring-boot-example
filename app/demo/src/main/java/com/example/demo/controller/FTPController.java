package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FTPService;

@RestController
public class FTPController {
    @Autowired
    private FTPService ftpService;

    @PostMapping("/update-file")
    public void updateFile(@RequestParam String filePath, @RequestBody String content) {
        ftpService.writeFile(filePath, content);
    }

    @GetMapping("read-file")
    public String readFile(@RequestParam String filePath) {
        return ftpService.readFile(filePath);
    }

    @DeleteMapping("delete-file")
    public void deleteFile(@RequestParam String filePath){
        ftpService.deleteFile(filePath);
    }
}
