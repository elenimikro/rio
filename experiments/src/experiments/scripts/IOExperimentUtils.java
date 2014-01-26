package experiments.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLEntity;

import experiments.ClusterResultsExploitationUtils;
import experiments.ExperimentHelper;

/** @author eleni */
public class IOExperimentUtils {
    private static String results_base = "";

    /** @param substring
     *            substring
     * @param clustering_type
     *            clustering_type
     * @param model
     *            model
     * @return file
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError
     * @throws TransformerException
     *             TransformerException
     * @throws FileNotFoundException
     *             FileNotFoundException */
    public static File saveResults(String substring, String clustering_type,
            ClusterDecompositionModel<OWLEntity> model)
            throws ParserConfigurationException, TransformerFactoryConfigurationError,
            TransformerException, FileNotFoundException {
        String xmlname = results_base + clustering_type + "-"
                + substring.replaceAll(".owl", ".xml");
        File xml = new File(xmlname);
        PrintStream out = new PrintStream(new File(xmlname + ".txt"));
        ClusterResultsExploitationUtils.printGeneralisationStats(model, out, xmlname);
        ExperimentHelper.getClusteringMatrix();
        Utils.saveToXML(model, xml);
        return xml;
    }
}
