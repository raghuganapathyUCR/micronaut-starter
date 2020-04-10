package io.micronaut.starter.command;

import io.micronaut.starter.ContextFactory;
import io.micronaut.starter.Options;
import io.micronaut.starter.feature.AvailableFeatures;
import io.micronaut.starter.feature.Feature;
import io.micronaut.starter.feature.FeatureContext;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListFeatures {

    private final AvailableFeatures availableFeatures;
    private final Options options;
    private final MicronautCommand command;
    private final ContextFactory contextFactory;

    public ListFeatures(AvailableFeatures availableFeatures,
                        Options options,
                        MicronautCommand command,
                        ContextFactory contextFactory) {
        this.availableFeatures = availableFeatures;
        this.options = options;
        this.command = command;
        this.contextFactory = contextFactory;
    }

    void output(ConsoleOutput consoleOutput) {
        FeatureContext featureContext = contextFactory.createFeatureContext(availableFeatures,
                Collections.emptyList(),
                command,
                options.getLanguage(),
                options.getBuildTool(),
                options.getTestFramework());
        CommandContext commandContext = contextFactory.createCommandContext(null, featureContext, ConsoleOutput.NOOP);

        List<Feature> defaultFeatures = commandContext.getFeatures().getFeatures();
        List<Feature> allFeatures = availableFeatures.getFeatures().collect(Collectors.toList());

        int width = allFeatures.stream()
                .map(Feature::getName)
                .max(Comparator.comparingInt(String::length))
                .map(String::length).get() + 8;

        allFeatures.sort(Comparator.comparing(Feature::getName));

        consoleOutput.out("Available Features");
        consoleOutput.out("@|blue (+)|@ denotes the feature is included by default");
        consoleOutput.out("  " + String.format("%1$-" + width + "s", "Name") + "Description");
        consoleOutput.out("  " + new String(new char[width-2]).replace("\0", "-") + "  ---------------");

        for (Feature feature: allFeatures) {
            if (defaultFeatures.contains(feature)) {
                String name = feature.getName() + " (+)";
                consoleOutput.out("@|blue   " + String.format("%1$-" + width + "s", name) + "description" + "|@");
            } else {
                consoleOutput.out("  " + String.format("%1$-" + width + "s", feature.getName()) + "description");
            }
        }
    }
}
