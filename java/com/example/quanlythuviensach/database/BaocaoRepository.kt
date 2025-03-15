package com.example.quanlythuviensach.database

import com.example.quanlythuviensach.models.Report
import com.example.quanlythuviensach.database.dao.BaocaoDao
import kotlinx.coroutines.flow.Flow

class BaocaoRepository(private val baocaoDao: BaocaoDao) {
    fun getAllReports(): Flow<List<Report>> {
        return baocaoDao.getAllBaocaos()
    }

    suspend fun getReportById(maBaoCao: Int): Report? {
        return baocaoDao.getBaocaoById(maBaoCao)
    }

    suspend fun insert(report: Report) {
        baocaoDao.insert(report)
    }

    suspend fun update(report: Report) {
        baocaoDao.update(report)
    }

    suspend fun delete(report: Report) {
        baocaoDao.delete(report)
    }
}