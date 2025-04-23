package services;



import models.Consultation;
import java.sql.SQLException;
import java.util.List;

public interface IServiceConsultation {

    void addConsultation(Consultation consultation) throws SQLException;

    List<Consultation> getAllConsultations() throws SQLException;

    Consultation getConsultationById(int id) throws SQLException;

    void updateConsultation(Consultation consultation) throws SQLException;

    boolean deleteConsultation(int id) throws SQLException;

    List<Consultation> getConsultationsByStatus(String status) throws SQLException;
}

