package com.sirma.pairofemployees.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PairDTO
{
    private int employeeId1;
    private int employeeId2;
    private long daysWorked;
}
