package com.ibm.hrl.scenoptic.debug;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class DebugSpec {
	protected List<DebugLocation> locations;

	public enum DebugLocation {
		INC_PHASE,
		CELL_PROP,
		INC_PROP,
		FULL_PROP,
		LISTENER,
		ERROR,
		LISTENER_WORKAROUND,
	}

	protected Map<String, DebugLocation> locationNames = Map.ofEntries(
			entry("inc-phase", DebugLocation.INC_PHASE),
			entry("cell-prop", DebugLocation.CELL_PROP),
			entry("inc-prop", DebugLocation.INC_PROP),
			entry("full-prop", DebugLocation.FULL_PROP),
			entry("listener", DebugLocation.LISTENER),
			entry("error", DebugLocation.ERROR),
			entry("listener-workaround", DebugLocation.LISTENER_WORKAROUND)
	);

	public DebugSpec(DebugLocation... locations) {
		if (locations == null)
			locations = new DebugLocation[]{};
		this.locations = List.of(locations);
	}

	public DebugSpec(String locations, boolean verbose) {
		final String[] locationSpecs = locations.toLowerCase().split(",");
		if (Arrays.asList(locationSpecs).contains("all"))
			this.locations = List.of(DebugLocation.values());
		else {
			List<String> badSpecs = Arrays.stream(locationSpecs)
					.filter(spec -> !spec.isEmpty() && !locationNames.containsKey(spec))
					.collect(Collectors.toList());
			if (!badSpecs.isEmpty())
				System.out.println("WARNING: bad debug specifications: " + badSpecs);
			this.locations = Arrays.stream(locationSpecs)
					.map(locationNames::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}
		if (verbose)
			System.out.println("Debugging: " + this.locations);
	}

	public boolean debug(DebugLocation... current) {
		return Arrays.stream(current).anyMatch(locations::contains);
	}

	public void println(DebugLocation current, String text) {
		if (locations.contains(current))
			System.out.println(text);
	}

	public void println(DebugLocation[] current, String text) {
		if (debug(current))
			System.out.println(text);
	}
}
