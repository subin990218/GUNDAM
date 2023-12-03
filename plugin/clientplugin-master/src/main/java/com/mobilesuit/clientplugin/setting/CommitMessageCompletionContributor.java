package com.mobilesuit.clientplugin.setting;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.mobilesuit.clientplugin.convention.ConventionContainer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class CommitMessageCompletionContributor extends CompletionContributor {
    public CommitMessageCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            public void addCompletions(@NotNull CompletionParameters parameters,
                                       @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet result) {

                CompletionResultSet resultSet = result.withRelevanceSorter(
                        CompletionSorter.emptySorter().weigh(new LookupElementWeigher("priorityFilter") {
                            @Nullable
                            @Override
                            public Comparable weigh(@NotNull LookupElement element) {
                                if (element instanceof PrioritizedLookupElement<?> prioritized) {
                                    return prioritized.getPriority() > 99 ? prioritized.getPriority() : null;
                                }
                                return null;
                            }
                        })
                );

                PsiFile psiFile = parameters.getOriginalFile();
                VirtualFile virtualFile = psiFile.getVirtualFile();

                if (!(virtualFile != null && "gundam_convention_temp.txt".equals(virtualFile.getName()))) {
                    return;
                }

                ConventionContainer conventionUtil = ConventionContainer.getInstance();
                String propertyName = conventionUtil.getCurrentProperty();
                PropertyOption propertyOption = conventionUtil.getCurrentPropertyOption();
                propertyOption.getKeywordRows().forEach(e -> resultSet.addElement(createLookupElement(e.keyword, e.description, propertyName, 100.0)));
                resultSet.stopHere();
            }
        });
    }

    private LookupElement createLookupElement(String keyword, String description, String property, double priority) {
        return PrioritizedLookupElement.withPriority(
                LookupElementBuilder.create(keyword)
                        .withTailText("  " + description, true)
                        .withTypeText(property)
                , priority);
    }
}