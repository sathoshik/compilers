package ece351.f;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.util.ExaminableProperties;
import ece351.util.TestInputs351;
import ece351.util.Tuple;


@RunWith(Parameterized.class)
public final class TestTechnologyMapper {

	private final File f;

	public TestTechnologyMapper(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.formulaFiles("cse.*");
	}

	@Test
	public void mapper() throws IOException {
		final String inputSpec = f.getAbsolutePath();
		final CommandLine c = new CommandLine("-p", "-o4", inputSpec);
		final String input = c.readInputSpec();
		System.out.println("processing " + inputSpec);
		System.out.println("input: ");
		System.out.println(input);

		// parse from the file to construct first AST
		final FProgram original = FParser.parse(c);
		final FProgram simplified = original.simplify();
    	System.out.println("simplified:");
    	System.out.println(simplified.toString());

		// check examinable sanity
		ExaminableProperties.checkAllUnary(original);
		ExaminableProperties.checkAllUnary(simplified);
		ExaminableProperties.checkAllBinary(original, simplified);

		// render the output
		final StringWriter sw = new StringWriter();
		final TechnologyMapper tm = new TechnologyMapper(new PrintWriter(sw));
		tm.render(simplified);
		sw.close();
		final String graphviz = sw.toString();
		System.out.println("output:");
		System.out.println(graphviz);

		final String s = File.separator;
		final String path = inputSpec.replace(s + "f" + s, s + "f" + s + "student.out" + s + "graph" + s).replace(".f", ".dot");
		final File f = new File(path);
		f.getParentFile().mkdirs();
		final PrintWriter pw = new PrintWriter(new FileWriter(f));
		pw.write(graphviz);
		pw.close();

		// Transform the generated .dot graphviz file back to FProgram.
		final Tuple<FProgram,Integer> t = GraphvizToF.graphvizToF(path);
		final FProgram fprogramFromGraphviz = t.x.simplify();
		assertTrue("FProgram to circuit inversion not working", simplified.equivalent(fprogramFromGraphviz));

		// check examinable sanity
		ExaminableProperties.checkAllUnary(fprogramFromGraphviz);
		ExaminableProperties.checkAllBinary(original, fprogramFromGraphviz);

		// check against staff output
		final String staffPath = path.replace("student.out", "staff.out");
		final Tuple<FProgram,Integer> t2 = GraphvizToF.graphvizToF(staffPath);
		final FProgram staffGraphF = t2.x.simplify();
		System.out.println("Staff FProgram from circuit:");
		System.out.println(staffGraphF);
		assertTrue("Student soln not equivalent to staff soln", staffGraphF.equivalent(fprogramFromGraphviz));
	
		// compare gate count
		final int gateDiff = t2.y - t.y;
		score += gateDiff;
		System.out.println("Staff gates:     " + t2.y);
		System.out.println("Student gates:   " + t.y);
		System.out.println("Gate diff:       " + gateDiff);
		System.out.println("Cumulative diff: " + score);
		System.out.println("Notes: ");
		System.out.println("   - 0 is par for the course");
		System.out.println("   - negative values indicate you used more gates");
		System.out.println("   - positive values indicate you used fewer gates (i.e., you have a better solution)");
		System.out.println();

		// success!
	}

	private int score = 0;
}
