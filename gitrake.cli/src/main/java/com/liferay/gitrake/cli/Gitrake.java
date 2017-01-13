package com.liferay.gitrake.cli;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.liferay.gitrake.cli.internal.FileUtil;

public class Gitrake {

	public static void main(String[] args) throws Exception {
		GitrakeArgs gitrakeArgs = new GitrakeArgs();

		JCommander jCommander = new JCommander(gitrakeArgs);

		try {
			File jarFile = FileUtil.getJarFile();

			if (jarFile.isFile()) {
				jCommander.setProgramName("java -jar " + jarFile.getName());
			}
			else {
				jCommander.setProgramName("gitrake");
			}

			jCommander.parse(args);

			if (gitrakeArgs.isHelp()) {
				_printHelp(jCommander);
			}
			else {
				new Gitrake(gitrakeArgs);
			}
		}
		catch (ParameterException pe) {
			System.err.println(pe.getMessage());

			_printHelp(jCommander);
		}
	}

	public Gitrake(GitrakeArgs gitrakeArgs) {
		// TODO Auto-generated constructor stub
	}

	private static void _printHelp(JCommander jCommander) throws Exception {
		System.out.println();
		jCommander.usage();
	}
}
