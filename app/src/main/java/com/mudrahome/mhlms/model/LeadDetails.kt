package com.mudrahome.mhlms.model

import java.util.ArrayList
import java.util.Collections


class LeadDetails {
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
    var assignerUId: String? = null
    var assignDate: String? = null
    var assignTime: String? = null
    var businessAssociateUid: String?=null;

    var telecallerRemarks = ArrayList<String>()
    var salesmanReason = ArrayList<String>()
    var timeStamp: Long = 0

    var banks: List<String> = ArrayList()
}
