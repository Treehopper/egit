package org.eclipse.egit.gitflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitFlowRepository {
	private static final String MASTER_KEY = "master";
	private static final String DEVELOP_KEY = "develop";
	private static final String HOTFIX_KEY = "hotfix";
	private static final String RELEASE_KEY = "release";
	private static final String FEATURE_KEY = "feature";
	private static final String USER_SECTION = "user";
	private static final String BRANCH_SECTION = "branch";
	private static final String PREFIX_SECTION = "prefix";
	private static final String GITFLOW_SECTION = "gitflow";
	private Repository repository;

	public GitFlowRepository(Repository repository) {
		this.repository = repository;
	}

	public boolean hasBranches() {
		List<Ref> branches;
		try {
			branches = Git.wrap(repository).branchList().call();
			return !branches.isEmpty();
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasBranch(String branch) throws GitAPIException {
		String fullBranchName = Constants.R_HEADS + branch;
		List<Ref> branchList = Git.wrap(repository).branchList().call();
		for (Ref ref : branchList) {
			if (fullBranchName.equals(ref.getTarget().getName())) {
				return true;
			}
		}

		return false;
	}

	public Ref findBranch(String branchName) throws IOException {
		return repository.getRef(Constants.R_HEADS + branchName);
	}

	public boolean isInitialized() throws IOException {
		StoredConfig config = repository.getConfig();
		Set<String> sections = config.getSections();
		return sections.contains(GITFLOW_SECTION);
	}

	public boolean isFeature() throws IOException {
		return repository.getBranch().startsWith(getFeaturePrefix());
	}

	public boolean isDevelop() throws IOException {
		return repository.getBranch().equals(getDevelop());
	}

	public boolean isMaster() throws IOException {
		return repository.getBranch().equals(getMaster());
	}

	public boolean isRelease() throws IOException {
		return repository.getBranch().startsWith(getReleasePrefix());
	}

	public boolean isHotfix() throws IOException {
		return repository.getBranch().startsWith(getHotfixPrefix());
	}

	public String getUser() {
		StoredConfig config = repository.getConfig();
		String userName = config.getString(USER_SECTION, null, "name");
		String email = config.getString(USER_SECTION, null, "email");
		return String.format("%s <%s>", userName, email);
	}

	public String getFeaturePrefix() {
		return getPrefix(FEATURE_KEY, GitFlowDefaults.FEATURE_PREFIX);
	}

	public String getReleasePrefix() {
		return getPrefix(RELEASE_KEY, GitFlowDefaults.RELEASE_PREFIX);
	}

	public String getHotfixPrefix() {
		return getPrefix(HOTFIX_KEY, GitFlowDefaults.HOTFIX_PREFIX);
	}

	public String getDevelop() {
		return getBranch(DEVELOP_KEY, GitFlowDefaults.DEVELOP);
	}

	public String getMaster() {
		return getBranch(MASTER_KEY, GitFlowDefaults.MASTER);
	}

	public String getDevelopFull() {
		return Constants.R_HEADS + getDevelop();
	}

	public String getPrefix(String prefixName, String defaultPrefix) {
		StoredConfig config = repository.getConfig();
		String result = config.getString(GITFLOW_SECTION, PREFIX_SECTION, prefixName);
		return (result == null) ? defaultPrefix : result;
	}

	public String getBranch(String branch, String defaultBranch) {
		StoredConfig config = repository.getConfig();
		String result = config.getString(GITFLOW_SECTION, BRANCH_SECTION, branch);
		return (result == null) ? defaultBranch : result;
	}

	public void setPrefix(String prefixName, String value) {
		StoredConfig config = repository.getConfig();
		config.setString(GITFLOW_SECTION, PREFIX_SECTION, prefixName, value);
	}

	public void setBranch(String branchName, String value) {
		StoredConfig config = repository.getConfig();
		config.setString(GITFLOW_SECTION, BRANCH_SECTION, branchName, value);
	}

	public RevCommit findHead() {
		RevWalk walk = new RevWalk(repository);

		try {
			ObjectId head = repository.resolve(Constants.HEAD);
			return walk.parseCommit(head);
		} catch (RevisionSyntaxException e) {
			throw new RuntimeException(e);
		} catch (AmbiguousObjectException e) {
			throw new RuntimeException(e);
		} catch (IncorrectObjectTypeException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RevCommit findHead(String branchName) {
		RevWalk walk = new RevWalk(repository);

		try {
			ObjectId head = repository.resolve(Constants.R_HEADS + branchName);
			return walk.parseCommit(head);
		} catch (RevisionSyntaxException e) {
			throw new RuntimeException(e);
		} catch (AmbiguousObjectException e) {
			throw new RuntimeException(e);
		} catch (IncorrectObjectTypeException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getFullFeatureBranchName(String featureName) {
		return Constants.R_HEADS + getFeatureBranchName(featureName);
	}

	public String getFeatureBranchName(String featureName) {
		return getFeaturePrefix() + featureName;
	}

	public String getHotfixBranchName(String hotfixName) {
		return getHotfixPrefix() + hotfixName;
	}

	public String getFullHotfixBranchName(String hotfixName) {
		return Constants.R_HEADS + getHotfixBranchName(hotfixName);
	}

	public String getFullReleaseBranchName(String releaseName) {
		return Constants.R_HEADS + getReleaseBranchName(releaseName);
	}

	public String getReleaseBranchName(String releaseName) {
		return getReleasePrefix() + releaseName;
	}

	public Repository getRepository() {
		return repository;
	}

	public List<Ref> getFeatureBranches() {
		return getPrefixBranches(Constants.R_HEADS + getFeaturePrefix());
	}

	public List<Ref> getReleaseBranches() {
		return getPrefixBranches(Constants.R_HEADS + getReleasePrefix());
	}

	public List<Ref> getHotfixBranches() {
		return getPrefixBranches(Constants.R_HEADS + getHotfixPrefix());
	}

	private List<Ref> getPrefixBranches(String prefix) {
		try {
			List<Ref> branches = Git.wrap(repository).branchList().call();
			List<Ref> prefixBranches = new ArrayList<Ref>();
			for (Ref ref : branches) {
				if (ref.getName().startsWith(prefix)) {
					prefixBranches.add(ref);
				}
			}

			return prefixBranches;
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

}
