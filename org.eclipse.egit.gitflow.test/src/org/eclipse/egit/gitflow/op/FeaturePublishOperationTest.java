/*******************************************************************************
 * Copyright (C) 2015, Max Hohenegger <eclipse@hohenegger.eu>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.gitflow.op;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.op.BranchOperation;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.test.DualRepositoryTestCase;
import org.eclipse.egit.core.test.TestRepository;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class FeaturePublishOperationTest extends DualRepositoryTestCase {
	private static final String MY_FEATURE = "myFeature";
	private static final String FEATURE_PREFIX = "feature";
	private static final String SEP = "/";

	private File workdir;
	private File workdir2;

	String projectName = "FeaturePublishTest";

	@Before
	public void beforeTestCase() throws Exception {
		workdir = testUtils.createTempDir("Repository1");
		workdir2 = testUtils.createTempDir("Repository2");

		repository1 = new TestRepository(new File(workdir, Constants.DOT_GIT));

		repository1.createInitialCommit("setUp");

		Repository repository = repository1.getRepository();
		new InitOperation(repository).execute(null);

		// now we create a project in repo1
		IProject project = testUtils.createProjectInLocalFileSystem(workdir, projectName);
		testUtils.addFileToProject(project, "folder1/file1.txt", "Hello world");

		repository1.connect(project);
		repository1.trackAllFiles(project);
		repository1.commit("Initial commit");

		// let's get rid of the project
		project.delete(false, false, null);

		// let's clone repository1 to repository2
		URIish uri = repository1.getUri();
		CloneOperation clop = new CloneOperation(uri, true, null, workdir2, "refs/heads/master", "origin", 0);
		clop.run(null);

		Repository repo2 = Activator.getDefault().getRepositoryCache()
				.lookupRepository(new File(workdir2, Constants.DOT_GIT));
		repository2 = new TestRepository(repo2);

	}

	@Test
	public void testFeaturePublish() throws Exception {
		new FeatureStartOperation(repository2.getRepository(), MY_FEATURE).execute(null);
		RevCommit branchCommit = repository2.createInitialCommit("testFeaturePublish");
		new FeaturePublishOperation(repository2.getRepository(), MY_FEATURE).execute(null);
		assertCommitArrivedAtRemote(branchCommit, repository1.getRepository());

		new FeatureFinishOperation(repository1.getRepository(), MY_FEATURE);
	}

	private void assertCommitArrivedAtRemote(RevCommit branchCommit, Repository remote) throws CoreException {
		BranchOperation checkoutOperation = new BranchOperation(remote,
				getFullFeatureBranchName(MY_FEATURE));
		checkoutOperation.execute(null);
		RevCommit developHead = findHead(remote);
		assertEquals(branchCommit, developHead);
	}

	protected RevCommit findHead(Repository repo) {
		RevWalk walk = new RevWalk(repo);

		try {
			ObjectId head = repo.resolve(Constants.HEAD);
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

	public static String getFullFeatureBranchName(String featureName) {
		return Constants.R_HEADS + getFeatureBranchName(featureName);
	}

	private static String getFeatureBranchName(String featureName) {
		return FEATURE_PREFIX + SEP + featureName;
	}
}
