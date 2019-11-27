package com.mudrahome.mhlms.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.model.*
import com.mudrahome.mhlms.sharedPreferences.ProfileSP
import java.util.*

class Firestore {
    internal var context: Context? = null
    private val nodes: Long = 0
    private val isAdmin = false
    private var preference: ProfileSP? = null

    constructor()

    constructor(context: Context) {
        this.context = context
        preference = ProfileSP(context)
    }

    fun uploadCustomerDetails(
        listener: FirestoreInterfaces.OnUploadCustomerDetails,
        leadDetails: LeadDetails
    ) {

        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("leadList").document()

        leadDetails.key = dRef.id

        dRef.set(leadDetails)
            .addOnCompleteListener { listener.onDataUploaded() }
            .addOnFailureListener { e -> Log.e("TAG", "Error adding document", e) }
    }

    fun fetchUsersByUserType(
        listener: FirestoreInterfaces.OnFetchUsersList, location: String, userType: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList")
        var query = dRef.whereArrayContains("userType", userType)

        if (location != "All")
            query = query.whereEqualTo("location.$location", true)

        query = query.orderBy("userName", Query.Direction.ASCENDING)

        query.get().addOnSuccessListener { documentSnapshots ->
            val salesPersonList = ArrayList<UserDetails>()
            for (document in documentSnapshots) {
                val l = document.toObject(UserDetails::class.java)
                salesPersonList.add(l)
            }
            val userList = UserList(salesPersonList)
            listener.onListFetched(userList)
        }.addOnFailureListener {
            listener.onFail()
        }
    }

    fun updateLeadDetails(listener: FirestoreInterfaces.OnUpdateLead, updateLead: LeadDetails) {

        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("leadList").document(updateLead.key!!)

        dRef.update(
            "name", updateLead.name,
            "loanAmount", updateLead.loanAmount,
            "contactNumber", updateLead.contactNumber,
            "assignedTo", updateLead.assignedTo,
            "assignedToUId", updateLead.assignedToUId,
            "assignDate", updateLead.assignDate,
            "assignTime", updateLead.assignTime,
            "telecallerRemarks", updateLead.telecallerRemarks,
            "salesmanRemarks", updateLead.salesmanRemarks,
            "salesmanReason", updateLead.salesmanReason,
            "status", updateLead.status,
            "timeStamp", updateLead.timeStamp,
            "banks", updateLead.banks
        )

            .addOnSuccessListener { aVoid -> listener.onLeadUpdated() }
            .addOnFailureListener { e -> listener.onFailer() }
    }

    fun getOffers(
        fetchOffer: FirestoreInterfaces.FetchOffer,
        name: String, userType: String, singleItem: Boolean
    ) {

        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("offerList")

        if (userType != "Admin")
            query = query.whereArrayContains("userNames", name)

        query = query.orderBy("timestamp", Query.Direction.DESCENDING)

        if (singleItem)
            query = query.limit(1)

        query.get().addOnSuccessListener { documentSnapshots ->
            val offerDetails = ArrayList<OfferDetails>()
            for (document in documentSnapshots) {
                val l = document.toObject(OfferDetails::class.java)
                offerDetails.add(l)
                //                    Log.d("offerr", l.getTitle());
                //                    Log.d("offerr", l.getDescription());
            }

            fetchOffer.onSuccess(offerDetails)
        }
    }

/*
    fun downloadLeadList(
        listener: FirestoreInterfaces.OnFetchLeadList,
        lastLead: DocumentSnapshot?, filter: LeadFilter
    ) {

        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("leadList")




        if (filter.assigner != "All")
            query = query.whereEqualTo("assigner", filter.assigner)
        if (filter.assignee != "All")
            query = query.whereEqualTo("assignedTo", filter.assignee)
        if (filter.loanType != "All")
            query = query.whereEqualTo("loanType", filter.loanType)
        if (filter.status != "All")
            query = query.whereEqualTo("status", filter.status)
        if(userDetails!!.getuId().equals("hwn8jj23f8VGmnesvAkhDZsORl52"))
        {
            Log.d("Dipa","runhuihh")
            query = query.whereEqualTo("businessAssociateUid","rRhDdY8Vi5QlcUMZU5lnids68Ey2")

        }else{

            if (filter.location != "All")
                query = query.whereEqualTo("location", filter.location)

            if (!preference!!.userType.equals("Salesman") &&
                !preference!!.userType.equals("Admin") &&
                !preference!!.userType.equals(context?.getString(R.string.admin_and_salesman))
            ) {
                Log.d("sdcs", "sdcsd")
                if (filter.businessAssociateUId != null) {
                    query = query.whereEqualTo("businessAssociateUid", filter.businessAssociateUId)
                } else {
                    query = query.whereEqualTo(
                        "businessAssociateUploader", filter.businessAssociateUploader
                    )
                    Log.d("sdcs", filter.businessAssociateUploader.toString())
                }
            }
        }


        if (lastLead == null) {
            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(20)
        } else {
            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastLead)
                .limit(20)
        }

        query.get().addOnSuccessListener { documentSnapshots ->

            val leads = ArrayList<LeadDetails>()
            if (documentSnapshots.size() > 0) {
                for (document in documentSnapshots) {
                    val l = document.toObject(LeadDetails::class.java)
                    leads.add(l)
                    Log.d("uuuuu", l.name)
                }

                val lastVisible = documentSnapshots.documents[documentSnapshots.size() - 1]

                listener.onLeadAdded(leads, lastVisible)
            } else {
                listener.noLeads()
            }
        }.addOnFailureListener { exception ->
            listener.onFail()
        }
    }
*/

    fun getUsers(onGetUserDetails: FirestoreInterfaces.OnGetUserDetails, uId: String) {

        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val userDetails = documentSnapshot.toObject(UserDetails::class.java)
                onGetUserDetails.onSuccess(userDetails)
            } else {
                onGetUserDetails.fail()
            }

        }
    }

    fun setCurrentDeviceToken(deviceToken: String, uId: String) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.update("deviceToken", deviceToken)
    }

    fun setPassword(password: String, uId: String) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.update("userPassword", password)
    }

    fun startOffer(listener: FirestoreInterfaces.OnUploadOffer, details: OfferDetails) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("offerList").document()

        details.key = dRef.id

        dRef.set(details).addOnCompleteListener { listener.onSuccess() }.addOnFailureListener { e ->
            listener.onFail()
            Log.e("TAG", "Error adding document", e)
        }
    }

    fun removeAd(removeAd: FirestoreInterfaces.OnRemoveAd, details: OfferDetails) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("offerList").document(details.key)

        dRef.delete().addOnSuccessListener { removeAd.onSuccess() }
            .addOnFailureListener { removeAd.onFail() }
    }

    fun updateUserDetails(
        updateUser: FirestoreInterfaces.OnUpdateUser,
        number: String,
        uId: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.update("contactNumber", number)

            .addOnSuccessListener { updateUser.onSuccess() }
            .addOnFailureListener { updateUser.onFail() }
    }

    fun getBankList(list: FirestoreInterfaces.OnFetchBankList) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("bankList").document("banks")

        dRef.get().addOnSuccessListener { snapshot ->
            var arrList: ArrayList<*> = ArrayList<String>()
            arrList = (snapshot.get("bankName") as ArrayList<*>?)!!

            list.onSuccess(arrList)
        }
    }

    fun setWorkingLocation(location: String, uId: String) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.update("workingLocation", location)
    }

    fun getLeadDetails(onLeadDetails: FirestoreInterfaces.OnLeadDetails, uid: String) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("leadList").document(uid)

        dRef.get().addOnSuccessListener { documentSnapshot ->

            val leadDetails = documentSnapshot.toObject(LeadDetails::class.java)
            onLeadDetails.onSucces(leadDetails)
        }
    }

    fun downloadLeadList(
        listener: FirestoreInterfaces.OnFetchLeadList,
        lastLead: DocumentSnapshot?, filter: LeadFilter
    ) {

        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("leadList")

        Log.d(
            "Filter",
            "g + \n${filter.businessAssociateUploader}\n${filter.assigner}\n${filter.assignee}\n${filter.location}\n${filter.forwarder}"
        )

        if (filter.location != "All")
            query = query.whereEqualTo("location", filter.location)
        if (filter.assigner != "All")
            query = query.whereEqualTo("assignerUId", filter.assigner)
        if (filter.assignee != "All")
            query = query.whereEqualTo("assignedToUId", filter.assignee)
        if (filter.loanType != "All")
            query = query.whereEqualTo("loanType", filter.loanType)
        if (filter.status != "All")
            query = query.whereEqualTo("status", filter.status)
        if (filter.forwarder != "All")
            query = query.whereEqualTo("forwarderUId", filter.forwarder)


//        if (!preference!!.userType.equals("Salesman") &&
//            !preference!!.userType.equals("Admin") &&
//            !preference!!.userType.equals(context?.getString(R.string.admin_and_salesman))
//        ) {
//            if (filter.businessAssociateUId != null) {
//                query = query.whereEqualTo("businessAssociateUid", filter.businessAssociateUId)
//            } else {
//                query = query.whereEqualTo(
//                    "businessAssociateUploader", filter.businessAssociateUploader
//                )
//                Log.d("sdcs", filter.businessAssociateUploader.toString())
//            }
//        }

        if (lastLead == null) {
            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(20)
        } else {
            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastLead)
                .limit(20)
        }

        query.get().addOnSuccessListener { documentSnapshots ->

            val leads = ArrayList<LeadDetails>()
            if (documentSnapshots.size() > 0) {
                for (document in documentSnapshots) {
                    val l = document.toObject(LeadDetails::class.java)
                    leads.add(l)
                    Log.d("uuuuu", l.name)
                }

                val lastVisible = documentSnapshots.documents[documentSnapshots.size() - 1]

                listener.onLeadAdded(leads, lastVisible)
            } else {
                listener.noLeads()
            }
        }.addOnFailureListener { exception ->
            listener.onFail()
        }
    }

    fun updateDeviceToken(uId: String, token: String) {
        val db = FirebaseFirestore.getInstance()
        val dRef = db.collection("userList").document(uId)

        dRef.update("deviceToken", FieldValue.arrayUnion(token))
    }
}