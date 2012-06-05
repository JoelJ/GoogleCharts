package com.attask.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.*;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: joeljohnson
 * Date: 6/1/12
 * Time: 1:45 PM
 */
public class GoogleChartPublisher extends Recorder {
	private final String csvFile;
	private final String yAxisLabel;
	private final String yAxisValue;

	@DataBoundConstructor
	public GoogleChartPublisher(String csvFile, String yAxisLabel, String yAxisValue) {
		this.csvFile = csvFile;
		this.yAxisLabel = yAxisLabel;
		this.yAxisValue = yAxisValue;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		if(csvFile == null || csvFile.isEmpty()) {
			listener.error("CSV File is required");
			return false;
		}

		File file = new File(build.getArtifactsDir(), csvFile);
		if(!file.exists()) {
			listener.error(file.getAbsolutePath() + " does not exist!");
			return false;
		}

		String csvContents = FileUtils.readFileToString(file);
		List<Plot> plots = Plot.parsePlots(csvContents, build.getFullDisplayName(), "Build", yAxisValue, yAxisLabel);
		build.addAction(new GoogleChartAction(build, plots));
		return true;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	public String getCsvFile() {
		return csvFile;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public String getyAxisValue() {
		return yAxisValue;
	}

	@Extension
	public static class ResultsOverrideRecorderDescriptor extends BuildStepDescriptor<Publisher> {
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Generate Google Chart";
		}
	}
}
