package services.commentaire;

import models.Commentaire;
import java.util.List;

public interface ICommentaireService {
    int ajouterCommentaire(Commentaire commentaire);
    void supprimerCommentaire(int id);
    void modifierCommentaire(Commentaire commentaire);
    Commentaire getCommentaireById(int id);
    List<Commentaire> getCommentairesByProgramme(int programmeId);
}