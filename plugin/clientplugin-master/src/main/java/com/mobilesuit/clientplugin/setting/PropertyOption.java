package com.mobilesuit.clientplugin.setting;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Tag("PropertyOption")
public class PropertyOption implements Cloneable {
    @Attribute("AllowOnlyKeywordsOnList")
    private boolean allowOnlyKeywordsOnList;

    @XCollection(style = XCollection.Style.v2)
    private List<KeywordRow> keywordRows = new ArrayList<>();

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        PropertyOption that = (PropertyOption) o;
//
//        boolean b1 = allowOnlyKeywordsOnList == that.allowOnlyKeywordsOnList;
//        if (keywordRows == null) {
//            return b1 && that.keywordRows == null;
//        }
//        boolean b2 = keywordRows.equals(that.keywordRows);
//        return b1 && b2;
//    }

    @Override
    public PropertyOption clone() {
        try {
            return (PropertyOption) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
