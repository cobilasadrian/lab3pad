package model;

import java.util.ArrayList;

/**
 * Created by Adrian on 12/22/2015.
 */
public class EmployeeList {
    private ArrayList<Employee> list;

    public EmployeeList(){
        list = new ArrayList<Employee>();
    }

    public void add(Employee e){
        list.add(e);
    }
}
