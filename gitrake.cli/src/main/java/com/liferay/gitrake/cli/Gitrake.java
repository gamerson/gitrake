package com.liferay.gitrake.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.liferay.gitrake.cli.internal.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * @author Gregory Amerson
 */
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

	public Gitrake(GitrakeArgs gitrakeArgs) throws IOException {
		File configFile = _getConfig();

		if (!configFile.exists()) {
			throw new IllegalStateException(
				"\n\n~/.gitrake.properties file does not exist. Please " +
					"create and add following entry:\n" +
						"\ttoken=<github_api_token>\n");
		}

		String repoPath = gitrakeArgs.getRepoPath();
		String filePath = gitrakeArgs.getFilePath();
		int pageSize = gitrakeArgs.getPageSize();
		int limit = gitrakeArgs.getLimit();

		RepositoryService repoService = new RepositoryService();

		_configureClient(repoService.getClient());

		String[] repoParts = repoPath.split("\\/");

		if (repoParts.length != 2) {
			throw new IllegalArgumentException(
				"repoPath must be in form of <user>/<repo>");
		}

		_repo = repoService.getRepository(repoParts[0], repoParts[1]);

		if (_repo == null) {
			throw new IllegalStateException(
				"Could not get repo at path " + repoPath);
		}

		_prService = new PullRequestService();

		_configureClient(_prService.getClient());

		List<PullRequest> interestingPrs = _findInterestingPrs(
			filePath, pageSize, limit);

		if (!interestingPrs.isEmpty()) {
			System.out.println(
				"\nListing interesting prs on " + _green(repoPath) + "\n");
		}

		for (PullRequest pr : interestingPrs) {
			StringBuilder sb = new StringBuilder();

			sb.append(_green(Integer.toString(pr.getNumber())));
			sb.append(" ");
			sb.append(pr.getTitle());
			sb.append(" ");
			sb.append(_green(pr.getUser().getLogin()));
			sb.append(" ");
			sb.append(_blue(pr.getHtmlUrl()));

			System.out.println(sb.toString());

			List<CommitFile> files = _prService.getFiles(_repo, pr.getNumber());

			files.stream().forEach(
				file -> System.out.println("\t" + file.getFilename()));

			System.out.println();
		}
	}

	private static void _printHelp(JCommander jCommander) throws Exception {
		System.out.println();
		jCommander.usage();
	}

	private String _blue(String msg) {
		//return "\\x1b[31m" + msg + "\\x1b[0m";
		return msg;
	}

	private void _configureClient(GitHubClient client) {
		try (FileInputStream fin = new FileInputStream(_getConfig())) {
			Properties props = new Properties();

			props.load(fin);
			String token = props.getProperty("token");

			client.setOAuth2Token(token);
		}
		catch (Exception e) {
		}
	}

	private List<PullRequest> _findInterestingPrs(
			String filePath, int pageSize, int limit)
		throws IOException {

		PageIterator<PullRequest> prs = _prService.pagePullRequests(
			_repo, "closed", pageSize);

		List<PullRequest> interestingPrs = new ArrayList<>();

		for (int i = 0; i < limit; i += pageSize) {
			List<PullRequest> foundPrs = prs.iterator().next().stream()
				.filter(pr -> _isInterestingPr(_repo, _prService, pr, filePath))
				.collect(Collectors.toList());

			if (foundPrs != null) {
				interestingPrs.addAll(foundPrs);
			}
		}

		return interestingPrs;
	}

	private File _getConfig() {
		return new File(
			System.getProperty("user.home") + "/.gitrake.properties");
	}

	private String _green(String msg) {
		//return "\\x1b[32m" + msg + "\\x1b[0m";
		return msg;
	}

	private boolean _isInterestingFile(CommitFile file, String filePath) {
		if ((file != null) && (file.getFilename() != null) &&
			file.getFilename().startsWith(filePath)) {

			return true;
		}

		return false;
	}

	private boolean _isInterestingPr(
		Repository repo, PullRequestService prService, PullRequest pr,
		String filePath) {

		System.out.println(
			"Searching pr #" + pr.getNumber() + " " + pr.getTitle());

		try {
			List<CommitFile> files = prService.getFiles(repo, pr.getNumber());

			if (files.stream()
					.filter(file -> _isInterestingFile(file, filePath))
						.count() > 0) {

				return true;
			}

			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private final PullRequestService _prService;
	private final Repository _repo;

}