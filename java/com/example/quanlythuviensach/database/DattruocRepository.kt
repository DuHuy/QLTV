package com.example.quanlythuviensach.database

import com.example.quanlythuviensach.models.Reservation
import com.example.quanlythuviensach.database.dao.DattruocDao
import kotlinx.coroutines.flow.Flow

class DattruocRepository(private val dattruocDao: DattruocDao) {
    fun getAllReservations(): Flow<List<Reservation>> {
        return dattruocDao.getAllDattruocs()
    }

    suspend fun getReservationById(maDatTruoc: Int): Reservation? {
        return dattruocDao.getDattruocById(maDatTruoc)
    }

    suspend fun insert(reservation: Reservation) {
        dattruocDao.insert(reservation)
    }

    suspend fun update(reservation: Reservation) {
        dattruocDao.update(reservation)
    }

    suspend fun delete(reservation: Reservation) {
        dattruocDao.delete(reservation)
    }
}