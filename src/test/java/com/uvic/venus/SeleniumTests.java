package com.uvic.venus;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SeleniumTests{
    public WebDriver driver;

    @BeforeEach
    public void setup(){
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get("http://localhost:3000");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterEach
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }   
    }

    @Test
    public void testAdminLogin(){
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login/SignUp", login.getText(), "Button not found");

        login.click();
        
        // Login using ADMIN credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("admin@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement resources = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[4]"));
        WebElement logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        WebElement admin = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[7]"));
        assertEquals("Resources", resources.getText(), "Button not found");
        assertEquals("Logout", logout.getText(), "Button not found");
        assertEquals("Admin", admin.getText(), "Button not found");
    }

    @Test
    public void testStaffLogin(){
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login/SignUp", login.getText(), "Button not found");

        login.click();
        
        // Login using STAFF credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("jonoliver@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement resources = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[4]"));
        WebElement logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Resources", resources.getText(), "Button not found");
        assertEquals("Logout", logout.getText(), "Button not found");
    }

    @Test
    public void testUserLogin(){
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login/SignUp", login.getText(), "Button not found");

        login.click();
        
        // Login using USER credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("testuser@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Logout", logout.getText(), "Button not found");
    }

    @Test
    public void testAdminPanel(){
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login/SignUp", login.getText(), "Button not found");

        login.click();
        
        // Login using ADMIN credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("admin@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement admin = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[7]"));
        assertEquals("Admin", admin.getText(), "Button not found");

        admin.click();
        
        // Check if list of users is empty
        WebElement table = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/table/tbody"));
        List<WebElement> userList = table.findElements(By.tagName("tr"));
        assertFalse(userList.isEmpty());
    }


    @Test
    public void testChangeUserRole(){
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login/SignUp", login.getText(), "Button not found");

        login.click();
        
        // Login using ADMIN credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("admin@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement admin = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[7]"));
        assertEquals("Admin", admin.getText(), "Button not found");

        admin.click();
        
        // Check if list of users is empty
        WebElement table = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/table/tbody"));
        List<WebElement> userList = table.findElements(By.tagName("tr"));
        assertFalse(userList.isEmpty());


        // Change Paul Aguilar Role to USER
        WebElement roleSelect = userList.get(1).findElement(By.className("form-select"));
        Select roleUSER = new Select(roleSelect);
        roleUSER.selectByValue("ROLE_USER");
    }

    @Test
    public void testRegistration() {
        WebElement signup = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div[2]/a"));
        assertEquals("Signup", signup.getText(), "Button not fonud");
        signup.click();

        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("selenium@test.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("Sel");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[3]/input")).sendKeys("Enium");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[4]/input")).sendKeys("pass" + Keys.ENTER);

        WebElement success = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/h6"));
        assertEquals("User created successfully!", success.getText());
    }

    @Test
    public void testApproval() {
        // Find login button
        WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login", login.getText(), "Button not found");

        login.click();

        // Login using ADMIN credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("admin@venus.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        WebElement admin = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[7]"));
        assertEquals("Admin", admin.getText(), "Button not found");

        admin.click();

        WebElement table = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/table/tbody"));
        WebElement enable = driver.findElement(By.xpath("//td[text()='Sel']/following-sibling::td[3]/a"));

        assertEquals("Enable User", enable.getText());

        enable.click();

        WebElement logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div[1]/a"));
        assertEquals("Logout", logout.getText());

        logout.click();

        WebElement logout_button = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/div/button"));
        assertEquals("signout", logout_button.getText());

        logout_button.click();

        login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Login", login.getText(), "Button not found");
        login.click();

        // Login using USER credentials
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("selenium@test.com");
        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

        // Check if buttons exits
        logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Logout", logout.getText(), "Button not found");
    }
}
