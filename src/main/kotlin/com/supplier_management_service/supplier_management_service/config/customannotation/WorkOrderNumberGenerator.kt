package com.supplier_management_service.supplier_management_service.config.customannotation

import com.supplier_management_service.supplier_management_service.models.WorkOrder
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import com.supplier_management_service.supplier_management_service.models.Sequence

@Component
class WorkOrderNumberGenerator(
    private val mongoTemplate: MongoTemplate
) : AbstractMongoEventListener<WorkOrder>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<WorkOrder>) {
        val workOrder = event.source
        if (workOrder.workOrderNumber.isNullOrEmpty()) {
            workOrder.workOrderNumber = "WO-${getNextSequence("workOrders")}"
        }
    }

    private fun getNextSequence(seqName: String): Int {
        val query = Query(Criteria.where("_id").`is`(seqName))
        val update = Update().inc("seq", 1)
        val sequence = mongoTemplate.findAndModify(query, update, Sequence::class.java)
            ?: mongoTemplate.save(Sequence(seqName, 1))

        return sequence.seq
    }
}

