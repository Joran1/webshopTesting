package test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import db.DbException;

public class ProductTest {
	private WebDriver driver;
	Properties properties;
	Connection connection;
	Statement statement; 
	String url = "jdbc:postgresql://gegevensbanken.khleuven.be:51617/1TX35?currentSchema=r0668325";
	
	@Before
	public void setup() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\utlisateur\\Desktop\\files school\\web files\\chromedriver.exe");
		driver = new ChromeDriver();
		properties = new Properties();	
		properties.setProperty("user", "r0668325");
		properties.setProperty("password", "Jo311098");
		properties.setProperty("ssl","true");
		properties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
		try{
			connection = DriverManager.getConnection(url,properties);
			statement = connection.createStatement();		
		}catch (SQLException e) {
			throw new DbException(e.getMessage(), e);
		}
	}
	
	private void fillOut(String id,String value) {
		WebElement field=driver.findElement(By.id(id));
		field.clear();
		field.sendKeys(value);
	}
	
	private void addProduct(String name, String description, String price) {
		fillOut("name", name);
		fillOut("description", description);
		fillOut("price",price);
		
		WebElement button=driver.findElement(By.id("save"));
		button.click();		
	}
	
	@Test
	public void testAddProduct() {
		driver.get("http://localhost:8080/WebShop/Controller?action=addProduct");
		String title=driver.getTitle();
		assertEquals("Add Product",title);
		addProduct("test", "test", "3.00");
		
		driver.get("http://localhost:8080/WebShop/Controller?action=products");
		
		assertEquals("Products", driver.findElement(By.cssSelector("h2")).getText());	
		ArrayList<WebElement> listItems=(ArrayList<WebElement>) driver.findElements(By.cssSelector("table tr"));
		boolean found=false;
		for (WebElement listItem:listItems) {
				if (listItem.getText().contains("test")) {
				found=true;
			}
		}
		assertEquals(true, found);	
	}
	
	@After
	public void clean() {
		driver.quit();
		try { statement.executeUpdate("DELETE FROM product WHERE name = 'test'; "); } catch (SQLException e) { throw new DbException(e.getMessage(), e); }
		try { statement.close(); connection.close(); } catch (SQLException e) { throw new DbException(e.getMessage(), e); }
	}

}

