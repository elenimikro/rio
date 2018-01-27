/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package org.coode.utils.owl.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.coode.owl.wrappers.OWLOntologyManagerBasedOWLAxiomProvider;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.LeastCommonSubsumer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.xml.sax.SAXException;

/** @author eleni */
public class LeastCommonSubsumerTest {
    /**
     * @param args args
     */
    public static void main(String[] args) {
        // ToStringRenderer.getInstance().setRenderer(new
        // ManchesterSyntaxRenderer());
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        try {
            ontologyManager.loadOntology(IRI.create(args[1]));
            Set<Set<OWLEntity>> clusters = Utils.readFromXML(
                LeastCommonSubsumerTest.class.getResourceAsStream(args[0]), ontologyManager);
            Set<String> names = new HashSet<>();
            Set<String> rootNames = new HashSet<>(Arrays.asList(null, "Thing", "topObjectProperty",
                "topDataProperty", "topAnnotationProperty"));
            for (Set<OWLEntity> set : clusters) {
                if (!set.isEmpty()) {
                    OWLAxiomProvider axiomProvider =
                        new OWLOntologyManagerBasedOWLAxiomProvider(ontologyManager);
                    LeastCommonSubsumer<OWLEntity, ?> lcs = LeastCommonSubsumer.build(set,
                        axiomProvider, ontologyManager.getOWLDataFactory());
                    if (lcs != null) {
                        OWLObject x = lcs.get(set);
                        System.out.println(String.format("lcs for %s is %s", set, x));
                        createName(x.toString(), names, rootNames);
                    } else {
                        createName(null, names, rootNames);
                        System.out.println(String.format("No lcs for %s", set));
                    }
                }
            }
            System.out.println(names);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createName(String string, Set<String> names, Set<String> rootNames) {
        if (!rootNames.contains(string)) {
            if (names.contains(string)) {
                String[] split = string.split("__");
                if (split != null && split.length >= 2) {
                    createName(String.format("%s__%d", split[0],
                        Integer.valueOf(Integer.parseInt(split[1]) + 1)), names, rootNames);
                } else {
                    createName(String.format("%s__1", string), names, rootNames);
                }
            } else {
                names.add(string);
            }
        } else {
            createName("cluster__1", names, rootNames);
        }
    }
}
