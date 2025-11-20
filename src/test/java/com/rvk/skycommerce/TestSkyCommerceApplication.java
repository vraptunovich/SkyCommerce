package com.rvk.skycommerce;

import org.springframework.boot.SpringApplication;

public class TestSkyCommerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(SkyCommerceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
