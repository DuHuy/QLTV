package com.example.quanlythuviensach.database

import com.example.quanlythuviensach.models.BorrowRecord
import com.example.quanlythuviensach.database.dao.MuontraDao
import kotlinx.coroutines.flow.Flow

class MuontraRepository(private val muontraDao: MuontraDao) {
    fun getAllBorrowRecords(): Flow<List<BorrowRecord>> {
        return muontraDao.getAllMuontras()
    }

    suspend fun getBorrowRecordById(maMuon: Int): BorrowRecord? {
        return muontraDao.getMuontraById(maMuon)
    }

    suspend fun insert(borrowRecord: BorrowRecord) {
        muontraDao.insert(borrowRecord)
    }

    suspend fun update(borrowRecord: BorrowRecord) {
        muontraDao.update(borrowRecord)
    }

    suspend fun delete(borrowRecord: BorrowRecord) {
        muontraDao.delete(borrowRecord)
    }
}