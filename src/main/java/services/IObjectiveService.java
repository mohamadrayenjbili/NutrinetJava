package services;

import models.Objective;
import java.sql.SQLException;
import java.util.List;

public interface IObjectiveService {
    // CREATE - Ajouter un objectif
    void ajouterObjective(Objective objective) throws SQLException;

    // READ - Récupérer tous les objectifs
    List<Objective> getAllObjectives() throws SQLException;

    // READ - Trouver un objectif par son nom
    Objective getObjectiveByName(String name) throws SQLException;

    // UPDATE - Mettre à jour un objectif
    void updateObjective(Objective objective) throws SQLException;

    // DELETE - Supprimer un objectif
    void deleteObjective(int id) throws SQLException;
}
