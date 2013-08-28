RIO
===

The Regularity Inspector for Ontologies (RIO) is a framework for inspecting regularities in OWL ontologies. 

RIO is open source and is available under the LGPL License. RIO is using [OWLAPI](http://owlapi.sourceforge.net/) for processing an ontology and [OPPL 2](http://oppl2.sourceforge.net/) for expressing the regularities. A standalone Java tool is used for computing the regularities, which are saved in an XML file. The RIO plugin for Prot&eacute;g&eacute; 4 can be used for loading and viewing the regularities.


## Example Usage

The computation and inspection of the regularities in an ontology can be done in the following two steps:

### Step 1: Computing regularities
Use the [clustering tool](https://github.com/elenimikro/rio/releases) first to compute regularties for an ontology by running the ./popularity-clustering.command script in the command line as follows:

```
./popularity-clustering.command <saveResultsFile.xml> <ontology>
```

### Step 2: Inspecting regularities

To inspect the regularities use the [pattern induction plugin for Prot&eacute;g&eacute;](http://sourceforge.net/projects/oppl2/files/).

Simply drop org.coode.patterns.induction.jar in the plugins folder of Prot&eacute;g&eacute;. The name of the plugin is pattern induction. The main view for inspecting the irregularities can be found in:

```
Window->Views->Misc Views->Cluster Graph View
```

__NOTE:__ RIO plugin needs the latest version of [the OPPL plugin installed in Prot&eacute;g&eacute;](http://sourceforge.net/projects/oppl2/files/).


