package com.saleh.EmployeeManagement.service;

import com.saleh.EmployeeManagement.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmployeeCreationNotification(Employee employee) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employee.getEmail());
        message.setSubject("Welcome to the Company");
        message.setText("Hello " + employee.getFirstName() + ", welcome to our company!");
        mailSender.send(message);
    }

    public void sendEmployeeUpdateNotification(Employee employee) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employee.getEmail());
        message.setSubject("profile update");
        message.setText("Hello " + employee.getFirstName() + ", you profile has been updated!");
        mailSender.send(message);
    }
}
