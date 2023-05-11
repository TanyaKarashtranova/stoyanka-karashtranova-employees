package com.sirma.pairofemployees.service;

import com.sirma.pairofemployees.model.PairDTO;
import com.sirma.pairofemployees.model.error.BadRequestException;
import com.sirma.pairofemployees.model.Employee;
import com.sirma.pairofemployees.model.Pair;
import com.sirma.pairofemployees.model.error.EmptyFileException;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EmployeeService
{
    private List<Employee> employees;
    public static final int employeeIdPosition = 0;
    public static final int projectIdPosition = 1;
    public static final int dateFromPosition = 2;
    public static final int dateToPosition = 3;
    private static final String[] header = {"EmpID1", "EmpID2", "DaysWorked"};
    private  enum DateFormat
    {
        ISO("yyyy-MM-dd"),
        DMY("dd/MM/yyyy"),
        MDY("MM/dd/yyyy");

        private final String template;

        DateFormat(String template)
        {
            this.template = template;
        }

        public String getTemplate()
        {
            return template;
        }
    }

    //        TODO - refactor exception handling
    public List<Employee> loadEmployeesFromCSV(MultipartFile file)
    {
        List<Employee> employees;
        try
        {
            if (file.isEmpty())
            {
                throw new EmptyFileException("No content in file!");
            }
            employees = readCSVFile(file);
        }
        catch (Exception e)
        {
            throw new BadRequestException("No content" + e.getMessage());
        }
        return employees;
    }

    private List<Employee> readCSVFile(MultipartFile file)
    {
        List<Employee> employees = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream()))
        {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : csvParser)
            {
                int empId = Integer.parseInt(csvRecord.get(employeeIdPosition));
                int projectId = Integer.parseInt(csvRecord.get(projectIdPosition));
                LocalDate dateFrom = parseStringToDate(csvRecord.get(dateFromPosition));
                LocalDate dateTo = parseStringToDate(csvRecord.get(dateToPosition));
                if (validateDate(empId, projectId))
                {
                    Employee employee = new Employee(empId, projectId, dateFrom, (dateTo));
                    employees.add(employee);
                }
                else
                {
                    throw new BadRequestException("Not appropriate date!");
                }
            }
        }
        catch (IOException e)
        {
            throw new BadRequestException("Not appropriate date!" + e.getMessage());
        }
        return employees;
    }

    private boolean validateDate(int employeeId, int projectId)
    {
        if (employeeId < 0 || projectId < 0)
        {
            return false;
        }
        return true;
    }

    private LocalDate parseStringToDate(String inputDate)
    {
        LocalDate date = null;
        for (DateFormat dateFormat : DateFormat.values())
        {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat.getTemplate());
                if (!(inputDate.equalsIgnoreCase("null")))
                {
                    date = LocalDate.parse(inputDate, formatter);
                }
                else
                {
                    date = LocalDate.parse(LocalDateTime.now().format(formatter));
                }
                return date;
            }
            catch (DateTimeParseException e)
            {
                continue;
            }
        }
        if (date == null)
        {
            throw new BadRequestException("Invalid date format: " + inputDate);
        }
        return date;
    }

    public PairDTO findLongestWorkingPair(List<Employee> employees)
    {
        Map<Pair, Long> pairToDaysWorked = findAllPairs(employees);
        return findMaximumDaysFromAllPairs(pairToDaysWorked);
    }

//TODO - optimisation algorithm
    private Map<Pair, Long> findAllPairs(List<Employee> employees)
    {
        Map<Pair, Long> pairToDaysWorked = new HashMap<>();
        employees.forEach(employee1 ->
        {
            employees.stream()
                    .skip(employees.indexOf(employee1) + 1)
                    .filter(employee2 -> employee1.getProjectId() == employee2.getProjectId())
                    .forEach(employee2 ->
                    {
                        long daysWorked = calculateDaysWorked(employee1, employee2);
                        Pair pair = new Pair(employee1, employee2);
                        pair.setProjectId(employee1.getProjectId());
                        if (!pairToDaysWorked.containsKey(pair) || daysWorked > pairToDaysWorked.get(pair))
                        {
                            pairToDaysWorked.put(pair, daysWorked);
                            pair.setDaysWorked(daysWorked);
                        }
                    });
        });
        return pairToDaysWorked;
    }

    private PairDTO findMaximumDaysFromAllPairs(Map<Pair, Long> pairToDaysWorked)
    {
        Pair longestWorkingPair = null;
        long maxDaysWorked = 0;
        for (Map.Entry<Pair, Long> entry : pairToDaysWorked.entrySet())
        {
            if (entry.getValue() > maxDaysWorked)
            {
                longestWorkingPair = entry.getKey();
                maxDaysWorked = entry.getValue();
            }
        }
        if (longestWorkingPair == null)
        {
            longestWorkingPair = new Pair(0, 0, 0, 0);
        }
        PairDTO pairDTO = new PairDTO(longestWorkingPair.getEmployeeId1(),longestWorkingPair.getEmployeeId2(),longestWorkingPair.getDaysWorked());
        generateCSVResponse(pairDTO);
        return pairDTO;
    }

    private long calculateDaysWorked(Employee employee1, Employee employee2)
    {
        LocalDate start1 = employee1.getDateFrom();
        LocalDate end1 = employee1.getDateTo();
        LocalDate start2 = employee2.getDateFrom();
        LocalDate end2 = employee2.getDateTo();
        if (end1.isBefore(start2) || end2.isBefore(start1))
        {
            return 0;
        }
        else
        {
            LocalDate startDate = start1.isAfter(start2) ? start1 : start2;
            LocalDate endDate = end1.isBefore(end2) ? end1 : end2;
            return ChronoUnit.DAYS.between(startDate, endDate);
        }
    }

    public void generateCSVResponse(PairDTO pair)
    {
        String name="response"+ File.separator + header[0] + header[1]+"."+"csv";
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(name), CSVFormat.DEFAULT))
        {
            csvPrinter.printRecord(header[0], header[1], header[2]);
            csvPrinter.printRecord(pair.getEmployeeId1(), pair.getEmployeeId2(), pair.getDaysWorked());
        }
        catch (IOException e)
        {
            throw new BadRequestException("Exception while generate csv file" + e.getMessage());
        }
    }
}

