package services.commentaire;

public interface ICommentaireLikeService {
    /**
     * Vérifie si un utilisateur a déjà liké un commentaire
     * @param commentaireId ID du commentaire
     * @param userId ID de l'utilisateur
     * @return true si l'utilisateur a liké, false sinon
     */
    boolean hasUserLiked(int commentaireId, int userId);

    /**
     * Ajoute ou retire un like selon l'état actuel
     * @param commentaireId ID du commentaire
     * @param userId ID de l'utilisateur
     */
    void toggleLike(int commentaireId, int userId);

    /**
     * Récupère le nombre total de likes pour un commentaire
     * @param commentaireId ID du commentaire
     * @return nombre de likes
     */
    int getLikeCount(int commentaireId);
}
