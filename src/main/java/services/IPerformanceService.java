package services;

import models.Performance;
import java.sql.SQLException;
import java.util.List;

public interface IPerformanceService {
    // CREATE - Ajouter une performance
    void ajouterPerformance(Performance performance) throws SQLException;

    // READ - Récupérer toutes les performances
    List<Performance> getAllPerformances() throws SQLException;

    // READ - Récupérer les performances par objectif
    List<Performance> getPerformancesByObjectiveId(int objectiveId) throws SQLException;

    // READ - Récupérer une performance par son ID
    Performance getPerformanceById(int id) throws SQLException;

    // UPDATE - Mettre à jour une performance
    void updatePerformance(Performance performance) throws SQLException;

    // DELETE - Supprimer une performance
    void deletePerformance(int id) throws SQLException;
}