package model;

public class Employee{
    private String firstName;
    private String lastName;
    private String department;
    private Double salary;

    public Employee(String firstName, String lastName, String departament, Double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = departament;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                '}';
    }
}
