package com.jayfella.sdk.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitUtils {

    public boolean cloneRepository(String gitUri, File dest) {

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(gitUri)
                .setDirectory(dest);

        try (Git git = cloneCommand.call()) {

        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
