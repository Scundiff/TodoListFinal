package Todo;

import java.sql.*;
import java.time.LocalDate;


public class TodoTable
{
    private int taskID;
    private String taskName;
    private LocalDate dueDate;

    //constructors
    public TodoTable(String taskName, LocalDate dueDate)
    {
        setTaskName(taskName);
        setDueDate(dueDate);
    }
    public TodoTable(int taskID, String taskName, LocalDate dueDate)
    {
        setTaskID(taskID);
        setTaskName(taskName);
        setDueDate(dueDate);
    }

    //Getters and Setters
    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        String input = taskName;
        //if it has at least 1 character, the checks to make sure it doesn't exceed 100 characters
        if (input.length() < 101)
        {
            this.taskName = taskName;
        }
        else
        {
            throw new IllegalArgumentException("Task Name Length Cannot exceed 100 characters");
        }
    }


    public LocalDate getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate)
    {
        this.dueDate = dueDate;
    }
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }


    public void insertNewTask() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try
        {
            //Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/2372db?autoReconnect=true&useSSL=false", "student", "Gr01stu211!");
            //set up string to hold query
            String query = "INSERT INTO silasTodoList (taskName, dueDate)" + "VALUES (?,?)";
            //pass statement value of query
            preparedStatement = connection.prepareStatement(query);

            //converts date of duedate to a value mysql datatype
            Date dDate = Date.valueOf(dueDate);

            //adds bindings for prepared statements
            preparedStatement.setString(1, taskName);
            preparedStatement.setDate(2, dDate);
            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {//closes statement and connection
            if (preparedStatement != null)
            {
                preparedStatement.close();
            }
            if (connection != null)
            {
                connection.close();
            }
        }
    }
}
