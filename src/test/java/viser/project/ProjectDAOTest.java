package viser.project;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viser.dao.project.ProjectDAO;
import viser.dao.user.UserDAO;
import viser.domain.project.Image;
import viser.domain.project.Project;
import viser.domain.user.User;
import viser.user.UserTest;

public class ProjectDAOTest {
  private static final Logger logger = LoggerFactory.getLogger(ProjectDAOTest.class);
  public static Project TEST_PROJECT = new Project("TEST_PROJECT");
  private ProjectDAO projectDAO;
  private UserDAO userDAO;

  @Before
  public void setup() throws SQLException {
    projectDAO = new ProjectDAO();
    userDAO = new UserDAO();
    projectDAO.removeProject(ProjectDAOTest.TEST_PROJECT.getProjectName());
    projectDAO.addProject(ProjectDAOTest.TEST_PROJECT);
  }

  @After
  public void returns() throws SQLException {
    projectDAO.removeProject(ProjectDAOTest.TEST_PROJECT.getProjectName());
  }

  @Test
  public void Connection() throws SQLException {
    Connection con = projectDAO.getConnection();
    assertNotNull(con);
  }

  @Test
  public void projectCrud() throws SQLException {
    Project dbProject = projectDAO.getByProjectName(TEST_PROJECT.getProjectName());
    assertEquals(TEST_PROJECT.getProjectName(), dbProject.getProjectName());

    Project UpdateProject = new Project("UpdateProject");
    projectDAO.updateProject(UpdateProject.getProjectName(), dbProject.getProjectName());
    dbProject = projectDAO.getByProjectName(UpdateProject.getProjectName());
    assertEquals(dbProject.getProjectName(), UpdateProject.getProjectName());

    projectDAO.removeProject(UpdateProject.getProjectName());
  }

  @Test
  public void projectMemberCrud() throws SQLException {
    User user = UserTest.TEST_USER;
    List ProjectMemberlist = new ArrayList();
    List Projectlist = new ArrayList();

    userDAO.removeUser(user.getUserId());
    userDAO.addUser(user);

    // invite Test
    projectDAO.InviteUser(user.getUserId(), TEST_PROJECT.getProjectName(), 0);
    User invitedUser = projectDAO.getProjectMember(TEST_PROJECT.getProjectName());
    assertEquals(user.getUserId(), invitedUser.getUserId());

    // getProjectMemberList Test
    ProjectMemberlist = projectDAO.getProjectMemberList(TEST_PROJECT.getProjectName());
    assertNotNull(ProjectMemberlist);
    logger.debug("projectMember : {}", ProjectMemberlist);

    // getProjectList Test
    Projectlist = projectDAO.getProjectList(user.getUserId());
    assertNotNull(Projectlist);
    logger.debug("projectList : {}", Projectlist);

    // remove Test
    projectDAO.removeProjectMember(invitedUser.getUserId(), TEST_PROJECT.getProjectName());
    invitedUser = projectDAO.getProjectMember(TEST_PROJECT.getProjectName());
    assertNull(invitedUser);

    userDAO.removeUser(user.getUserId());
  }

  @Test
  public void getUserList() throws SQLException {
    userDAO.addUser(new User("loginUser", "", "", "", "", ""));
    userDAO.addUser(new User("TestId1", "", "", "", "", ""));
    userDAO.addUser(new User("TestId2", "", "", "", "", ""));

    logger.debug("Users : {}", projectDAO.getUserList("te", "loginUser"));
    assertNotNull(projectDAO.getUserList("te", "loginUser"));

    userDAO.removeUser("loginUser");
    userDAO.removeUser("TestId1");
    userDAO.removeUser("TestId2");
  }

  @Test
  public void imageCrd() throws Exception {
    User user = UserTest.TEST_USER;
    Image image = new Image("TEST_PATH", user.getUserId());
    List list = new ArrayList();
    
    projectDAO.removeImage(image.getImagePath());
    projectDAO.addImage(image, TEST_PROJECT.getProjectName());
    
    image.setImageNum(projectDAO.getImageNum(TEST_PROJECT.getProjectName(), image.getAuthor()));

    Image dbimage = projectDAO.getByImageNum(image.getImageNum());
    logger.debug("image : {}", image);
    logger.debug("DBimage : {}", dbimage);
    assertEquals(image.getImagePath(), dbimage.getImagePath());
    assertEquals(image.getAuthor(), dbimage.getAuthor());
    
    list = projectDAO.getImageList(TEST_PROJECT.getProjectName());
    assertNotNull(list);
    logger.debug("imageList : {}", list);
  }
}
