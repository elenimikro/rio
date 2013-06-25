package org.coode.rclustering;

import org.rosuda.JRI.Rengine;

public class JRITest {
	public static void main(String[] args) {
		// new R-engine
		Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

		// print a random number from uniform distribution
		// re.eval ("clus_out <- hclust (r_matrix, method = '"+method+"')");)
		System.out.println(re.eval("runif(1)").asDouble());

		// done...
		re.end();

	}

}
