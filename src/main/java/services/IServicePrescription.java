package src.main.java.services;

import Models.Prescription;
import java.sql.SQLException;
import java.util.List;

public interface IServicePrescription {

    boolean addPrescription(Prescription prescription) throws SQLException;

    boolean updatePrescription(Prescription prescription) throws SQLException;

    boolean deletePrescription(int id) throws SQLException;

    Prescription getPrescriptionByConsultationId(int consultationId) throws SQLException;
}
