package com.mobilesuit.clientplugin.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DefaultCommitConventionSettings {

    public static final List<String> PROPERTIES = new ArrayList<>();
    public static final List<PropertyOption> PROPERTY_OPTIONS = new ArrayList<>();
    public static final String HEADER_CONVENTION = "Gitmoji CommitType : Subject";
    public static final String BODY_CONVENTION = "Description";
    public static final String FOOTER_CONVENTION = "IssueType : #IssueNumber";
    public static final boolean ALLOW_EMPTY_BODY = true;
    public static final boolean ALLOW_EMPTY_FOOTER = true;

    static {
        PROPERTIES.addAll(Arrays.asList("Gitmoji", "CommitType", "Subject", "Description", "IssueType", "IssueNumber"));
        PropertyOption gitmojiOption = PropertyOption.builder()
                .allowOnlyKeywordsOnList(true)
                .keywordRows(new ArrayList<>(Arrays.asList(
                        new KeywordRow("✨", "새 기능"),
                        new KeywordRow("\uD83D\uDC1B", "버그 수정"),
                        new KeywordRow("✅", "테스트 추가/수정"),
                        new KeywordRow("\uD83C\uDFA8", "코드의 구조/형태 개선"),
                        new KeywordRow("⚡\uFE0F", "성능 개선"),
                        new KeywordRow("\uD83D\uDCA9", "똥싼 코드"),
                        new KeywordRow("\uD83C\uDF7B", "술 취해서 쓴 코드"),
                        new KeywordRow("\uD83D\uDD25", "코드/파일 삭제"),
                        new KeywordRow("\uD83D\uDE91", "긴급 수정"),
                        new KeywordRow("\uD83D\uDCDD", "문서 추가/수정"),
                        new KeywordRow("\uD83D\uDC84", "UI/스타일 파일 추가/수정"),
                        new KeywordRow("\uD83C\uDF89", "프로젝트 시작"),
                        new KeywordRow("\uD83D\uDD12", "보안 이슈 수정"),
                        new KeywordRow("\uD83D\uDD16", "릴리즈/버전 태그"),
                        new KeywordRow("\uD83D\uDC9A", "CI 빌드 수정"),
                        new KeywordRow("\uD83D\uDCCC", "특정 버전 의존성 고정"),
                        new KeywordRow("\uD83D\uDC77", "CI 빌드 시스템 추가/수정"),
                        new KeywordRow("\uD83D\uDCC8", "분석, 추적 코드 추가/수정"),
                        new KeywordRow("♻\uFE0F", "코드 리팩토링"),
                        new KeywordRow("➕", "의존성 추가"),
                        new KeywordRow("➖", "의존성 제거"),
                        new KeywordRow("\uD83D\uDD27", "구성 파일 추가/삭제"),
                        new KeywordRow("\uD83D\uDD28", "개발 스크립트 추가/수정"),
                        new KeywordRow("\uD83C\uDF10", "국제화/현지화"),
                        new KeywordRow("⏪", "변경 내용 되돌리기"),
                        new KeywordRow("\uD83D\uDD00", "브랜치 합병"),
                        new KeywordRow("\uD83D\uDCE6", "컴파일된 파일 추가/수정"),
                        new KeywordRow("\uD83D\uDC7D", "외부 API 변화로 인한 수정"),
                        new KeywordRow("\uD83D\uDE9A", "리소스 이동, 이름 변경"),
                        new KeywordRow("\uD83D\uDCC4", "라이센스 추가/수정"),
                        new KeywordRow("\uD83D\uDCA1", "주석 추가/수정"),
                        new KeywordRow("\uD83D\uDDC3", "데이버베이스 관련 수정"),
                        new KeywordRow("\uD83D\uDD0A", "로그 추가/수정"),
                        new KeywordRow("\uD83D\uDE48", ".gitignore 추가/수정")
                )))
                .build();

        PropertyOption commitTypeOption = PropertyOption.builder()
                        .allowOnlyKeywordsOnList(true)
                        .keywordRows(new ArrayList<>(Arrays.asList(
                                new KeywordRow("Feat", "새로운 기능을 추가하는 경우"),
                                new KeywordRow("Fix", "버그를 고친 경우"),
                                new KeywordRow("Docs", "문서를 수정한 경우"),
                                new KeywordRow("Style", "코드 포맷 변경, 세미콜론 누락, 코드 수정이 없는 경우"),
                                new KeywordRow("Refactor", "코드 리팩토링"),
                                new KeywordRow("Test", "테스트 코드. 리팩토링 테스트 코드를 추가했을 때"),
                                new KeywordRow("Chore", "빌드 업무 수정, 패키지 매니저 수정"),
                                new KeywordRow("Design", "CSS 등 사용자가 UI 디자인을 변경했을 때"),
                                new KeywordRow("Rename", "파일명(or 폴더명) 을 수정한 경우"),
                                new KeywordRow("Remove", "파일을 삭제한 경우")
                        )))
                        .build();
        PropertyOption subjectOption = PropertyOption.builder()
                .allowOnlyKeywordsOnList(false)
                .keywordRows(new ArrayList<>())
                .build();
        PropertyOption descriptionOption = PropertyOption.builder()
                .allowOnlyKeywordsOnList(false)
                .keywordRows(new ArrayList<>())
                .build();
        PropertyOption issueTypeOption = PropertyOption.builder()
                        .allowOnlyKeywordsOnList(true)
                        .keywordRows(new ArrayList<>(Arrays.asList(
                                new KeywordRow("Fixes", "이슈 수정 중인 경우 (아직 해결되지 않은 경우)"),
                                new KeywordRow("Resolves", "이슈를 해결한 경우"),
                                new KeywordRow("Ref", "참고할 이슈가 있는 경우"),
                                new KeywordRow("Related to", "해당 커밋과 관련된 이슈가 있는 경우 (아직 해결되지 않은 경우)")
                        )))
                        .build();
        PropertyOption issueNumberOption = PropertyOption.builder()
                        .allowOnlyKeywordsOnList(false)
                        .keywordRows(new ArrayList<>())
                        .build();
        PROPERTY_OPTIONS.addAll(Arrays.asList(gitmojiOption, commitTypeOption, subjectOption, descriptionOption, issueTypeOption, issueNumberOption));
    }

    public static CommitConventionSettingsState getDefaultSettings() {
        return CommitConventionSettingsState.builder()
                .properties(new ArrayList<>(PROPERTIES))
                .propertyOptions(new ArrayList<>(PROPERTY_OPTIONS.stream().map(PropertyOption::clone).toList()))
                .headerConvention(HEADER_CONVENTION)
                .bodyConvention(BODY_CONVENTION)
                .footerConvention(FOOTER_CONVENTION)
                .allowEmptyBody(ALLOW_EMPTY_BODY)
                .allowEmptyFooter(ALLOW_EMPTY_FOOTER)
                .build();
    }
}
