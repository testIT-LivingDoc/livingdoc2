package org.livingdoc.repositories.cache

/**
 * InvalidCachePolicyException is thrown when the policy statement in
 * the cache configuration does not have a valid value.
 */
class InvalidCachePolicyException(policy: String) :
    RuntimeException("Invalid policy statement in configuration: $policy")
