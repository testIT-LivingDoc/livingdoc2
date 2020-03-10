package org.livingdoc.repositories.git

import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.treewalk.TreeWalk
import org.livingdoc.repositories.DocumentNotFoundException
import java.io.InputStream

/**
 * A GitFileResolver can resolve file paths in git repositories
 */
internal class GitFileResolver(private val repository: Repository) {
    /**
     * Open the file at path as an input stream
     *
     * @param path the path of the file to read
     *
     * @returns an input stream containing the contents of the file
     */
    fun resolve(identifier: GitDocumentIdentifier): InputStream {
        val commitId = repository.resolve(identifier.revision)
            ?: throw DocumentNotFoundException("Could not find revision ${identifier.revision}")
        val commit = repository.parseCommit(commitId)

        val treeWalk = TreeWalk.forPath(repository, identifier.path, commit.tree)
            ?: throw DocumentNotFoundException("Could not find document at ${identifier.path}")

        val blobId = treeWalk.getObjectId(0)

        try {
            return repository.open(blobId, Constants.OBJ_BLOB).openStream()
        } catch (e: IncorrectObjectTypeException) {
            throw DocumentNotFoundException("${identifier.path} is a directory")
        }
    }
}
