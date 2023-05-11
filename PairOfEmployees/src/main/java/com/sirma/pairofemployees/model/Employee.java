package com.sirma.pairofemployees.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class Employee
{
    private int employeeID;
    private int projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
