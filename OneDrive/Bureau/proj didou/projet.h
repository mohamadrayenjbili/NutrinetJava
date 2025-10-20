#ifndef EMPLOYE_H
#define EMPLOYE_H
#include <QWidget>
#include <QDate>
#include <QString>
#include <QSqlQuery>
#include <QSqlQueryModel>
#include <QPixmap>
#include <QPainter>
#include <QStyledItemDelegate>
class projet
{
private:
    int id,salle;
    QString nom_du_client,nom_du_projet,email,budget;
    QDate date_debut,date_fin;
public:
    projet();
    projet(int,int,QString,QString,QString,QString,QDate,QDate);
    bool ajouterproj();
    bool supprimerproj(int id);
    bool modifierproj(int id);
    QSqlQueryModel* afficher();
    QSqlQueryModel* afficherPid(int id);
};


#endif // projet_H
