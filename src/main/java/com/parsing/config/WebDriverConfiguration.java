package com.parsing.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfiguration {

    @Bean
    public WebDriver createWebDriver() {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options= new ChromeOptions();
        options.setHeadless(true);
        return new ChromeDriver();
    }
}
