package com.acme.salary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class SalaryManagementApplication {

	public static void main(String[] args) {
		ensureDatabaseDirectoryExists();
		SpringApplication.run(SalaryManagementApplication.class, args);
	}

	/**
	 * The SQLite driver creates the database file itself but not missing
	 * parent directories, so a fresh checkout or container volume would
	 * otherwise fail on first run with "unable to open database file".
	 */
	private static void ensureDatabaseDirectoryExists() {
		String dbPath = System.getenv().getOrDefault("SALARY_DB_PATH", "./data/salary.db");
		Path parent = Path.of(dbPath).toAbsolutePath().normalize().getParent();
		if (parent != null) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				throw new UncheckedIOException("Could not create database directory: " + parent, e);
			}
		}
	}

}
