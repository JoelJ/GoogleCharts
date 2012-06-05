package com.attask.jenkins;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * User: joeljohnson
 * Date: 6/4/12
 * Time: 12:04 PM
 */
public class PlotTest {
	@Test
	public void testParse() {
		String csvContents =
				"api,count,total,avg\n" +
				"/tile,3.0,0.5,0.18\n" +
				"/timesheet,2.0,0.0,0.01";

		List<Plot> plots = Plot.parsePlots(csvContents, "#1", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots.size());

		assertEquals("#1", plots.get(0).getX());
		assertEquals("Build", plots.get(0).getXLabel());
		assertEquals("0.18", plots.get(0).getY());
		assertEquals("/tile", plots.get(0).getYLabel());

		assertEquals("#1", plots.get(1).getX());
		assertEquals("Build", plots.get(1).getXLabel());
		assertEquals("0.01", plots.get(1).getY());
		assertEquals("/timesheet", plots.get(1).getYLabel());
	}

	@Test
	public void testMapify() {
		String csvContents1 =
				"api,count,total,avg\n" +
				"/tile,3.0,0.5,0.18\n" +
				"/timesheet,2.0,0.0,0.01";
		List<Plot> plots1 = Plot.parsePlots(csvContents1, "#1", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots1.size());

		String csvContents2 =
				"api,count,total,avg\n" +
				"/tile,4.0,1.5,1.18\n" +
				"/timesheet,3.0,1.0,1.01";
		List<Plot> plots2 = Plot.parsePlots(csvContents2, "#2", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots2.size());

		String csvContents3 =
				"api,count,total,avg\n" +
				"/tile,0.0,0.3,0.01\n" +
				"/timesheet,1.0,1.0,1.0";
		List<Plot> plots3 = Plot.parsePlots(csvContents3, "#3", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots3.size());

		Map<String, Map<String, Plot>> map = Plot.mapify(Arrays.asList(plots1, plots2, plots3), Pattern.compile(".*"));
		assertEquals(3, map.size());
	}

	@Test
	public void testJavascriptify() {
		String csvContents1 =
				"api,count,total,avg\n" +
						"/tile,3.0,0.5,0.18\n" +
						"/timesheet,2.0,0.0,0.01";
		List<Plot> plots1 = Plot.parsePlots(csvContents1, "#1", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots1.size());

		String csvContents2 =
				"api,count,total,avg\n" +
						"/tile,4.0,1.5,1.18\n" +
						"/timesheet,3.0,1.0,1.01";
		List<Plot> plots2 = Plot.parsePlots(csvContents2, "#2", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots2.size());

		String csvContents3 =
				"api,count,total,avg\n" +
						"/tile,0.0,0.3,0.01\n" +
						"/timesheet,1.0,1.0,1.0";
		List<Plot> plots3 = Plot.parsePlots(csvContents3, "#3", "Build", "avg", "api");
		assertEquals("Should have exactly 2 elements", 2, plots3.size());

		Map<String, Map<String, Plot>> map = Plot.mapify(Arrays.asList(plots1, plots2, plots3), Pattern.compile(".*"));
		assertEquals(3, map.size());

		String javascript = Plot.toJavascriptFormat(map);
		assertEquals(
				"['Build','/tile','/timesheet'],"+
				"['#1','0.18','0.01'],"+
				"['#2','1.18','1.01'],"+
				"['#3','0.01','1.0']"
				, javascript);
	}
}
