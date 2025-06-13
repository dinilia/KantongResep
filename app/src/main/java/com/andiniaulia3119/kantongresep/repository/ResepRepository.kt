package com.andiniaulia3119.kantongresep.repository

import com.andiniaulia3119.kantongresep.model.MessageResponse
import com.andiniaulia3119.kantongresep.model.Resep
import com.andiniaulia3119.kantongresep.model.ResepCreate
import com.andiniaulia3119.kantongresep.network.Api

class ResepRepository {

    suspend fun getResep(email: String): List<Resep> =
        Api.service.getAllData().filter { it.userEmail == email }

    suspend fun addResep(data: ResepCreate): MessageResponse =
        Api.service.addData(data)

    suspend fun deleteResep(id: Int): MessageResponse =
        Api.service.deleteData(id)

    suspend fun updateResep(id: Int, data: ResepCreate): MessageResponse =
        Api.service.updateResep(id, data)
}


