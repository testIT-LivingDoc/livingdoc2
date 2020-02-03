package org.livingdoc.repositories.rest

class InvalidCacheUseConfigurationException(use: String) : RuntimeException("Invalid use in configuration: $use")
