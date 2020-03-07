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
class GitFileResolver(private val repository: Repository) {
    /**
     * Open the file at path as an input stream
     *
     * @param path the path of the file to read
     *
     * @returns an input stream containing the contents of the file
     */
    fun resolve(path: String): InputStream {
        val ref = repository.findRef("HEAD")
        val commit = repository.parseCommit(ref.leaf.objectId)

        val treeWalk = TreeWalk.forPath(repository, path, commit.tree)
            ?: throw DocumentNotFoundException("Could not find document at $path")

        val blobId = treeWalk.getObjectId(0)

        try {
            return repository.open(blobId, Constants.OBJ_BLOB).openStream()
        } catch (e: IncorrectObjectTypeException) {
            throw DocumentNotFoundException("$path is a directory")
        }
    }
}
