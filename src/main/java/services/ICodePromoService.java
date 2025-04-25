package services.CodePromo;

import models.CodePromo;
import java.sql.SQLException;
import java.util.List;

public interface ICodePromoService {
    void ajouterCodePromo(CodePromo codePromo) throws SQLException;
    List<CodePromo> getAllCodesPromo() throws SQLException;
    CodePromo getCodePromoByCode(String code) throws SQLException;
    void updateCodePromo(CodePromo codePromo) throws SQLException;
    void deleteCodePromo(int id) throws SQLException;
    void closeConnection() throws SQLException;
}
