package Todo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class Controller implements Initializable
{
    //FXML Bindings
    @FXML private TableView<TodoTable> tableView;
    @FXML private TableColumn<TodoTable, Integer> taskIDColumn;
    @FXML private TableColumn<TodoTable, String> taskNameColumn;
    @FXML private TableColumn<TodoTable, LocalDate> dueDateColumn;
    @FXML private TextField taskField;
    @FXML private DatePicker dueDate;
    @FXML private Label errorLabel;


    //execute this method when submit button is pushed
    public void submitButtonPushed(ActionEvent event)
    {

        try
        {
            if (taskField.getText().trim().isEmpty() || taskField.getText() == null)
            {
                errorLabel.setText("Task input cannot be empty");
            }
            else
            {
                //instantiate new todoTable object, pass it the value enter in the taskField and dueDate boxes in the scene
                TodoTable todoTable = new TodoTable(taskField.getText(), dueDate.getValue());
                errorLabel.setText(" ");//Sets error message to hidden
                todoTable.insertNewTask();//calls the insert method within todoTable to insert the data held by the todoTable object that was just instantiated
                //sets the value inside the text area as " " for QOL
                taskField.setText(" ");
                //sets the value of date picker to default (current day) for QOL
                dueDate.setValue(LocalDate.now());
                //calls method that populates table.
                loadTodoListTable();
            }
        }
        catch (Exception e)
        {
            errorLabel.setText(e.getMessage());//sets the label text to the error message
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //cell factories for the tables columns
        taskIDColumn.setCellValueFactory(new PropertyValueFactory<TodoTable, Integer>("taskID"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<TodoTable, String>("taskName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<TodoTable, LocalDate>("dueDate"));

        try
        {
            loadTodoListTable();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

        //Disables date in the date picker that have already passed based on the local date of the system.
        //also sets a max due date available for datepicker to 2 years from current day
        LocalDate maxDate = LocalDate.now().plusYears(2);
        dueDate.setDayCellFactory(picker -> new DateCell()
            {
                public void updateItem(LocalDate date, boolean isEmpty)
                {
                    super.updateItem(date, isEmpty);
                    LocalDate now = LocalDate.now();
                    setDisable(isEmpty || (date.compareTo(now) < 0) || date.isAfter(maxDate));
                }
            });
        //sets the default date to local date of now (the day the program is launched)
        dueDate.setValue(LocalDate.now());
    }

    private void loadTodoListTable() throws SQLException {
        //Creates observable to do list from to do data
        ObservableList<TodoTable> todoTable = FXCollections.observableArrayList();

        //declaring variables used to establish connection, store statement, and result set
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;



        try
        {
            //establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/2372db?autoReconnect=true&useSSL=false", "student", "Gr01stu211!");

            //create a statement
            statement = connection.createStatement();
            //create a query
            resultset = statement.executeQuery("SELECT * FROM silasTodoList");
            //loops through to do list table
            while (resultset.next())
            {
                //gets the task name and due date based on the column label
                TodoTable newTodoData = new TodoTable(resultset.getString("taskName"),resultset.getDate("dueDate").toLocalDate());
                //gets the id based on the column label
                newTodoData.setTaskID(resultset.getInt("taskID"));
                todoTable.add(newTodoData);
            }
            //resets the table so when method is called from submitButtonPushed it doesn't re-add all the previously shown entries
            tableView.getItems().clear();
            //adds the items to the table view
            tableView.getItems().addAll(todoTable);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        //close connections
        finally
        {
            if(resultset != null)
            {
                connection.close();
            }
            if(statement != null)
            {
                connection.close();
            }
            if(connection != null)
            {
                connection.close();
            }
        }
    }


}



