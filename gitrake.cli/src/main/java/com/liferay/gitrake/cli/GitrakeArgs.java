package com.liferay.gitrake.cli;

import com.beust.jcommander.Parameter;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public class GitrakeArgs {

	public List<String> getFilePaths() {
		return _filePaths;
	}

	public int getLimit() {
		return _limit;
	}

	public int getPageSize() {
		return _pageSize;
	}

	public String getRepoPath() {
		return _repoPath;
	}

	protected boolean isHelp() {
		return _help;
	}

	@Parameter(
		description = "The list of file paths to search in closed PRs",
		help = true, names = {"-f", "--file-paths"}, required = true,
		variableArity = true
	)
	private List<String> _filePaths;

	@Parameter(
		description = "Print this message.", help = true,
		names = {"-h", "--help"}
	)
	private boolean _help;

	@Parameter(
		description = "The total limit of search results when searching prs.",
		help = true, names = {"-l", "--limit"}
	)
	private int _limit = 50;

	@Parameter(
		description = "The search page size when hitting github api.",
		help = true, names = {"-p", "--page-size"}
	)
	private int _pageSize = 10;

	@Parameter(
		description = "The repository path to search", help = true,
		names = {"-r", "--repo-path"}, required = true
	)
	private String _repoPath;

}