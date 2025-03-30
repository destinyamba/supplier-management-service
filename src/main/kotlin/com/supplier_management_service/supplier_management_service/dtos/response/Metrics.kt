package com.supplier_management_service.supplier_management_service.dtos.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class RequirementStatusMetric(
    val pending: Int,
    val submitted: Int
)

data class WorkStatusMetric(
    @JsonProperty("approved") val approved: Int,
    @JsonProperty("notApproved") val notApproved: Int
)

data class ContractTypeMetric(
    val direct: Int,
    val subcontracted: Int
)

data class WorkOrderStatusMetric(
    val pending: Int,
    val completed: Int,
    val inProgress: Int,
    val cancelled: Int,
)

data class OnboardingTrend(
    val date: LocalDate,
    val count: Int
)

data class AverageCompletionTrend(
    val date: LocalDate,
    val averageCompletionTime: Double
)

data class ServiceTypeMetric(
    @JsonProperty("serviceType") val serviceType: String,
    @JsonProperty("count") val count: Int
)