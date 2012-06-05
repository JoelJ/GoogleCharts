package com.attask.jenkins;

import com.google.common.collect.ImmutableList;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Build;
import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: joeljohnson
 * Date: 6/1/12
 * Time: 2:00 PM
 */
public class GoogleChartAction implements Action {
	private final String buildID;
	private final List<Plot> plots;

	public GoogleChartAction(Run build, List<Plot> plots) {
		this.buildID = build.getExternalizableId();
		this.plots = Collections.unmodifiableList(new ArrayList<Plot>(plots));
	}

	public Run getBuild() {
		return Build.fromExternalizableId(buildID);
	}

	public List<Plot> getPlots() {
		return plots;
	}

	public String getJavascriptData(String buildCount, String pattern) {
		int build = Integer.parseInt(buildCount);
		int currentBuild = 0;

		ImmutableList.Builder<List<Plot>> builder = ImmutableList.builder();
		Run run = getBuild();
		while(run != null && (currentBuild++) < build) {
			GoogleChartAction action = run.getAction(this.getClass());
			if(action != null) {
				builder.add(action.getPlots());
			}
			run = run.getPreviousBuild();
		}
		Map<String, Map<String, Plot>> mapPlot = Plot.mapify(builder.build(), Pattern.compile(pattern));
		return Plot.toJavascriptFormat(mapPlot);

//		return "['Month',   'Bolivia', 'Ecuador', 'Madagascar', 'Papua New Guinea', 'Rwanda'],\n" +
//				"['2004/05',    165,      938,         522,             998,           450],\n" +
//				"['2005/06',    135,      1120,        599,             1268,          288],\n" +
//				"['2006/07',    157,      1167,        587,             807,           397],\n" +
//				"['2007/08',    139,      1110,        615,             968,           215],\n" +
//				"['2008/09',    136,      691,         629,             1026,          366]";
	}

	public String getIconFileName() {
		return "icon.png";
	}

	public String getDisplayName() {
		return "Google Chart";
	}

	public String getUrlName() {
		return "googlechart";
	}
}
