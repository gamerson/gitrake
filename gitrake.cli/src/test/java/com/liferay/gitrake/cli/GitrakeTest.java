package com.liferay.gitrake.cli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;

public class GitrakeTest {

	@Test
	public void testGithubAPI() throws Exception {
		RepositoryService service = new RepositoryService();
		service.getClient().setCredentials("gamerson", "Dlc2010gg");

		Repository bchanPortalRepo = service.getRepository("brianchandotcom", "liferay-portal");

		assertNotNull(bchanPortalRepo);

		PullRequestService prService = new PullRequestService();
		prService.getClient().setCredentials("gamerson", "Dlc2010gg");

		PageIterator<PullRequest> prs = prService.pagePullRequests(bchanPortalRepo, "closed", 50);

		assertNotNull(prs);

		List<PullRequest> interestingPrs = prs.iterator().next().stream()
			.filter(pr -> isInterestingPr(bchanPortalRepo, prService, pr))
			.collect(Collectors.toList());

		assertNotNull(interestingPrs);

		assertTrue(interestingPrs.size() > 0);
	}

	private boolean isInterestingPr(Repository repo, PullRequestService prService, PullRequest pr) {
		try {
			return prService.getFiles(repo, pr.getNumber()).stream().filter(file -> file.getFilename().startsWith("modules/sdk")).count() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
