package services.Programme;

import models.Programme;

import java.sql.SQLException;
import java.util.List;

public interface IProgrammeService {
    void ajouterProgramme(Programme programme) throws SQLException;
    List<Programme> getAllProgrammes() throws SQLException;
    Programme getProgrammeById(int id) throws SQLException;
    void updateProgramme(Programme programme) throws SQLException;
    void deleteProgramme(int id) throws SQLException;

}
