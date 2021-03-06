package net.micropact.aea.tu.page.objectImport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.entellitrak.ApplicationException;
import com.entellitrak.PageExecutionContext;
import com.entellitrak.page.PageController;
import com.entellitrak.page.Response;

import net.micropact.aea.core.importExport.ComponentDataImporter;
import net.micropact.aea.core.importExport.IImportLogic;
import net.micropact.aea.utility.ImportExportUtility;
import net.micropact.aea.utility.Utility;

/**
 * This page ingests an XML file of user-configured information surrounding the Template Utility.
 * The XML file is one generated by by the {@link net.micropact.aea.tu.page.objectExport.ObjectExportController}.
 *
 * @author zmiller
 * @see net.micropact.aea.tu.page.objectExport.ObjectExportController
 */
public class ObjectImportController implements PageController {

    @Override
    public Response execute(final PageExecutionContext etk)
            throws ApplicationException {

        return ComponentDataImporter.performExecute(etk, new IImportLogic() {

            /**
             * Imports data into the TU Replacement Variable reference data list.
             *
             * @param document XML document representing user-configurable data related to the Template Utility.
             */
            private void importReplacementVariables(final Document document) {
                final List<Map<String, String>> replacementVariables =
                        ImportExportUtility.getTable(document, "T_TU_REPLACEMENT_VARIABLE");
                for(final Map<String, String> replacementVariable : replacementVariables){

                    final List<Map<String, Object>> matchingReplacementVariables = etk.createSQL("SELECT ID FROM t_tu_replacement_variable WHERE c_code = :code")
                            .setParameter("code", replacementVariable.get("C_CODE"))
                            .fetchList(); /*ID*/
                    if(matchingReplacementVariables.size() == 0){
                        //Insert
                        etk.createSQL(Utility.isSqlServer(etk) ? "INSERT INTO t_tu_replacement_variable(c_code, c_description, etk_end_date, c_name, c_sql, etk_start_date) VALUES(:c_code, :c_description, CONVERT(DATE, :etk_end_date, 101), :c_name, :c_sql, CONVERT(DATE, :etk_start_date, 101))"
                                : "INSERT INTO t_tu_replacement_variable(id, c_code, c_description, etk_end_date, c_name, c_sql, etk_start_date) VALUES(OBJECT_ID.NEXTVAL, :c_code, :c_description, TO_DATE(:etk_end_date, 'MM/DD/YYYY'), :c_name, :c_sql, TO_DATE(:etk_start_date, 'MM/DD/YYYY'))")
                                .setParameter("c_code", replacementVariable.get("C_CODE"))
                                .setParameter("c_sql", replacementVariable.get("C_SQL"))
                                .setParameter("c_description", replacementVariable.get("C_DESCRIPTION"))
                                .setParameter("etk_start_date", replacementVariable.get("ETK_START_DATE"))
                                .setParameter("etk_end_date", replacementVariable.get("ETK_END_DATE"))
                                .setParameter("c_name", replacementVariable.get("C_NAME"))
                                .execute();
                    }else{
                        //Update
                        etk.createSQL(Utility.isSqlServer(etk) ? "UPDATE t_tu_replacement_variable SET c_code = :c_code, c_description = :c_description, etk_end_date = CONVERT(DATE, :etk_end_date, 101), c_name = :c_name, c_sql = :c_sql, etk_start_date = CONVERT(DATE, :etk_start_date, 101) WHERE id =:id"
                                : "UPDATE t_tu_replacement_variable SET c_code = :c_code, c_description = :c_description, etk_end_date = TO_DATE(:etk_end_date, 'MM/DD/YYYY'), c_name = :c_name, c_sql = :c_sql, etk_start_date = TO_DATE(:etk_start_date, 'MM/DD/YYYY') WHERE id =:id")
                                .setParameter("id", matchingReplacementVariables.get(0).get("ID"))
                                .setParameter("c_code", replacementVariable.get("C_CODE"))
                                .setParameter("c_sql", replacementVariable.get("C_SQL"))
                                .setParameter("c_description", replacementVariable.get("C_DESCRIPTION"))
                                .setParameter("etk_start_date", replacementVariable.get("ETK_START_DATE"))
                                .setParameter("etk_end_date", replacementVariable.get("ETK_END_DATE"))
                                .setParameter("c_name", replacementVariable.get("C_NAME"))
                                .execute();
                    }
                }
            }

            /**
             * Imports data for the TU Replacement Section reference data list.
             *
             * @param document XML Document containing user-configurable Template Utility data.
             */
            private void importReplacementSections(final Document document) {
                final List<Map<String, String>> replacementSections =
                        ImportExportUtility.getTable(document, "T_TU_REPLACEMENT_SECTION");
                for(final Map<String, String> replacementSection : replacementSections){

                    final List<Map<String, Object>> matchingSections = etk.createSQL("SELECT ID FROM t_tu_replacement_section WHERE c_code = :code")
                            .setParameter("code", replacementSection.get("C_CODE"))
                            .fetchList(); /*ID*/
                    if(matchingSections.size() == 0){
                        //Insert
                        etk.createSQL(Utility.isSqlServer(etk) ? "INSERT INTO t_tu_replacement_section(c_code, etk_end_date, c_name, etk_start_date, c_text) VALUES(:c_code, CONVERT(DATE, :etk_end_date, 101), :c_name, CONVERT(DATE, :etk_start_date, 101), :c_text)"
                                : "INSERT INTO t_tu_replacement_section(id, c_code, etk_end_date, c_name, etk_start_date, c_text) VALUES(OBJECT_ID.NEXTVAL, :c_code, TO_DATE(:etk_end_date, 'MM/DD/YYYY'), :c_name, TO_DATE(:etk_start_date, 'MM/DD/YYYY'), :c_text)")
                                .setParameter("etk_start_date", replacementSection.get("ETK_START_DATE"))
                                .setParameter("c_text", replacementSection.get("C_TEXT"))
                                .setParameter("c_code", replacementSection.get("C_CODE"))
                                .setParameter("etk_end_date", replacementSection.get("ETK_END_DATE"))
                                .setParameter("c_name", replacementSection.get("C_NAME"))
                                .execute();
                    }else{
                        //Update
                        etk.createSQL(Utility.isSqlServer(etk) ? "UPDATE t_tu_replacement_section SET c_code = :c_code, etk_end_date = CONVERT(DATE, :etk_end_date, 101), c_name = :c_name, etk_start_date = CONVERT(DATE, :etk_start_date, 101), c_text = :c_text WHERE id = :id"
                                : "UPDATE t_tu_replacement_section SET c_code = :c_code, etk_end_date = TO_DATE(:etk_end_date, 'MM/DD/YYYY'), c_name = :c_name, etk_start_date = TO_DATE(:etk_start_date, 'MM/DD/YYYY'), c_text = :c_text WHERE id =:id")
                                .setParameter("id", matchingSections.get(0).get("ID"))
                                .setParameter("etk_start_date", replacementSection.get("ETK_START_DATE"))
                                .setParameter("c_text", replacementSection.get("C_TEXT"))
                                .setParameter("c_code", replacementSection.get("C_CODE"))
                                .setParameter("etk_end_date", replacementSection.get("ETK_END_DATE"))
                                .setParameter("c_name", replacementSection.get("C_NAME"))
                                .execute();
                    }
                }
            }

            @Override
            public void performImport(final InputStream inputStream) throws ApplicationException{
                try {
                    final Document document = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(new InputSource(new InputStreamReader(inputStream)));

                    importReplacementVariables(document);
                    importReplacementSections(document);
                } catch (final SAXException | IOException | ParserConfigurationException e) {
                    throw new ApplicationException(e);
                }
            }
        });
    }
}
