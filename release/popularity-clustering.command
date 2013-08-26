#!/bin/sh
java -Xmx1500M -DentityExpansionLimit=10000000 -Djava.library.path=lib -cp rio.jar:lib/owlapi-api-3.4.3.jar:lib/commons-math-2.1.jar:lib/oppl2.jar:lib/commons-io-2.4.jar:FaCTpp-OWLAPI-3.3-v1.6.0.jar org.coode.proximitymatrix.cluster.commandline.WrappingEquivalenceClassesAgglomerateAll $1 $2
