package com.sirma.pairofemployees.controller;

import com.sirma.pairofemployees.model.Employee;
import com.sirma.pairofemployees.model.Pair;
import com.sirma.pairofemployees.model.PairDTO;
import com.sirma.pairofemployees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EmployeeController extends AbstractController {
    @Autowired
    private EmployeeService employeeService;

    @PutMapping("/upload")
    public PairDTO uploadCSVFile(@RequestParam("file") MultipartFile file) {
        return employeeService.findLongestWorkingPair(employeeService.loadEmployeesFromCSV(file));
    }
}
