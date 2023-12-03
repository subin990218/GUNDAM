package com.mobilesuit.clientplugin.setting;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Tag("Row")
public class KeywordRow implements Cloneable {
    @Attribute("Keyword")
    public String keyword;
    @Attribute("Description")
    public String description;

    @Override
    public KeywordRow clone() {
        try {
            return (KeywordRow) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
