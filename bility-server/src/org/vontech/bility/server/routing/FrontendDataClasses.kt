package org.vontech.bility.server.routing

import org.vontech.bility.core.algorithms.rulebased.loggers.IssueReport
import org.vontech.bility.core.interaction.UserAction

data class FrontendReportInfo(
        val automatonGraph: String?,
        val action: UserAction?,
        val numUnexplored: Int?,
        val issueReport: IssueReport?
)