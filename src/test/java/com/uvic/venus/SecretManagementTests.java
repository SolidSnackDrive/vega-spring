package com.uvic.venus;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import java.util.concurrent.TimeUnit;



@SpringBootTest
public class SecretManagementTests {
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
    /* 
    Feature: Read secrets
    Scenario: Users can see a list of all secrets in their vault
        Given a user has an existing account
        And they have been approved to use Vega services by an admin
        When they navigate to the Vega Vault tab
        Then they are able to see all secrets contained in their vault
        And they can filter secret entries by date
    */
    @Test
    public void testReadUserZeroSecrets() {

       // Find login button
       WebElement login = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
       assertEquals("Login/SignUp", login.getText(), "Button not found");

       login.click();
       
       // Login using USER credentials
       driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[1]/input")).sendKeys("testuser@venus.com");
       driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div/form/div[2]/input")).sendKeys("pass" + Keys.ENTER);

       // Navigate to vault
       WebElement vault = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[5]"));
       vault.click();

       // Check there are no secrets 

       assertThrows(NoSuchElementException.class, () -> 
       driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div/div[2]")));
       

        // Check if buttons exits
        WebElement resources = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/a[4]"));
        WebElement logout = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/nav[2]/div/div/div/a"));
        assertEquals("Resources", resources.getText(), "Button not found");
        assertEquals("Logout", logout.getText(), "Button not found");        


    }

 

    
}
