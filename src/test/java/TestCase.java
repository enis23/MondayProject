import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class TestCase {
    private Statement statement;
    private Connection connection;

    @Parameters({"jdbc_username", "jdbc_password", "jdbc_url"})
    @BeforeClass
    public void connect(String user, String password, String url) throws SQLException {
        connection = DriverManager.getConnection( url, user, password );
        statement = connection.createStatement();
    }

    @AfterClass
    public void disconnect() throws SQLException {
        connection.close();
    }

    @DataProvider(name = "gender")
    public Object[][] genderData() throws SQLException {
        // select everything from gender table and generate Object[][] with id and gender columns
        ResultSet resultSet = statement.executeQuery( "select " +
                "* " +
                "from gender" );
        resultSet.last();
        int numberOfRow = resultSet.getRow();
        Object[][] resultData = new Object[numberOfRow][2];
        resultSet.beforeFirst();
        int i = 0;
        while(resultSet.next()) {
            Integer id = resultSet.getInt( "id" );
            String gender = resultSet.getString( "gender" );
            resultData[i][0] = id;
            resultData[i][1] = gender;
            i++;
        }

        return resultData;
    }
    @Test(dataProvider = "gender")
    public void genderTest(Integer idFromSQL, String genderFromSQL) throws IOException {
        // read from gender.xlsx, get gender value by id provided and assert that gender in excel is the same as gender in mysql
        FileInputStream excelFile = new FileInputStream(new File("src/test/resources/gender.xlsx"));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        String genderFromExcel = "";
        for (int i = 1; i <= 100; i++) { // here skipping zero index because is just headers
            Row row = sheet.getRow(i);
            if(row.getCell(0).getNumericCellValue() == idFromSQL){
                genderFromExcel = row.getCell( 1 ).getStringCellValue();
            }
        }
        // Alternatively
    /** Row row = sheet.getRow( idFromSQL );
        Cell idCell = row.getCell( 0 );
        Cell genderCell = row.getCell( 1 );
        String genderFromExcel = genderCell.getStringCellValue(); */
        Assert.assertEquals( genderFromSQL, genderFromExcel );
    }

    @DataProvider(name = "student")
    public Object[][] studentsData() throws SQLException {
        // select id and multiplier from students table and generate Object[][]
        ResultSet resultSet = statement.executeQuery( "select " +
                "* " +
                "from student" );
        resultSet.last();
        int numberOfRow = resultSet.getRow();
        Object[][] resultData = new Object[numberOfRow][2];
        resultSet.beforeFirst();
        int i = 0;
        while(resultSet.next()) {
            Integer id = resultSet.getInt( "id" );
            Double multiplier = resultSet.getDouble( "multiplier" );
            resultData[i][0] = id;
            resultData[i][1] = multiplier;
            i++;
        }

        return resultData;
    }

    @Test(dataProvider = "student")
    public void multiplierTest(Integer id, Double multiplier) throws SQLException {
        // select student's fee from students table by id and save in variable for assertion
        PreparedStatement resultStatement = connection.prepareStatement( "select fee from student " +
                "where id = ?" );
        resultStatement.setInt( 1, id );
        ResultSet rs = resultStatement.executeQuery();
        rs.first();
        Double expected = rs.getDouble( 1 );

        // update student's fee by id, multiply fee by multiplier
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE student SET fee = (fee * ?) WHERE id = ?;");
        preparedStatement.setDouble( 1, multiplier);
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();

        // select student's fee from students table by id and assert that it's updated according to multiplier within 0.004 delta
        rs = resultStatement.executeQuery();
        rs.first();
        Double actual = rs.getDouble( 1 );
        Assert.assertEquals(actual, expected * multiplier, 0.004);
    }


}
