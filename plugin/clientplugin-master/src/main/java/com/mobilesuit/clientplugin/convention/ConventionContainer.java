package com.mobilesuit.clientplugin.convention;

import com.intellij.openapi.project.Project;
import com.mobilesuit.clientplugin.setting.CommitConventionSettingsState;
import com.mobilesuit.clientplugin.setting.PropertyOption;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Getter
public class ConventionContainer {

    private String headerConvention;
    private String bodyConvention;
    private String footerConvention;
    private String identifiedConvention;
    private String conventionMessage;
    private boolean allowEmptyBody;
    private boolean allowEmptyFooter;
    private List<String> sortedProperties;
    private List<String> identifiers;
    private List<PropertyOption> sortedPropertyOptions;
    private List<String> userInputs;
    private List<Boolean> userInputFlags;
    private int previousIndex;
    private int currentIndex;
    private int propertyCnt;
    private int currentPlaceholderOffset;
    private int currentPlaceholderLength;

    private ConventionContainer() {}
    private static final ConventionContainer INSTANCE = new ConventionContainer();
    public static ConventionContainer getInstance() {
        return INSTANCE;
    }
    public void loadConvention(Project project) {
        CommitConventionSettingsState settingsState = CommitConventionSettingsState.getInstance(project);

        headerConvention = settingsState.getHeaderConvention();
        bodyConvention = settingsState.getBodyConvention();
        footerConvention = settingsState.getFooterConvention();
        allowEmptyBody = settingsState.isAllowEmptyBody();
        allowEmptyFooter = settingsState.isAllowEmptyFooter();

        conventionMessage = headerConvention + "\n\n"
                + bodyConvention + "\n\n"
                + footerConvention;

        List<Integer> offsets = settingsState.getProperties().stream()
                .map(e -> conventionMessage.indexOf(e))
                .toList();
        propertyCnt = offsets.size();
        List<Integer> sortedIndices = IntStream.range(0, propertyCnt)
                .boxed()
                .sorted(Comparator.comparingInt(offsets::get))
                .toList();
        List<Integer> orders = IntStream.range(0, propertyCnt)
                .map(sortedIndices::indexOf)
                .boxed()
                .toList();
        sortedProperties = Arrays.asList(new String[propertyCnt]);
        sortedPropertyOptions = Arrays.asList(new PropertyOption[propertyCnt]);
        IntStream.range(0, propertyCnt).forEach(i -> {
            int order = orders.get(i);
            sortedProperties.set(order, settingsState.getProperties().get(i));
            sortedPropertyOptions.set(order, settingsState.getPropertyOptions().get(i).clone());
        });

        String[] identifierArray = new String[propertyCnt];
        for (int i = 0; i < propertyCnt; i++) {
            identifierArray[i] = UUID.randomUUID().toString();
        }
        identifiers = new ArrayList<>(List.of(identifierArray));

        identifiedConvention = conventionMessage;
        IntStream.range(0, propertyCnt).forEach(i -> {
            identifiedConvention = identifiedConvention.replace(sortedProperties.get(i), identifiers.get(i));
        });

        String[] initialInputs = new String[propertyCnt];
        Arrays.fill(initialInputs, "");
        userInputs = new ArrayList<>(List.of(initialInputs));

        Boolean[] initialInputFlags = new Boolean[propertyCnt];
        Arrays.fill(initialInputFlags, Boolean.FALSE);
        userInputFlags = new ArrayList<>(List.of(initialInputFlags));

        currentIndex = 0;

        currentPlaceholderOffset = identifiedConvention.indexOf(identifiers.get(0));
        currentPlaceholderLength = sortedProperties.get(0).length();
    }

    public void increaseCurrentIndex() {
        previousIndex = currentIndex;
        currentIndex = (++currentIndex) % userInputs.size();
    }

    public void decreaseCurrentIndex() {
        previousIndex = currentIndex;
        currentIndex = (--currentIndex + propertyCnt) % propertyCnt;
    }

    public String getCurrentUserInput() {
        return userInputs.get(currentIndex);
    }

    public void setCurrentUserInput(String userInput) {
        userInputs.set(currentIndex, userInput);
    }

    public void setCurrentUserInputFlag(Boolean flag) {
        userInputFlags.set(currentIndex, flag);
    }

    public void setPreviousUserInputFlag(Boolean flag) {
        userInputFlags.set(previousIndex, flag);
    }

    public String getCurrentPrompt() {
        return sortedProperties.get(currentIndex) + " 를 작성 중 입니다.";
    }

    public String getCurrentProperty() {
        return sortedProperties.get(currentIndex);
    }

    public PropertyOption getCurrentPropertyOption() {
        return sortedPropertyOptions.get(currentIndex);
    }

    public void updateConventionMessage() {
        conventionMessage = identifiedConvention;
        for (int i = 0; i < propertyCnt; i++) {
            String identifier = identifiers.get(i);
            String userInput = userInputs.get(i);
            Boolean userInputFlag = userInputFlags.get(i);

            String replacement;
            if (userInput != null && userInputFlag) {
                replacement = userInput;
            } else {
                replacement = sortedProperties.get(i);
            }

            if (i == currentIndex) {
                currentPlaceholderOffset = conventionMessage.indexOf(identifiers.get(i));
                currentPlaceholderLength = replacement.length();
            }

            conventionMessage = conventionMessage.replace(identifier, replacement);
        }
    }
}
