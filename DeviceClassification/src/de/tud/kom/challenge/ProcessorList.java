package de.tud.kom.challenge;

import java.util.Vector;

import de.tud.kom.challenge.processors.AutocorrelationProcessor;
import de.tud.kom.challenge.processors.AverageLevelOnTimeProcessor;
import de.tud.kom.challenge.processors.AverageLevelProcessor1;
import de.tud.kom.challenge.processors.AverageLevelProcessor2;
import de.tud.kom.challenge.processors.AveragePowerProcessor;
import de.tud.kom.challenge.processors.AvgOnTimeProcessor;
import de.tud.kom.challenge.processors.AvgPowerInWProcessor;
import de.tud.kom.challenge.processors.DFTProcessor;
import de.tud.kom.challenge.processors.DeviceOnCountProcessor;
import de.tud.kom.challenge.processors.EqualOverDayProcessor;
import de.tud.kom.challenge.processors.FeatureProcessor;
import de.tud.kom.challenge.processors.FourierProcessor;
import de.tud.kom.challenge.processors.FrequencyProcessor;
import de.tud.kom.challenge.processors.HistogramProcessor;
import de.tud.kom.challenge.processors.IntervalDescriptionProcessor;
import de.tud.kom.challenge.processors.IntervalProcessor;
import de.tud.kom.challenge.processors.IsWeekendProcessor;
import de.tud.kom.challenge.processors.MaxPowerInWProcessor;
import de.tud.kom.challenge.processors.MaxPowerProcessor;
import de.tud.kom.challenge.processors.OnOffCharacteristicsProcessor;
import de.tud.kom.challenge.processors.OnOffTimeProcessor;
import de.tud.kom.challenge.processors.PeakLevelProcessor;
import de.tud.kom.challenge.processors.PeakSlopeProcessor;
import de.tud.kom.challenge.processors.PowerConsumptionProcessor;
import de.tud.kom.challenge.processors.PowerOscProcessor;
import de.tud.kom.challenge.processors.ProcessorAdapter;
import de.tud.kom.challenge.processors.ProcessorFeX;
import de.tud.kom.challenge.processors.ProcessorXmt;
import de.tud.kom.challenge.processors.SimpleMetricsProcessor;
import de.tud.kom.challenge.processors.StartTimeProcessor;
import de.tud.kom.challenge.processors.StddevOnTimeProcessor;
import de.tud.kom.challenge.processors.SummarizedPowerConsumptionProcessor;
import de.tud.kom.challenge.processors.TimeOfDayProcessor;
import de.tud.kom.challenge.processors.TimeRegionProcessor;

public class ProcessorList {

	private static Vector<FeatureProcessor> processors = new Vector<FeatureProcessor>();
	private static boolean inited = false;
	
	public static void init() {
		processors.clear();
		
		// Add all required processing modules
		processors.add(new AutocorrelationProcessor());
		processors.add(new AveragePowerProcessor());
		processors.add(new FourierProcessor());
		processors.add(new HistogramProcessor());
		processors.add(new IntervalProcessor());
		processors.add(new MaxPowerProcessor());
		processors.add(new OnOffCharacteristicsProcessor());
		processors.add(new PowerOscProcessor());
		processors.add(new SimpleMetricsProcessor());
		processors.add(new StartTimeProcessor());

		processors.add(new PeakLevelProcessor());
		processors.add(new AverageLevelOnTimeProcessor());
		processors.add(new PeakSlopeProcessor());
		processors.add(new EqualOverDayProcessor());
		processors.add(new AverageLevelProcessor1());
		processors.add(new AverageLevelProcessor2());
		processors.add(new IsWeekendProcessor());
		processors.add(new IntervalDescriptionProcessor());

		processors.add(new ProcessorAdapter(new PowerConsumptionProcessor()));
		processors.add(new ProcessorAdapter(
				new SummarizedPowerConsumptionProcessor()));
		processors.add(new ProcessorAdapter(new TimeRegionProcessor()));
		processors.add(new ProcessorAdapter(new TimeOfDayProcessor()));
		processors.add(new ProcessorAdapter(new FrequencyProcessor()));
		processors.add(new ProcessorAdapter(new DFTProcessor()));

		processors.add(new ProcessorAdapter(new OnOffTimeProcessor()));

		processors.add(new ProcessorAdapter(new AvgOnTimeProcessor()));
		processors.add(new ProcessorAdapter(new AvgPowerInWProcessor()));
		processors.add(new ProcessorAdapter(new DeviceOnCountProcessor()));
		processors.add(new ProcessorAdapter(new MaxPowerInWProcessor()));
		processors.add(new ProcessorAdapter(new StddevOnTimeProcessor()));

		//processors.add(new ProcessorXmt());
		processors.add(new ProcessorFeX());
		inited = true;
	}
	
	public static Vector<FeatureProcessor> getProcessors(){
		if (!inited) init();
		return processors;
	}
}
