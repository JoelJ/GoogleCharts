package com.attask.jenkins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.regex.Pattern;

/**
 * User: joeljohnson
 * Date: 6/1/12
 * Time: 2:44 PM
 */
public class Plot {
	private final String x;
	private final String y;
	private final String xLabel;
	private final String yLabel;
	private static final Plot NULL = new Plot(null, null, null, null);

	public Plot(String x, String xLabel, String y, String yLabel) {
		this.x = x;
		this.y = y;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public String getXLabel() {
		return xLabel;
	}

	public String getYLabel() {
		return yLabel;
	}

	@Override
	public String toString() {
		return "Plot {" + getX() + " (" + getXLabel() + "), " + getY() + "(" + getYLabel() + ")}";
	}

	public static List<Plot> parsePlots(String csvContents, String xValue, String xLabel, String yColumn, String yColumnLabelColumn) {
		ImmutableList.Builder<Plot> builder = ImmutableList.builder();

		String[] split = csvContents.split("\n");

		String headerString = split[0];
		List<String> header = Arrays.asList(headerString.split(","));
		int y = header.indexOf(yColumn);
		int yColumnIndex = header.indexOf(yColumnLabelColumn);

		for(int i = 1; i < split.length; i++) {
			String line = split[i];
			String[] lineSplit = line.split(",");
			builder.add(new Plot(xValue, xLabel, lineSplit[y], lineSplit[yColumnIndex]));
		}
		return builder.build();
	}

	public static Map<String, Map<String, Plot>> mapify(List<List<Plot>> list, Pattern regexFilter) {
		Map<String, Map<String, Plot>> result = new LinkedHashMap<String, Map<String, Plot>>();
		for (List<Plot> plots : list) {
			for (Plot plot : plots) {
				String innerKey = plot.getYLabel();
				if(regexFilter.matcher(innerKey).find()) {
					String outerKey = plot.getX();
					Map<String, Plot> plotList = result.get(outerKey);
					if(plotList == null) {
						plotList = new LinkedHashMap<String, Plot>();
						result.put(outerKey, plotList);
					}
					plotList.put(innerKey, plot);
				}
			}
		}
		return result;
	}


	private static String join(Collection<? extends Object> toJoin, String separator, String surroundWith) {
		StringBuilder result = new StringBuilder();
		Object[] objects = toJoin.toArray();
		for(int i = 0; i < objects.length-1; i++) {
			Object object = objects[i];
			result.append(surroundWith).append(object).append(surroundWith).append(separator);
		}
		result.append(surroundWith).append(objects[objects.length-1]).append(surroundWith);
		return result.toString();
	}

	public static String toJavascriptFormat(Map<String, Map<String, Plot>> mapPlot) {
		normalizeMap(mapPlot);
		List<String> header = new LinkedList<String>();
		StringBuilder values = new StringBuilder();
		boolean found = false;
		for (String outerKey : mapPlot.keySet()) {
			values.append("[");
			Map<String, Plot> value = mapPlot.get(outerKey);
			values.append("'").append(outerKey).append("'");
			Plot previous = null;
			for (String innerKey : value.keySet()) {
				Plot plot = value.get(innerKey);
				String yValue = "0.0";
				if(plot != NULL && plot.getY() != null) {
					if(!found) {
						header.add(plot.getXLabel());
						found = true;
					}
					if(!header.contains(innerKey)) {
						header.add(innerKey);
					}
					yValue = plot.getY();
					previous = plot;
				} else {
					if(previous != null) {
						yValue = previous.getY();
					}
				}
				values.append(",").append(yValue);
			}
			values.append("],\n");
		}
		//remove trailing comma
		values.deleteCharAt(values.length()-2);

		String headerString = "[" + join(header, ",", "'") + "],\n";
		System.out.println(headerString + values);
		return headerString + values;
	}

	private static void normalizeMap(Map<String, Map<String, Plot>> mapPlot) {
		ImmutableList.Builder<String> allKeys = ImmutableList.builder();
		for (Map<String, Plot> stringPlotMap : mapPlot.values()) {
			allKeys.addAll(stringPlotMap.keySet());
		}

		List<Map<String, Plot>> values = new ArrayList<Map<String, Plot>>(mapPlot.values());
		Collections.reverse(values);
		for (Map<String, Plot> stringPlotMap : values) {
			for (String key : allKeys.build()) {
				if (!stringPlotMap.containsKey(key)) {
					stringPlotMap.put(key, Plot.NULL);
				}
			}
		}
	}
}
