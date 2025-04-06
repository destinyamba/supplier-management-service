package com.supplier_management_service

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class BasicSimulation : Simulation() {

    private val httpProtocol = http
        .baseUrl("http://localhost:6060")
        .acceptHeader("application/json")

    private val signinScenario = scenario("User Sign In")
        .exec(
            http("Sign In")
                .post("/api/v1/auth/signin")
                .header("Content-Type", "application/json")
                .body(StringBody("""{"email": "destiny.amba@gmail.com", "password": "Flower12."}"""))
                .check(status().shouldBe(200))
                .check(jsonPath("$.token").saveAs("authToken"))
        )
    private val authChain = exec(
        http("Sign In")
            .post("/api/v1/auth/signin")
            .header("Content-Type", "application/json")
            .body(StringBody("""{"email": "destiny.amba@gmail.com", "password": "Flower12."}"""))
            .check(status().shouldBe(200))
            .check(jsonPath("$.token").saveAs("authToken"))
    )

    private val getAllSuppliersScenario = exec(
        http("Get All Suppliers")
            .get("/api/v1/supplier/all")
            .header("Authorization", "Bearer #{authToken}")
            .check(status().shouldBe(200))
    )


    private val getSupplierByIdScenario = exec(
        http("Get a Supplier")
            .get("/api/v1/supplier/67b78a4e9468a167d8d1e2dc")
            .header("Authorization", "Bearer #{authToken}")
            .check(status().shouldBe(200))
    )

    private val searchSuppliersScenario = exec(
        http("NLP Search Suppliers")
            .get("/api/v1/supplier/nlp/search?query=get%20mesuppliers%20submitted%20requirements")
            .header("Authorization", "Bearer #{authToken}")
            .check(status().shouldBe(200))
    )

    private val getTotalSuppliersScenario = scenario("Get Total Suppliers")
        .exec(
            http("Get Total Suppliers")
                .get("/api/v1/metrics/total-suppliers")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getTotalWorkOrdersScenario = scenario("Get Total Work Orders")
        .exec(
            http("Get Total Work Orders")
                .get("/api/v1/metrics/total-work-orders")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getTotalUpcomingWorkOrdersScenario = scenario("Get Total Upcoming Work Orders")
        .exec(
            http("Get Total Upcoming Work Orders")
                .get("/api/v1/metrics/upcoming-work-orders")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getSupplierRequirementStatusCountScenario = scenario("Get Supplier Requirement Status Count")
        .exec(
            http("Get Supplier Requirement Status Count")
                .get("/api/v1/metrics/requirement-status")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getSupplierWorkStatusCountScenario = scenario("Get Supplier Work Status Count")
        .exec(
            http("Get Supplier Work Status Count")
                .get("/api/v1/metrics/work-status")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getSupplierContractTypeCountScenario = scenario("Get Supplier Contract Type Count")
        .exec(
            http("Get Supplier Contract Type Count")
                .get("/api/v1/metrics/contract-type")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getWorkOrderStatusCountScenario = scenario("Get Work Order Status Count")
        .exec(
            http("Get Work Order Status Count")
                .get("/api/v1/metrics/work-order-status")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getSuppliersOnboardedOverTimeScenario = scenario("Get Suppliers Onboarded Over Time")
        .exec(
            http("Get Suppliers Onboarded Over Time")
                .get("/api/v1/metrics/suppliers-onboarded-over-time")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getAverageWorkOrderCompletionScenario = scenario("Get Average Work Order Completion")
        .exec(
            http("Get Average Work Order Completion")
                .get("/api/v1/metrics/average-work-order-completion")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getWorkOrderByServiceScenario = scenario("Get Work Order By Service")
        .exec(
            http("Get Work Order By Service")
                .get("/api/v1/metrics/work-order-service")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )

    private val getSuppliersByServiceScenario = scenario("Get Suppliers By Service")
        .exec(
            http("Get Suppliers By Service")
                .get("/api/v1/metrics/suppliers-by-service")
                .queryParam("clientId", "exampleClientId")
                .check(status().shouldBe(200))
        )
    private val supplierListScenario = scenario("List Suppliers")
        .exec(authChain)
        .exec(getAllSuppliersScenario)

    private val nlpSearchSupplierListScenario = scenario("NLP Search Suppliers")
        .exec(authChain)
        .exec(searchSuppliersScenario)

    private val supplierScenario = scenario("Get a Supplier")
        .exec(authChain)
        .exec(getSupplierByIdScenario)

    init {
        setUp(
            signinScenario.injectOpen(rampUsers(100).during(60)),
            supplierListScenario.injectOpen(rampUsers(100).during(60)),
            nlpSearchSupplierListScenario.injectOpen(rampUsers(100).during(60)),
            supplierScenario.injectOpen(rampUsers(100).during(60)),
            getTotalSuppliersScenario.injectOpen(rampUsers(100).during(60)),
            getTotalWorkOrdersScenario.injectOpen(rampUsers(100).during(60)),
            getTotalUpcomingWorkOrdersScenario.injectOpen(rampUsers(100).during(60)),
            getSupplierRequirementStatusCountScenario.injectOpen(rampUsers(100).during(60)),
            getSupplierWorkStatusCountScenario.injectOpen(rampUsers(100).during(60)),
            getSupplierContractTypeCountScenario.injectOpen(rampUsers(100).during(60)),
            getWorkOrderStatusCountScenario.injectOpen(rampUsers(100).during(60)),
            getSuppliersOnboardedOverTimeScenario.injectOpen(rampUsers(100).during(60)),
            getAverageWorkOrderCompletionScenario.injectOpen(rampUsers(100).during(60)),
            getWorkOrderByServiceScenario.injectOpen(rampUsers(100).during(60)),
            getSuppliersByServiceScenario.injectOpen(rampUsers(100).during(60))

        ).protocols(httpProtocol)
    }
}
