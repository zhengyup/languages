package com.huskie.languages.exception.scenario

class IncompleteVocabularyCoverageException(
    lineOrder: Int
) : RuntimeException("Scenario line $lineOrder does not have complete vocabulary coverage")
