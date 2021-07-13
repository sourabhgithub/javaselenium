package test_pack;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium_Start {

	static Logger log = Logger.getLogger(Selenium_Start.class.getName());
	// Webpage URL
	static String URL = "http://duckduckgo.com";

	// Webpage load variables
	static int MAX_WAIT = 30;
	static int MAX_RETRY = 10;

	// Webpage login credentials
	static String username = "user1";
	static String password = "password1";

	// # Node
	String NODE_TO_SEARCH = "su-esbapiappaprd02";

	static WebDriver driver;
	static DesiredCapabilities caps;
	static WebDriverWait wait;

	public static WebElement find_element(WebDriver driver, String id, String xpath) {
		/*
		 * 
		 * This function will check via xpath or id if an element exists or not
		 */
		try {
			if (!id.isEmpty()) {
				return driver.findElement(By.id(id));
			} else
				return driver.findElement(By.xpath(xpath));
		} catch (Exception e) {
			e.printStackTrace();
			return driver.findElement(By.xpath(""));
		}
	}

	public static void execute(String node_list, String node_status) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("disable-infobars");
		options.addArguments("--disable-gpu");
		//options.addArguments("--headless");
		caps = new DesiredCapabilities();
		caps.setCapability(ChromeOptions.CAPABILITY, options);
		options.merge(caps);
		try {
			driver = new ChromeDriver(options);
		} catch (Exception e) {
			log.info("driver is quit");
			driver.quit();
		}
		driver.get(URL);
		log.info("URL is entered");

		wait = new WebDriverWait(driver, MAX_WAIT);

		boolean found = false;
		int tries = 0;

		while (!found && tries < MAX_RETRY) {
			try {
				System.out.println("search for username");
				WebElement username_text = wait.until(ExpectedConditions
						.presenceOfElementLocated(By
								.xpath("'//*[@id='username']")));
				found = true;

				Thread.sleep(3000);

			} catch (Exception e) {
				tries += 1;
			}
			if (tries == MAX_RETRY) {
				driver.quit();
			}
		}
		log.info("username is visible on UI");
		try {
			WebElement username_box = find_element(driver, "username", "");
			username_box.clear();
			username_box.sendKeys(username);
			// LOG("Username is entered");
			log.info("username is entered");
			Thread.sleep(1000);

			WebElement password_box = find_element(driver, "passwd", "");
			password_box.clear();
			password_box.sendKeys(password);
			log.info("Password entered");
			Thread.sleep(1000);

			// LOG("Click Login Button")
			WebElement login_btn = find_element(driver, "",
					"//button[text()='Login']");
			login_btn.click();

			log.info("Wait for user to Log In");
			// The webdriver will wait for 50 seconds maximum for the logged in
			// page and its content to be loaded
			// 50 seconds = 10(MAX_RETRY) * 5
			int count = MAX_RETRY;

			while (true) {
				try {
					WebElement traffic = find_element(driver, "",
							"//div[@id='mainmenu-localtraffic']/a[text()='Local Traffic']");
					if (traffic.isDisplayed()) {
						// LOG("Logged in, Content loaded...")
						Thread.sleep(1000);
						break;
					} else {
						if (count > 0) {
							// LOG("Not yet logged in, still loading...")
							Thread.sleep(5000);
							count -= 1;
						} else {
							// LOG("Not able to login, quitting...")
							driver.quit();
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
			for (String node : Arrays.asList(node_list) ){
				driver.switchTo().defaultContent();
				driver.switchTo().frame(
						driver.findElement(By
								.xpath("//iframe[@id='contenframe']")));

				log.info("Enter the Node to be searched in the Node List");
				WebElement search_input = find_element(driver, "",
						"//input[@name='search_input']");
				search_input.clear();
				search_input.sendKeys(node);
				// LOG("{} entered".format(node))
				Thread.sleep(1000);

				log.info("Click on Search to search for the Node: " + node
						+ " in the Node List");
				WebElement search_btn = find_element(driver, " ",
						"//input[@name='search_button']");
				search_btn.click();
				Thread.sleep(3000);

				// LOG("Select the Node: '{}' in the Node List".format(node))
				WebElement node_chk = find_element(driver, " ",
						"//tr/td/input[@type='checkbox' and @value='/Integration_Services/{}']");
				if (node_chk.isDisplayed()) {
					node_chk.click();
					log.info("The node " + node + " found in the node list");
					Thread.sleep(1000);

					// LOG("Disable the Node: '{}' in the Node List".format(node))
					WebElement node_dis_btn = find_element(driver, "",
							"//tr/td/div/input[@type='submit' and @id='disable']");
					node_dis_btn.click();
					Thread.sleep(5000);
				}else{
					log.info("node is not found");
				}

				/*// LOG("Select the Node: '{}' in the Node List".format(node))
				node_chk = find_element(driver, "",
						"//tr/td/input[@type='checkbox' and @value='/Integration_Services/{}']");
				node_chk.click();
				Thread.sleep(1000);

				// LOG("Enable the Node: '{}' in the Node List".format(node))
				WebElement node_en_btn = find_element(driver, "",
						"//tr/td/div/input[@type='submit' and @id='enable']");
				node_en_btn.click();
				Thread.sleep(5000);*/

				// LOG("Logout")
				WebElement logout_btn = find_element(driver, "",
						"//div[@id='logout'/a[text()='Log out' and @id='logout_anchor']");
				logout_btn.click();
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			log.info("before closing driver Exception is catched !!!");
		} finally {
			driver.close();
		}
	}
	
	public static void main(String[] args) {
		execute(args[0], args[1]);
		//execute(Arrays.asList("1","2"), "demo");
	}
}
