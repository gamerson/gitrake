package com.liferay.gitrake.cli;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;

public class Encrypt {

	public static void main(String[] args) {
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		String encryptedPassword = passwordEncryptor.encryptPassword("Dlc2010gg");
		System.out.println(encryptedPassword);


		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();

	}

}
