package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.dtos.response.WOPagedResponse
import com.supplier_management_service.supplier_management_service.dtos.response.WOResponse
import com.supplier_management_service.supplier_management_service.enums.ContractStatus
import com.supplier_management_service.supplier_management_service.enums.Region
import com.supplier_management_service.supplier_management_service.enums.SupplyChainService
import com.supplier_management_service.supplier_management_service.models.WorkOrder
import com.supplier_management_service.supplier_management_service.repositories.SupplierRepository
import com.supplier_management_service.supplier_management_service.repositories.WorkOrderRepository
import org.springframework.stereotype.Service
import java.util.Date

@Service
class WorkOrderService(private val workOrderRepository: WorkOrderRepository, private val supplierRepository: SupplierRepository) {
    // create WO
    fun createWorkOrder(workOrder: WorkOrder): WorkOrder {
        // convert util.Date to sql.Date for dueDate and startDate
        val sqlDueDate = convertUtilDateToSqlDate(workOrder.dueDate)
        val sqlStartDate = convertUtilDateToSqlDate(workOrder.startDate)

        // create a new WorkOrder object with the converted dates
        val newWorkOrder = sqlDueDate?.let { workOrder.copy(dueDate = it, startDate = sqlStartDate!!) }

        return workOrderRepository.save(newWorkOrder!!)
    }

    // view one WO
    fun getWorkOrderById(id: String): WorkOrder {
        return workOrderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Work Order not found") }
    }

    // view list of WOs by client ID
    fun listOfWOs(pageNum: Int, pageSize: Int, clientId: String): WOPagedResponse<WOResponse> {
        val allWOs = workOrderRepository.findByClientId(clientId)

        // Convert dates safely (avoiding nulls in the list)
        val convertedWOs = allWOs.mapNotNull { wo ->
            val sqlDueDate = convertUtilDateToSqlDate(wo.dueDate)
            val sqlStartDate = convertUtilDateToSqlDate(wo.startDate)

            if (sqlDueDate != null && sqlStartDate != null) {
                wo.copy(dueDate = sqlDueDate, startDate = sqlStartDate)
            } else {
                null
            }
        }

        // Split the list into IN_PROGRESS and other items
        val inProgressWOs = convertedWOs.filter { it.status == ContractStatus.IN_PROGRESS }
        val pendingWOs = convertedWOs.filter { it.status == ContractStatus.PENDING }
        val otherWOs = convertedWOs.filter { it.status != ContractStatus.IN_PROGRESS && it.status != ContractStatus.PENDING }

        // Concatenate the lists to prioritize IN_PROGRESS
        val sortedWOs = inProgressWOs + pendingWOs + otherWOs

        // Pagination logic
        val totalWOs = sortedWOs.size
        val totalPages = (totalWOs + pageSize - 1) / pageSize
        val startIndex = (pageNum - 1) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(totalWOs)

        val paginatedWOs = if (startIndex < totalWOs) {
            sortedWOs.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        val wosResponse = paginatedWOs.map { WOResponse(it) }

        return WOPagedResponse(
            wos = wosResponse,
            page = pageNum,
            pageSize = pageSize,
            totalItems = totalWOs,
            totalPages = totalPages
        )
    }

    // void WO
    fun voidWO(clientId: String, woId: String) {
        val wo = workOrderRepository.findById(woId).orElseThrow { IllegalArgumentException("Work Order not found.") }
        wo.status = ContractStatus.CANCELLED
        workOrderRepository.save(wo)
    }

    // get a list of work orders that match supplier service
    // take in the supplier id, if the suppliers list of services contains the work order service it should add to the list.
    fun getWorkOrdersBySupplierService(pageNum: Int, pageSize: Int, supplierId: String): WOPagedResponse<WOResponse> {
        val supplierServices = getSupplierServices(supplierId).toSet()
        val allWOs = workOrderRepository.findByServiceIn(supplierServices)

        // Pagination logic
        val totalWOs = allWOs.size
        val totalPages = (totalWOs + pageSize - 1) / pageSize
        val startIndex = (pageNum - 1) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(totalWOs)

        val paginatedWOs = if (startIndex < totalWOs) {
            allWOs.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        val wosResponse = paginatedWOs.map { WOResponse(it) }

        return WOPagedResponse(
            wos = wosResponse,
            page = pageNum,
            pageSize = pageSize,
            totalItems = totalWOs,
            totalPages = totalPages
        )
    }

    private fun getSupplierServices(supplierId: String): List<String> {
        val supplier = supplierRepository.findById(supplierId)
            .orElseThrow { IllegalArgumentException("Supplier not found") }
        return supplier.services
    }


    // return list of locations
    fun getAllRegions(): List<Region> = RegionData.regions

    fun getServices(): List<SupplyChainService> = ServiceData.supplyChainServices

    object RegionData {
        val regions = listOf(
            Region("Avon", "AVN", "England"),
            Region("Bedfordshire", "BDF", "England"),
            Region("Berkshire", "BRK", "England"),
            Region("Buckinghamshire", "BKM", "England"),
            Region("Cambridgeshire", "CAM", "England"),
            Region("Cheshire", "CHS", "England"),
            Region("Cleveland", "CLV", "England"),
            Region("Cornwall", "CON", "England"),
            Region("Cumbria", "CMA", "England"),
            Region("Derbyshire", "DBY", "England"),
            Region("Devon", "DEV", "England"),
            Region("Dorset", "DOR", "England"),
            Region("Durham", "DUR", "England"),
            Region("East Sussex", "SXE", "England"),
            Region("Essex", "ESS", "England"),
            Region("Gloucestershire", "GLS", "England"),
            Region("Hampshire", "HAM", "England"),
            Region("Herefordshire", "HEF", "England"),
            Region("Hertfordshire", "HRT", "England"),
            Region("Isle of Wight", "IOW", "England"),
            Region("Kent", "KEN", "England"),
            Region("Lancashire", "LAN", "England"),
            Region("Leicestershire", "LEI", "England"),
            Region("Lincolnshire", "LIN", "England"),
            Region("London", "LDN", "England"),
            Region("Merseyside", "MSY", "England"),
            Region("Norfolk", "NFK", "England"),
            Region("Northamptonshire", "NTH", "England"),
            Region("Northumberland", "NBL", "England"),
            Region("North Yorkshire", "NYK", "England"),
            Region("Nottinghamshire", "NTT", "England"),
            Region("Oxfordshire", "OXF", "England"),
            Region("Rutland", "RUT", "England"),
            Region("Shropshire", "SAL", "England"),
            Region("Somerset", "SOM", "England"),
            Region("South Yorkshire", "SYK", "England"),
            Region("Staffordshire", "STS", "England"),
            Region("Suffolk", "SFK", "England"),
            Region("Surrey", "SRY", "England"),
            Region("Tyne and Wear", "TWR", "England"),
            Region("Warwickshire", "WAR", "England"),
            Region("West Midlands", "WMD", "England"),
            Region("West Sussex", "SXW", "England"),
            Region("West Yorkshire", "WYK", "England"),
            Region("Wiltshire", "WIL", "England"),
            Region("Worcestershire", "WOR", "England"),
            Region("Clwyd", "CWD", "Wales"),
            Region("Dyfed", "DFD", "Wales"),
            Region("Gwent", "GNT", "Wales"),
            Region("Gwynedd", "GWN", "Wales"),
            Region("Mid Glamorgan", "MGM", "Wales"),
            Region("Powys", "POW", "Wales"),
            Region("South Glamorgan", "SGM", "Wales"),
            Region("West Glamorgan", "WGM", "Wales"),
            Region("Aberdeenshire", "ABD", "Scotland"),
            Region("Angus", "ANS", "Scotland"),
            Region("Argyll", "ARL", "Scotland"),
            Region("Ayrshire", "AYR", "Scotland"),
            Region("Banffshire", "BAN", "Scotland"),
            Region("Berwickshire", "BEW", "Scotland"),
            Region("Bute", "BUT", "Scotland"),
            Region("Caithness", "CAI", "Scotland"),
            Region("Clackmannanshire", "CLK", "Scotland"),
            Region("Dumfriesshire", "DGY", "Scotland"),
            Region("Dunbartonshire", "DNB", "Scotland"),
            Region("East Lothian", "ELN", "Scotland"),
            Region("Fife", "FIF", "Scotland"),
            Region("Inverness-shire", "INV", "Scotland"),
            Region("Kincardineshire", "KCD", "Scotland"),
            Region("Kinross-shire", "KRS", "Scotland"),
            Region("Kirkcudbrightshire", "KKD", "Scotland"),
            Region("Lanarkshire", "LKS", "Scotland"),
            Region("Midlothian", "MLN", "Scotland"),
            Region("Moray", "MOR", "Scotland"),
            Region("Nairnshire", "NAI", "Scotland"),
            Region("Orkney", "OKI", "Scotland"),
            Region("Peeblesshire", "PEE", "Scotland"),
            Region("Perthshire", "PER", "Scotland"),
            Region("Renfrewshire", "RFW", "Scotland"),
            Region("Ross-shire", "ROC", "Scotland"),
            Region("Roxburghshire", "ROX", "Scotland"),
            Region("Selkirkshire", "SEL", "Scotland"),
            Region("Shetland", "SHI", "Scotland"),
            Region("Stirlingshire", "STI", "Scotland"),
            Region("Sutherland", "SUT", "Scotland"),
            Region("West Lothian", "WLN", "Scotland"),
            Region("Wigtownshire", "WIG", "Scotland"),
            Region("Antrim", "ANT", "Northern Ireland"),
            Region("Armagh", "ARM", "Northern Ireland"),
            Region("Down", "DOW", "Northern Ireland"),
            Region("Fermanagh", "FER", "Northern Ireland"),
            Region("Londonderry", "LDY", "Northern Ireland"),
            Region("Tyrone", "TYR", "Northern Ireland")
        )
    }

    object ServiceData {
        val supplyChainServices = listOf(
            SupplyChainService("Warehousing", "Storage"),
            SupplyChainService("Inventory Management", "Storage"),
            SupplyChainService("Freight Forwarding", "Transportation"),
            SupplyChainService("Customs Clearance", "Compliance"),
            SupplyChainService("Procurement", "Sourcing"),
            SupplyChainService("Supplier Auditing", "Compliance"),
            SupplyChainService("Packaging and Labeling", "Processing"),
            SupplyChainService("Last-Mile Delivery", "Transportation"),
            SupplyChainService("Reverse Logistics", "Returns"),
            SupplyChainService("Demand Planning", "Planning"),
            SupplyChainService("Route Optimization", "Technology"),
            SupplyChainService("Cold Chain Logistics", "Specialized"),
            SupplyChainService("E-Commerce Fulfillment", "Fulfillment"),
            SupplyChainService("Drop Shipping", "Fulfillment"),
            SupplyChainService("Quality Assurance", "Inspection"),
            SupplyChainService("Supply Chain Analytics", "Technology"),
            SupplyChainService("Sustainability Consulting", "Consulting"),
            SupplyChainService("Fleet Management", "Transportation"),
            SupplyChainService("Custom Product Assembly", "Processing"),
            SupplyChainService("Integrated Logistics Solutions", "Comprehensive")
        )
    }

    private fun convertUtilDateToSqlDate(utilDate: Date?): java.sql.Date? {
        return utilDate?.let { java.sql.Date(it.time) }
    }
}
