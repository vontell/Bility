package org.vontech.androidserver.routing

import org.vontech.algorithms.rulebased.loggers.IssueReport
import org.vontech.core.interaction.UserAction

data class FrontendReportInfo(
        val automatonGraph: String?,
        val action: UserAction?,
        val numUnexplored: Int?,
        val issueReport: IssueReport?
)