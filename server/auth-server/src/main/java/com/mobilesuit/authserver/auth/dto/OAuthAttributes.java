package com.mobilesuit.authserver.auth.dto;

import com.mobilesuit.authserver.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Override
    public String toString() {
        return "OAuthAttributes{" +
                "attributes=" + attributes +
                ", nameAttributeKey='" + nameAttributeKey + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        System.out.println("userNameAttributeName : "+userNameAttributeName );
        if ("kakao".equals(registrationId)) {
            System.out.println("카카오 로그인 요청");
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String,Object> newAttributes = new HashMap<>(attributes);

        newAttributes.put("type","GOOGLE");

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(newAttributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        Map<String,Object> newAttributes = new HashMap<>(attributes); // unmodifiable Map to modifiable (Boxing?)

        // OAuth2SuccessHandler 에서 email attribute 가 없는 예외를 처리하기 위한 분기를 제거하기 위해, 수동으로 attribute 를 추가함 어느 것이 더 옳은가?

        newAttributes.put("email",(String)kakaoAccount.get("email"));
        newAttributes.put("name",(String)kakaoProfile.get("nickname"));
        newAttributes.put("type","KAKAO");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .attributes(newAttributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        /*MemberImage memberImage = MemberImage.builder()
                .fileUrl(picture).build();*/
        return Member.builder()
                //.nickname(name)
                .email(email+"@"+attributes.get("type"))
                //.memberImage(memberImage)
                .build();
    }
}