package models;

public class Produit {
    private int id;
    private String nomProduit;
    private double prix;
    private String description;
    private String categorie;
    private String image;
    private int stock;

    // Constructeurs
    public Produit() {
    }

    public Produit(String nomProduit, double prix, String description,
                   String categorie, String image, int stock) {
        this.nomProduit = nomProduit;
        this.prix = prix;
        this.description = description;
        this.categorie = categorie;
        this.image = image;
        this.stock = stock;
    }

    public Produit(int i, String aucunProduit, int i1, String s, String s1, String s2, int i2) {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nomProduit='" + nomProduit + '\'' +
                ", prix=" + prix +
                ", stock=" + stock +
                '}';
    }
}