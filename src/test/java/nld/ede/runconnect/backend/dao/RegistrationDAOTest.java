package nld.ede.runconnect.backend.dao;

import nld.ede.runconnect.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class RegistrationDAOTest {

    private static final String GOOGLE_ID = "as22121as";
    private RegistrationDAO sut;

    @BeforeEach
    public void setup() {
        sut = new RegistrationDAO();
    }

    @Test
    public void registerUserReturnsTrueIfUserAddedTest() {
        User user = new User();
        user.setGoogleId(GOOGLE_ID);
        String sql = "insert into `USER` (FIRSTNAME, LASTNAME, E_MAILADRES, USERNAME, IMAGE_URL) values (?, ?, ?, ?, ?)";
        try {
            RegistrationDAO sutSpy = spy(sut);
            doReturn(false).when(sutSpy).isExistingUser(user);

            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);


            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);

            sutSpy.setDatasource(dataSource);
            boolean isRegistered = sutSpy.registerUser(user);

            verify(connection).prepareStatement(sql);
            verify(preparedStatement).executeUpdate();
            assertTrue(isRegistered);
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void registerUserReturnsFalseIfUserExistsTest() {
        User user = new User();
        user.setGoogleId(GOOGLE_ID);
        try {
            RegistrationDAO sutSpy = spy(sut);
            doReturn(true).when(sutSpy).isExistingUser(user);

            boolean isRegistered = sutSpy.registerUser(user);

            assertFalse(isRegistered);

        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void isExistingUserReturnsTrueIfUserFoundTest() {
        User user = new User();
        user.setGoogleId(GOOGLE_ID);
        user.setEmailAddress("email");
        String sql = "SELECT count(*) as count FROM `USER` where E_MAILADRES = ?";
        try {

            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            ResultSet resultSet = mock(ResultSet.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);


            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt(1)).thenReturn(1);

            sut.setDatasource(dataSource);
            boolean exist = sut.isExistingUser(user);

            verify(connection).prepareStatement(sql);
            verify(preparedStatement).executeQuery();
            verify(resultSet).next();
            assertTrue(exist);
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void isExistingUserReturnsFalseIfUserNotFoundTest() {
        User user = new User();
        user.setGoogleId(GOOGLE_ID);
        user.setEmailAddress("email");
        String sql = "SELECT count(*) as count FROM `USER` where E_MAILADRES = ?";
        try {

            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            ResultSet resultSet = mock(ResultSet.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);


            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.getString(1)).thenReturn("ail");

            sut.setDatasource(dataSource);
            boolean exist = sut.isExistingUser(user);

            verify(connection).prepareStatement(sql);
            verify(preparedStatement).executeQuery();
            verify(resultSet).next();
            assertFalse(exist);
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void findUserTest() {
        try {
            String expectedSQL = "SELECT * FROM `USER` WHERE E_MAILADRES = ?";

            DataSource dataSource = mock(DataSource.class);
            Connection connection = mock(Connection.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);

            // instruct Mocks
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(expectedSQL)).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            sut.setDatasource(dataSource);

            User actualUser = sut.findUser(GOOGLE_ID);

            verify(connection).prepareStatement(expectedSQL);
            verify(preparedStatement).setString(1, GOOGLE_ID);
            verify(preparedStatement).executeQuery();
            assertNull(actualUser);
        } catch (SQLException e) {
            fail();
        }
    }

    @Test public void extractUserTest() {
        ResultSet rs = mock(ResultSet.class);
        try {
            when(rs.getInt(1)).thenReturn(2);
            when(rs.getString(2)).thenReturn("first name");
            when(rs.getString(3)).thenReturn("lastname");
            when(rs.getString(4)).thenReturn("emailaddress");
            when(rs.getString(5)).thenReturn("username");
            when(rs.getInt(6)).thenReturn(5);
            when(rs.getString(7)).thenReturn("url");

            User user = sut.extractUser(rs);
            assertEquals(2, user.getUserId());
            assertEquals("first name", user.getFirstName());
            assertEquals("lastname", user.getLastName());
            assertEquals("emailaddress", user.getEmailAddress());
            assertEquals("username",user.getUsername());
            assertEquals(5, user.getTotalScore());
            assertEquals("url",user.getImageUrl());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
