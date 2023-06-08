package com.sirma.pairofemployees.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class Pair<E, E1> {
    private int employeeId1;
    private int employeeId2;
    private int projectId;
    private long daysWorked;

    public Pair(Employee employee1, Employee employee2) {
        this.employeeId1 = employee1.getEmployeeID();
        this.employeeId2 = employee2.getEmployeeID();
    }
}
