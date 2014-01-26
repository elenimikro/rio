package experiments;

/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi - initial API and implementation
 ******************************************************************************/
import java.io.File;

import org.coode.atomicdecomposition.commandline.AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll;
import org.coode.atomicdecomposition.commandline.AtomicDecompositionEquivalenceClassesAgglomerateAll;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/** @author eleni */
public class Runner {
    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static void main(final String[] args) throws OWLOntologyCreationException {
        final String uri = "file://";
        String atomic1 = "atomic1";
        String atomic2 = "atomic2";
        // String wrapping = "popularity";
        // String structural = "structural";
        String baseFolder = "/eclipse-workspace/similarity/eswc-ontologies/";
        String[] input = new String[] { "efo-v3.owl", "travel/c23.owl",
                "SNOMEDclinicalFindingPresentModule.owl", "amino-acid-original.owl",
                "kupkb.owl", "ho.owl" };
        String current = atomic2;
        for (String s : input) {
            System.out.println("Runner.main() " + current + "\t" + s);
            final String[] args2 = args(uri, baseFolder, current, s);
            File f = new File(args2[0]);
            if (!f.exists()) {
                AtomicDecompositionEquivalenceClassesAgglomerateAll.main(args2);
            }
        }
        current = atomic1;
        for (String s : input) {
            System.out.println("Runner.main() " + current + "\t" + s);
            final String[] args2 = args(uri, baseFolder, current, s);
            File f = new File(args2[0]);
            if (!f.exists()) {
                AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll
                        .main(args2);
            }
        }
        // current = structural;
        // for (String s : input) {
        // System.out.println("Runner.main() " + current + "\t" + s);
        // final String[] args2 = args(uri, baseFolder, current, s);
        // File f = new File(args2[0]);
        // if (!f.exists()) {
        // StructuralDifferenceWrappingEquivalenceClassesAgglomerateAll.main(args2);
        // }
        // }
        // current = wrapping;
        // for (String s : input) {
        // System.out.println("Runner.main() " + current + "\t" + s);
        // final String[] args2 = args(uri, baseFolder, current, s);
        // File f = new File(args2[0]);
        // if (!f.exists()) {
        // WrappingEquivalenceClassesAgglomerateAll.main(args2);
        // }
        // }
    }

    /** @param uri
     *            uri
     * @param base
     *            base
     * @param current
     *            current
     * @param s
     *            s
     * @return arguments */
    public static String[] args(final String uri, final String base,
            final String current, final String s) {
        return new String[] {
                base + s.replace("/", "_").replace(".owl", "") + "_" + current
                        + "_results.xml", uri + base + s };
    }
}
