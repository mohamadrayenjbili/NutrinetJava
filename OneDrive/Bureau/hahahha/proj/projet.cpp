#include "projet.h"
#include <QSqlQuery>
#include <QDate>
#include <QPixmap>
#include <QBuffer>
#include <QDebug>

projet::projet()
{
    id=0;
}
projet::projet(int id,int salle,QString nom_du_client,QString nom_du_projet,QString email,QString budget,QDate date_debut,QDate date_fin)
{
    this->id=id;this->nom_du_client=nom_du_client;this->nom_du_projet=nom_du_projet;this->email=email;
    this->budget=budget;this->date_debut=date_debut;this->date_fin=date_fin;
    this->salle=salle;
}
bool projet::ajouterproj()
{
    // Vérifier si les champs obligatoires sont vides
    if (nom_du_client.isEmpty() || nom_du_projet.isEmpty() || budget.isEmpty() || email.isEmpty() || date_debut.isNull() || date_fin.isNull()) {
        qDebug() << "Erreur: Veuillez remplir tous les champs obligatoires.";
        return false;
    }

    // Vérifier si le nom du client et du projet ne contiennent que des caractères
    QRegExp regex("^[a-zA-Z\\s]*$");
    if (!nom_du_client.contains(regex) || !nom_du_projet.contains(regex)) {
        qDebug() << "Erreur: Le nom du client et du projet ne peuvent contenir que des lettres et des espaces.";
        return false;
    }

    // Vérifier si la date de début est inférieure à la date de fin
    if (date_debut >= date_fin) {
        qDebug() << "Erreur: La date de début doit être antérieure à la date de fin.";
        return false;
    }

    // Vérifier si l'email est valide
    QRegularExpression emailRegex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    if (!emailRegex.match(email).hasMatch()) {
        qDebug() << "Erreur: L'adresse email n'est pas valide.";
        return false;
    }

    // Vérifier si la salle est positive
    if (salle < 0) {
        qDebug() << "Erreur: La salle doit être un nombre positif.";
        return false;
    }

    QSqlQuery query;
    query.prepare("INSERT INTO PROJET (salle, nom_du_client, nom_du_projet,email, budget, date_debut, date_fin) "
                  "VALUES (:salle, :nom_du_client, :nom_du_projet,:email, :budget, :date_debut, :date_fin)");

    query.bindValue(":salle", salle);
    query.bindValue(":nom_du_client", nom_du_client);
    query.bindValue(":nom_du_projet", nom_du_projet);
    query.bindValue(":email", email);
    query.bindValue(":budget", budget);
    query.bindValue(":date_debut", date_debut);
    query.bindValue(":date_fin", date_fin);

    return query.exec();
}


/*
bool projet::supprimerproj(int id)
{
    QSqlQuery query;
    query.prepare("DELETE FROM PROJET WHERE ID = :id");
    query.bindValue(":id", id);
    return query.exec();
}
*/
bool projet::supprimerproj(int id)
{
    QSqlQuery query;
    QString res=QString::number(id);

    query.prepare("Delete from PROJET where ID= :id");
    query.bindValue(":id",id);
    return query.exec();
}
QSqlQueryModel * projet::afficher()
{
    QSqlQueryModel * model=new QSqlQueryModel();
    model->setQuery("select * from PROJET");
    model->setHeaderData(0,Qt::Horizontal,QObject::tr(("id")));
    model->setHeaderData(1,Qt::Horizontal,QObject::tr(("nom_du_client")));
    model->setHeaderData(2,Qt::Horizontal,QObject::tr(("nom_du_projet")));
    model->setHeaderData(3,Qt::Horizontal,QObject::tr(("budget")));
    model->setHeaderData(4,Qt::Horizontal,QObject::tr(("salle")));
    model->setHeaderData(5,Qt::Horizontal,QObject::tr(("date_debut")));
    model->setHeaderData(6,Qt::Horizontal,QObject::tr(("date_fin")));
    model->setHeaderData(7,Qt::Horizontal,QObject::tr(("email")));
            return model;

}

bool projet::modifierproj(int id)
{

    // Vérifier si les champs obligatoires sont vides
       if (nom_du_client.isEmpty() || nom_du_projet.isEmpty() || budget.isEmpty() || email.isEmpty() || date_debut.isNull() || date_fin.isNull()) {
           qDebug() << "Erreur: Veuillez remplir tous les champs obligatoires.";
           return false;
       }

       // Vérifier si le nom du client et du projet ne contiennent que des caractères
       QRegExp regex("^[a-zA-Z\\s]*$");
       if (!nom_du_client.contains(regex) || !nom_du_projet.contains(regex)) {
           qDebug() << "Erreur: Le nom du client et du projet ne peuvent contenir que des lettres et des espaces.";
           return false;
       }

       // Vérifier si la date de début est inférieure à la date de fin
       if (date_debut >= date_fin) {
           qDebug() << "Erreur: La date de début doit être antérieure à la date de fin.";
           return false;
       }

       // Vérifier si l'email est valide
       QRegularExpression emailRegex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
       if (!emailRegex.match(email).hasMatch()) {
           qDebug() << "Erreur: L'adresse email n'est pas valide.";
           return false;
       }

       // Vérifier si la salle est positive
       if (salle < 0) {
           qDebug() << "Erreur: La salle doit être un nombre positif.";
           return false;
       }


    QSqlQuery query;

    query.prepare("UPDATE PROJET SET NOM_DU_PROJET = :nom_du_projet, NOM_DU_CLIENT = :nom_du_client, BUDGET = :email, SALLE = :salle, DATE_DEBUT = :date_debut, DATE_FIN = :date_fin, EMAIL = :budget_val WHERE id = :id");
    query.bindValue(":id", id);
    query.bindValue(":nom_du_projet", nom_du_projet);
    query.bindValue(":nom_du_client", nom_du_client);
    query.bindValue(":budget_val", budget); // Utilisez un nom distinct pour la valeur du budget
    query.bindValue(":salle", salle);
    query.bindValue(":date_debut", date_debut);
    query.bindValue(":date_fin", date_fin);
    query.bindValue(":email", email);
    return query.exec();
}

QSqlQueryModel* projet::afficherPid(int id)
{
    QSqlQueryModel* model = new QSqlQueryModel();
    QSqlQuery query;
    query.prepare("SELECT * FROM PROJET WHERE ID = :id");
    query.bindValue(":id", id);
    query.exec();
    model->setQuery(query);

    // Set header data
    model->setHeaderData(0, Qt::Horizontal, QObject::tr("Id"));
    model->setHeaderData(1, Qt::Horizontal, QObject::tr("Nom_du_client"));
    model->setHeaderData(2, Qt::Horizontal, QObject::tr("Nom_du_projet"));
    model->setHeaderData(3, Qt::Horizontal, QObject::tr("budget"));
    model->setHeaderData(4, Qt::Horizontal, QObject::tr("salle"));
    model->setHeaderData(5, Qt::Horizontal, QObject::tr("Date_debut"));
    model->setHeaderData(6, Qt::Horizontal, QObject::tr("Date_fin"));
    model->setHeaderData(7, Qt::Horizontal, QObject::tr("email"));

    return model;
}

