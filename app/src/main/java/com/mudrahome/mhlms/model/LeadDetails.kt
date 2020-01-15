package com.mudrahome.mhlms.model

import java.io.Serializable
import java.util.*


class LeadDetails : Serializable {
    var name: String? = null
    var contactNumber: String? = null
    var loanAmount: String? = null
    var loanType: String? = null
    var propertyType: String? = null
    var employment: String? = null
    var employmentType: String? = null
    var location: String? = null
    var salesmanRemarks: String? = null
    var assignedTo: String? = null
    var status: String? = null
    var key: String? = null
    var assigner: String? = null
    var assignedToUId: String? = null
    var forwarderUId: String? = null
    var forwarderName: String? = null
    var assignerUId: String? = null
    var assignDate: String? = null
    var assignTime: String? = null
    var businessAssociateUploader: Boolean? = null
    var businessAssociateUid: String? = null

    var telecallerRemarks = ArrayList<String>()
    var salesmanReason = ArrayList<String>()
    var forwarderRemarks = ArrayList<String>()
    var timeStamp: Long = 0

    var banks: List<String> = ArrayList()
}











//package com.mudrahome.mhlms.model

//import java.util.*
//
//
//class LeadDetails {
//    var name = "Not Provided"
//    var contactNumber: String? = null
//    var loanAmount = "Not Provided"
//    var loanType = "Not Provided"
//    var propertyType = "Not Provided"
//    var employment = "Not Provided"
//    var employmentType = "Not Provided"
//    var location = "Not Provided"
//    var salesmanRemarks = "Not Assigned"
//    var assignedTo = "Not Assigned"
//    var status = "Incomplete Lead"
//    var key: String? = null
//    var assigner = "Not Assigned"
//    var assignedToUId: String? = null
//    var forwarderUId: String? = null
//    var forwarderName = "Not Assigned"
//    var assignerUId: String? = null
//    var assignDate = "Not Assigned"
//    var assignTime = "Not Assigned"
//
//    var telecallerRemarks = ArrayList<String>()
//    var salesmanReason = ArrayList<String>()
//    var forwarderRemarks = ArrayList<String>()
//    var timeStamp: Long = 0
//
//    var banks: List<String> = ArrayList()
//}
