package com.liferay.gitrake.cli;

import com.beust.jcommander.Parameter;

public class GitrakeArgs {

	protected boolean isHelp() {
		return _help;
	}

	@Parameter(
		description = "Print this message.", help = true,
		names = {"-h", "--help"}
	)
	private boolean _help;
}
